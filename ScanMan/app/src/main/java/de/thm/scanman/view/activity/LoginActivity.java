package de.thm.scanman.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import de.thm.scanman.R;

/**
 * A login screen that offers login via email/password
 */
// TODO: Check whether additional logic can be moved to the superclass.
public class LoginActivity extends AuthenticationBaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText emailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    private View signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startMainActivityIfAlreadyLoggedIn();
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent caller = getIntent();
        if (caller != null) {
            Uri data = caller.getData();
            if (data != null && data.toString().contains("http://de.thm.scanman")) {
                List<String> params = data.getPathSegments();
                if (params.size() != 2) return;     // stop process when data is not valid

                String ownerID = params.get(0);
                String documentID = params.get(1);
                Intent intent = new Intent(LoginActivity.this, DocumentsListsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ownerID", ownerID);
                intent.putExtra("documentID", documentID);
                startActivity(intent);
            }
        }
    }

    private void setupView() {
        emailView = findViewById(R.id.email);

        passwordView = findViewById(R.id.password);
        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> attemptLogin());

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        signUpLink = findViewById(R.id.sign_up_link);
        signUpLink.setOnClickListener(view -> startSignUpActivity());
    }

    /**
     * When the user is already logged in (session stored from previous app usage) skip the login
     * form and navigate directly to the main activity
     */
    private void startMainActivityIfAlreadyLoggedIn() {
        if (getAuth().getCurrentUser() != null) {
            Toast.makeText(this, R.string.login_welcome_back, Toast.LENGTH_SHORT).show();
            startMainActivity();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean isValid = true;
        View focusView = null;

        if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            isValid = false;
        }

        if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            isValid = false;
        }

        if (isValid) {
            makeLogin(email, password);
        } else {
            // There was an error; don't attempt login and focus the first
            // form field with an error
            focusView.requestFocus();
        }
    }

    /**
     * Actually make the login request based on the credentials validated before and adjust the ui
     * accordingly
     */
    private void makeLogin(String email, String password) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt
        showProgress(loginFormView, progressView, true);

        // Start firebase auth task
        getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, start main activity
                        startMainActivity();
                    } else {
                        // If sign in fails, show the form and an error message
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, R.string.login_failed,
                                Toast.LENGTH_SHORT).show();
                        showProgress(loginFormView, progressView, false);
                    }
                });
    }

    protected boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@");
    }

    protected boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password);
    }

    protected void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, DocumentsListsActivity.class);
        // Create no backstack history so a logged in user doesn't get back to the login screen
        // when trying to close the app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimationTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    private void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}

