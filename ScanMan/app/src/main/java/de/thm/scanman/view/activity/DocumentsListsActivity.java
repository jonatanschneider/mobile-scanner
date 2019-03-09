package de.thm.scanman.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import org.apache.commons.io.FileUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.model.Stats;
import de.thm.scanman.model.User;
import de.thm.scanman.view.fragment.ViewPagerItemFragment;

import static de.thm.scanman.persistence.FirebaseDatabase.userDAO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.thm.scanman.persistence.FirebaseDatabase.documentDAO;
import static de.thm.scanman.persistence.FirebaseDatabase.userDAO;

public class DocumentsListsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout tabBar;
    private ViewPager viewPager;
    private FloatingActionButton addFab;
    private User user;
    private Set<String> documentIDs = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_lists);

        getSupportActionBar().setElevation(0f);

        tabBar = findViewById(R.id.fragment_container);
        viewPager = findViewById(R.id.viewpager);
        setUpPagerAndTabs();

        addFab = findViewById(R.id.add_fab);
        addFab.setOnClickListener(
            view -> {
                Intent i = new Intent(this, EditDocumentActivity.class);
                i.setData(Uri.parse(String.valueOf(EditDocumentActivity.FIRST_VISIT)));
                startActivity(i);
            });

        userDAO.get(FirebaseAuth.getInstance().getUid()).observe(this, u -> user = u);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent caller = getIntent();
        if (caller != null) {
            System.out.println("LOLoL" + Intent.ACTION_VIEW.equals(caller.getAction()));
            // Add document to shared documents
            Uri data = caller.getParcelableExtra("data");
            if (data == null) return;           // stop process when data is null

            List<String> params = data.getPathSegments();
            if (params.size() != 2) return;     // stop process when data is not valid

            String ownerID = params.get(0);
            String documentID = params.get(1);

            // stop process when document is already added this session
            if (documentIDs.contains(documentID)) return;
            documentIDs.add(documentID);
            if (ownerID.equals(FirebaseAuth.getInstance().getUid())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Sie sind bereits der Besitzer des Dokumentes");
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> { });
                builder.show();
                return;
            }
            Document doc = new Document();
            doc.setOwnerId(ownerID);
            doc.setId(documentID);
            if (    user != null
                    && user.getSharedDocuments().stream().noneMatch(d -> d.getId().equals(documentID))
                    && documentDAO.addSharedDocument(doc)) {
                Toast.makeText(this, R.string.added_new_document, Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Sie sind dem Dokument bereits beigetreten");
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> { });
            builder.show();
        } else {
            System.out.println("Intent is null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.documents_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stats:
                new StatsTask(this).execute(user);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // setup and style tab bar
    private void setUpPagerAndTabs(){
        tabBar.setTabTextColors(ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTab));

        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);

        tabBar.addOnTabSelectedListener(this);
        tabBar.setupWithViewPager(viewPager);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] pageTitles = {
                getApplicationContext().getString(R.string.all_documents),
                getApplicationContext().getString(R.string.my_documents),
                getApplicationContext().getString(R.string.shared_documents)
        };

        public TabPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public int getCount() {
            return pageTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new ViewPagerItemFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("idx", position);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }

    private class StatsTask extends AsyncTask<User, Void, Stats> {
        private Context context;

        public StatsTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected Stats doInBackground(User... users) {

            return new Stats(users[0]);
        }

        @Override
        protected void onPostExecute(Stats stats) {
            AlertDialog.Builder dialogB = new AlertDialog.Builder(context);
            dialogB.setView(R.layout.dialog_stats);
            dialogB.setTitle(R.string.stats_title);
            dialogB.setNeutralButton(R.string.stats_close, null);

            AlertDialog dialog = dialogB.create();
            dialog.show();

            TextView numberOfCreatedDocuments = dialog.findViewById(R.id.number_of_created_documents);
            TextView numberOfSharedDocuments = dialog.findViewById(R.id.number_of_shared_documents);
            TextView numberOfDocumentsSharedWithUser = dialog.findViewById(R.id.number_of_documents_shared_with_user);
            TextView numberOfAllDocuments = dialog.findViewById(R.id.number_of_all_documents);

            //TODO inconsistency sharedDocuments / sharedWithUser / sharedWithOthers
            numberOfCreatedDocuments.setText(getResources().getString(R.string.count_with_bytes,
                    stats.countOfCreatedDocuments(), stats.createdDocumentsFileSize()));

            numberOfSharedDocuments.setText(getResources().getString(R.string.count_with_bytes,
                    stats.countOfDocumentsSharedWithOthers(), stats.documentsSharedWithOthersFileSize()));

            numberOfDocumentsSharedWithUser.setText(getResources().getString(R.string.count_with_bytes,
                    stats.countOfSharedDocuments(), stats.sharedDocumentsFileSize()));

            numberOfAllDocuments.setText(getResources().getString(R.string.count_with_bytes,
                    stats.countOfAllDocuments(), stats.allDocumentsFileSize()));
        }
    }

}