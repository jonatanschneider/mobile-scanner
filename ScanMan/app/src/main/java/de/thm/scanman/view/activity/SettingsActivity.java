package de.thm.scanman.view.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import de.thm.scanman.R;

import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show up button in action
        ActionBar ac = getSupportActionBar();
        if (ac != null) {
            ac.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.logout_button).setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startLoginActivity();
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
