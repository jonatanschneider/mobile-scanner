package de.thm.scanman;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class DocumentsListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;
    private ListView documentsListView;
    private FloatingActionButton addFab;

    private TabLayout tabBar;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_list);

        viewPager = findViewById(R.id.viewpager);

        //viewPager.setAdapter(new RecordPagerAdapter(getSupportFragmentManager()));
        //viewPager.setCurrentItem(idx);

        getSupportActionBar().setElevation(0f);

        content = (TextView) findViewById(R.id.lbl_basic_content);

        tabBar = (TabLayout)findViewById(R.id.tbl_main_content);

        //create new tabs and set titles
        tabBar.addTab(tabBar.newTab().setText("Alle Dokumente"));
        tabBar.addTab(tabBar.newTab().setText("Meine Dokumente"));
        tabBar.addTab(tabBar.newTab().setText("Geteilte Dokumente"));


        tabBar.setTabTextColors(ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, R.color.colorAccent));
        tabBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTab));

        tabBar.addOnTabSelectedListener(this);
        //tabBar.setupWithViewPager(pager);

        //documentsListView = findViewById(R.id.documents_list);
        //documentsListView.setEmptyView(findViewById(R.id.documents_list_empty));

        addFab = findViewById(R.id.add_fab);
        //addFab.setOnClickListener(
                // implement call for new intent here
        //);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.documentslist, menu);
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
                // implement call for settings here
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //content.setText(tab.getText().toString());
        if(tab.getText().toString().equals("Alle Dokumente")) {
            Fragment fragment = new AllDocumentsFragment();
            //Bundle bundle = new Bundle();
            //bundle.putString("id", checkedRecord.getId());
            //fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, "details")
                    .commit();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
