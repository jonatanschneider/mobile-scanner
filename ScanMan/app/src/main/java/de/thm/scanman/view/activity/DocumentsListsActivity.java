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

        userDAO.get(FirebaseAuth.getInstance().getUid()).observe(this, u -> {
            user = u;
            handleIntent(getIntent());
        });
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getStringExtra("ownerID") != null) {
            // Add document to shared documents
            // Uri data = caller.getParcelableExtra("data");
            String ownerID = intent.getStringExtra("ownerID");
            String documentID = intent.getStringExtra("documentID");
            System.out.println(ownerID + "LOLOL" + documentID + FirebaseAuth.getInstance().getUid());
            // stop process when document is already added this session
            if (documentIDs.contains(documentID)) return;
            documentIDs.add(documentID);
            if (ownerID.equals(FirebaseAuth.getInstance().getUid())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Sie sind bereits der Besitzer des Dokumentes");
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> { });
                builder.show();
                System.out.println("LOLOL Besitzer");
                return;
            }
            Document doc = new Document();
            doc.setOwnerId(ownerID);
            doc.setId(documentID);
            System.out.println("LOLOL im here");
            if (    user != null) {
                if (user.getSharedDocuments().stream().noneMatch(d -> d.getId().equals(documentID))) {
                    if (documentDAO.addSharedDocument(doc)) {
                        Toast.makeText(this, R.string.added_new_document, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        System.out.println("LOLoL couldNotAdd");
                    }
                } else {
                    System.out.println("LOLoL A Matsch");
                }
            } else {
                System.out.println("LOLoL user is null");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Sie sind dem Dokument bereits beigetreten");
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> { });
            builder.show();
        } else {
            System.out.println("Intent is null");
        }
        System.out.println("LOLOL ENDE");
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