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
 * Activity offering base actions / utilities for authentication screens (login, register, ..)
 */
public abstract class AuthenticationBaseActivity extends AppCompatActivity {
    /**
     * Minimum length of the requested password. Used for validation.
     */
    private static final int PASSWORD_MIN_LENGTH = 6;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Gets the FirebaseAuth's instance
     * @return the FirebaseAuth instance
     */
    protected FirebaseAuth getAuth() {
        return auth;
    }

    /**
     * Checks if the passed string is a valid email
     * @param email
     * @return <code>true</code> if email is a non empty string and contains an @ char;
     * <code>false</code> otherwise
     */
    protected boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@");
    }

    /**
     * Checks if the passed string is a valid password
     * @param password
     * @return <code>true</code> if the password's length is not less then the configured password
     * min length; <code>false</code> otherwise
     */
    protected boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= PASSWORD_MIN_LENGTH;
    }

    /**
     * Starts the main activity as a new task (backstack clear)
     */
    protected void startMainActivity() {
        Intent intent = new Intent(this, DocumentsListsActivity.class);
        // Create no backstack history so a logged in user doesn't get back to the authentication
        // screen when trying to close the app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Toggles form's / progress indicator's visibility based on the passed show (progress) flag
     * @param contentView
     * @param progressView
     * @param show if <code>true</code> show the progress indicator and hide the form; if <code>false</code>
     *             hide the progress indicator and show the form
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
