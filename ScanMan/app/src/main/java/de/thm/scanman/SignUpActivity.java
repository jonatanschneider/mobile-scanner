package de.thm.scanman;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// TODO: Check whether additional logic can be moved to the superclass.
public class SignUpActivity extends AuthenticationBaseActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText emailView;
    private EditText passwordView;
    private EditText repeatPasswordView;

    private View progressView;
    private View signUpFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setupView();
    }

    private void setupView() {
        emailView = findViewById(R.id.email);

        passwordView = findViewById(R.id.password);
        repeatPasswordView = findViewById(R.id.repeat_password);
        repeatPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptSignUp();
                return true;
            }
            return false;
        });

        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(view -> attemptSignUp());

        signUpFormView = findViewById(R.id.sign_up_form);
        progressView = findViewById(R.id.sign_up_progress);
    }

    private void attemptSignUp() {
        // Reset errors
        emailView.setError(null);
        passwordView.setError(null);
        repeatPasswordView.setError(null);

        // Store values at the time of the sign up attempt
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String repeatPassword = repeatPasswordView.getText().toString();

        boolean isValid = true;
        View focusView = null;

        // TODO: check if password.length >= 6
        if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            isValid = false;
        }

        if (!repeatPassword.equals(password)) {
            repeatPasswordView.setError(getString(R.string.error_unequal_passwords));
            focusView = repeatPasswordView;
            isValid = false;
        }

        if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            isValid = false;
        }

        if (isValid) {
            makeSignUp(email, password);
        } else {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error
            focusView.requestFocus();
        }
    }

    /**
     * Actually make the sign up request based on the credentials validated before and adjust the ui
     * accordingly
     */
    private void makeSignUp(String email, String password) {
        // Show a progress spinner, and kick off a background task to
        // perform the user sign up attempt
        showProgress(signUpFormView, progressView, true);

        // Start firebase auth task
        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        startMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, R.string.sign_up_failed,
                                Toast.LENGTH_SHORT).show();
                        showProgress(signUpFormView, progressView, false);
                    }
                });
    }
}
