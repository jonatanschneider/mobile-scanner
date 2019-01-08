package de.thm.scanman;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import static de.thm.scanman.persistence.FirebaseDatabase.documentDAO;
import static de.thm.scanman.persistence.FirebaseDatabase.userDAO;

public class DocumentsListsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout tabBar;
    private ViewPager viewPager;
    private FloatingActionButton addFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_lists);

        getSupportActionBar().setElevation(0f);

        tabBar = findViewById(R.id.tbl_main_content);
        viewPager = findViewById(R.id.viewpager);
        setUpPagerAndTabs();

        addFab = findViewById(R.id.add_fab);
        addFab.setOnClickListener(
            // implement call for new intent here
            view -> {}
        );
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
                // implement call for statistics here
                //new StatsTask(this).execute(documents);
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

        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));

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

    public class TabAdapter extends FragmentStatePagerAdapter {

        private final String[] pageTitles = {
                getApplicationContext().getString(R.string.all_documents),
                getApplicationContext().getString(R.string.my_documents),
                getApplicationContext().getString(R.string.shared_documents)
        };

        public TabAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public int getCount() {
            return pageTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            /*
            Fragment fragment = new ViewPagerItemFragment();
            Record r = records.get(position);
            Bundle bundle = new Bundle();
            bundle.putLong("id", r.id);
            fragment.setArguments(bundle);
            return fragment;
            */
            return ViewPagerItemFragment.getInstance(pageTitles[position]);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return pageTitles[position];
        }
    }

}