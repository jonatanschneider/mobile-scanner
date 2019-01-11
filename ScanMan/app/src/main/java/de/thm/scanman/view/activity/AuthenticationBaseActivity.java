package de.thm.scanman.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import de.thm.scanman.view.activity.DocumentsListsActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity offering base actions for authentication screens (login, register, ..)
 */
public abstract class AuthenticationBaseActivity extends AppCompatActivity {
    private static final int PASSWORD_MIN_LENGTH = 6;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
    }

    protected FirebaseAuth getAuth() {
        return auth;
    }

    protected boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@");
    }

    protected boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= PASSWORD_MIN_LENGTH;
    }

    protected void startMainActivity() {
        Intent intent = new Intent(this, DocumentsListsActivity.class);
        // Create no backstack history so a logged in user doesn't get back to the login screen
        // when trying to close the app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    protected void showProgress(View contentView, View progressView, final boolean show) {
        int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
        contentView.animate().setDuration(shortAnimationTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimationTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
