package de.thm.scanman.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.thm.scanman.R;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.FirebaseDatabase;

/**
 * Activity allowing the user to create an account (sign up process)
 * <br>
 * Asks for
 * <ul>
 *     <li>name</li>
 *     <li>email</li>
 *     <li>password</li>
 * </ul>
 * Uses validation and view utilities of the {@link AuthenticationBaseActivity}.
 *
 * @see AuthenticationBaseActivity
 */
public class SignUpActivity extends AuthenticationBaseActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText emailView;
    private EditText nameView;
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

    /**
     * Init the content view by referencing views and setting up interaction listeners
     */
    private void setupView() {
        emailView = findViewById(R.id.email);
        nameView = findViewById(R.id.name);

        passwordView = findViewById(R.id.password);
        repeatPasswordView = findViewById(R.id.repeat_password);
        // Attempt sign up when the user triggers the next / done keyboard event in the last input
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

    /**
     * Attempts to sign up the account specified by the sign up form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual sign up attempt is made.
     */
    private void attemptSignUp() {
        // Reset errors
        emailView.setError(null);
        nameView.setError(null);
        passwordView.setError(null);
        repeatPasswordView.setError(null);

        // Store values at the time of the sign up attempt
        String email = emailView.getText().toString();
        String name = nameView.getText().toString();
        String password = passwordView.getText().toString();
        String repeatPassword = repeatPasswordView.getText().toString();

        boolean isValid = true;
        View focusView = null;

        if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            isValid = false;
        }

        if (!isRepeatPasswordValid(password, repeatPassword)) {
            repeatPasswordView.setError(getString(R.string.error_unequal_passwords));
            focusView = repeatPasswordView;
            isValid = false;
        }

        if (!isNameValid(name)) {
            nameView.setError(getString(R.string.error_invalid_name));
            focusView = nameView;
            isValid = false;
        }

        if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            isValid = false;
        }

        if (isValid) {
            makeSignUp(email, name, password);
        } else {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error
            focusView.requestFocus();
        }
    }

    /**
     * Checks if the passed string is a valid name
     * @param name
     * @return <code>true</code> if name is a non empty string; <code>false</code> otherwise
     */
    private boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name);
    }

    /**
     * Checks if the repeat password is valid
     * @param password
     * @param repeatPassword
     * @return <code>true</code> if the repeat password equals the password; <code>false</code> otherwise
     */
    private boolean isRepeatPasswordValid(String password, String repeatPassword) {
        return repeatPassword.equals(password);
    }

    /**
     * Actually makes the sign up request based on the credentials validated before and adjust the ui
     * accordingly
     * @param email
     * @param name
     * @param password
     */
    private void makeSignUp(String email, String name, String password) {
        // Show a progress spinner, and kick off a background task to
        // perform the user sign up attempt
        showProgress(signUpFormView, progressView, true);

        // Start firebase auth task
        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        User user = new User();
                        user.setId(getAuth().getCurrentUser().getUid());
                        user.setName(name);
                        user.setMail(email);

                        // Add the created user to the users node in the database
                        FirebaseDatabase.userDAO.add(user);

                        // Navigate to the main activity
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
