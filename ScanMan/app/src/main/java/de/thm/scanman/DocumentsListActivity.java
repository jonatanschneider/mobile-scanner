package de.thm.scanman;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class DocumentsListActivity extends AppCompatActivity {

    private ListView documentsListView;
    private FloatingActionButton addFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_list);

        documentsListView = findViewById(R.id.documents_list);
        documentsListView.setEmptyView(findViewById(R.id.documents_list_empty));

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
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
