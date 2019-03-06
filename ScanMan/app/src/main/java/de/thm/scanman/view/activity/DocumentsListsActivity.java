package de.thm.scanman.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.util.DocumentComparators;
import de.thm.scanman.view.fragment.ViewPagerItemFragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import de.thm.scanman.model.Stats;
import de.thm.scanman.model.User;

import static de.thm.scanman.persistence.FirebaseDatabase.userDAO;

public class DocumentsListsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout tabBar;
    private ViewPager viewPager;
    private FloatingActionButton addFab;
    private ViewPagerItemFragment fragment;
    private User user;
    private int comparator = 0;
    private boolean descending = false;

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
    protected void onStart() {
        super.onStart();
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
            case R.id.action_sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.layout.sort_dialog);
                builder.setTitle(R.string.sortedBy);
                Boolean[] descending = {false};
                String[] compareAfter = {""};
                builder.setPositiveButton(R.string.sort, (q, w) -> sort (compareAfter[0], descending[0]));
                builder.setNeutralButton(R.string.cancel, (q,w) -> {});

                AlertDialog dialog = builder.create();
                dialog.show();

                // configure spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, sortedByList());
                Spinner sp = dialog.findViewById(R.id.sortedBy);
                CheckBox cb = dialog.findViewById(R.id.descending);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> descending[0] = isChecked);
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        compareAfter[0] = parent.getItemAtPosition(pos).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                sp.setAdapter(adapter);
                dialog.setView(sp);
        }
        return super.onOptionsItemSelected(item);
    }

    // setup and style tab bar
    private void setUpPagerAndTabs(){
        tabBar.setTabTextColors(ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTab));

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        tabPagerAdapter.getItem(0);
        viewPager.setCurrentItem(0);

        tabBar.addOnTabSelectedListener(this);
        tabBar.setupWithViewPager(viewPager);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        if (fragment != null) fragment.sort();
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

            fragment = new ViewPagerItemFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("idx", position);
            bundle.putInt("comparator", comparator);
            bundle.putBoolean("descending", descending);
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
    /**
     * This method is used to sort the the lists allDocuments, createdDocuments and sharedDocuments.
     * @param comparatorString contains the name of comparator for sorting
     */
    private void sort(String comparatorString, boolean descending){
        this.comparator = sortedByList().indexOf(comparatorString);
        this.descending = descending;
        Comparator<Document> comparator = DocumentComparators.getComparator(this.comparator, descending);
        fragment.sort(comparator);
    }

    private List<String> sortedByList() {
        return Arrays.asList(
                getResources().getString(R.string.alphabet), getResources().getString(R.string.size),
                getResources().getString(R.string.createdAt), getResources().getString(R.string.lastUpdate),
                getResources().getString(R.string.owner));
    }
}