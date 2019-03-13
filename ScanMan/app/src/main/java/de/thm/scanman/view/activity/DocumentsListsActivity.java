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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

import static de.thm.scanman.persistence.FirebaseDatabase.documentDAO;
import static de.thm.scanman.persistence.FirebaseDatabase.userDAO;

/**
 * Main Activity of the Application.
 */
public class DocumentsListsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout tabBar;
    private ViewPager viewPager;
    private FloatingActionButton addFab;
    private User user;
    private Set<String> documentIDs = new HashSet<>();
    private Intent intent;

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

        intent = getIntent();
        userDAO.get(FirebaseAuth.getInstance().getUid()).observe(this, u -> {
            user = u;
            handleIntent();
        });
    }

    private void handleIntent() {
        if (intent != null && intent.getStringExtra("ownerID") != null) {
            // Add document to shared documents
            // Uri data = caller.getParcelableExtra("data");
            String ownerID = intent.getStringExtra("ownerID");
            String documentID = intent.getStringExtra("documentID");
            // stop process when document is already added this session
            if (documentIDs.contains(documentID)) return;

            if (userIsOwner(documentID)) {
                Toast.makeText(this, "Sie sind bereits der Besitzer des Dokumentes", Toast.LENGTH_LONG).show();
            } else if (userHasAccess(documentID)) {
                Toast.makeText(this, "Sie haben bereits Zugriff auf dieses Dokument", Toast.LENGTH_LONG).show();
            } else {
                documentIDs.add(documentID);
                Document doc = new Document();
                doc.setOwnerId(ownerID);
                doc.setId(documentID);
                Toast success = Toast.makeText(this, R.string.added_new_document, Toast.LENGTH_LONG);
                Toast fail = Toast.makeText(this, R.string.already_joined_document, Toast.LENGTH_LONG);
                documentDAO.addSharedDocument(doc, Optional.of(success), Optional.of(fail));

            }
        }
    }

    private boolean userIsOwner(String documentID) {
        return user.getCreatedDocuments().stream().anyMatch(doc -> doc.getId().equals(documentID));
    }

    private boolean userHasAccess(String documentID) {
        return user.getSharedDocuments().stream().anyMatch(doc -> doc.getId().equals(documentID));
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

    /**
     * Method to style and setup the TabLayout tabBar
     */
    private void setUpPagerAndTabs(){
        // set colors of the tabs
        tabBar.setTabTextColors(ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTab));

        // set an adapter onto viewPager
        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);

        tabBar.addOnTabSelectedListener(this);
        // give the TabLayout the ViewPager
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

    /**
     * Inner class for the adapter for viewPager.
     */
    public class TabPagerAdapter extends FragmentStatePagerAdapter {

        /**
         * The titles of the tabs.
         */
        private final String[] pageTitles = {
                getApplicationContext().getString(R.string.all_documents),
                getApplicationContext().getString(R.string.my_documents),
                getApplicationContext().getString(R.string.shared_documents)
        };

        /**
         * Constructor
         *
         * @param fm FragmentManager
         */
        public TabPagerAdapter(FragmentManager fm){
            super(fm);
        }

        /**
         * Return the number of tabs.
         */
        @Override
        public int getCount() {
            return pageTitles.length;
        }

        /**
         * Return the Fragment associated with a specified position (tab).
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ViewPagerItemFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("idx", position);
            fragment.setArguments(bundle);

            return fragment;
        }

        /**
         * Return the title of the tab associated with a specified position.
         */
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