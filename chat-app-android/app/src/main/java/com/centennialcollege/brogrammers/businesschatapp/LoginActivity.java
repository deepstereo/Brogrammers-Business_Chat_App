package com.centennialcollege.brogrammers.businesschatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    public static final String ERROR_AUTHENTICATION = "Authentication failed";
    private static final String TAG = "LoginActivity";
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        // If the user is still logged in, navigate straight to the Chat Screen.
        if (isUserLoggedIn()) {
            launchChatActivity();
        }

        init();
    }

    /**
     * Initialize the views.
     */
    private void init() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_registration).setOnClickListener(this);
    }

    /**
     * Checks if the user is still logged in or not.
     * @return : True if logged in, false otherwise.
     */
    private boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                attemptLogin();
                break;
            case R.id.button_registration:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }

    /**
     *  Perform data validations and attempt to login.
     */
    private void attemptLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (performValidations(email, password)) {
            showProgress(true);
            signInWithEmailAndPassword(email, password);
        }
    }

    /**
     * Performs validations on the user inputs.
     * @return : True if all inputs are valid, false otherwise.
     */
    private boolean performValidations(String email, String password) {
        String errorMessage = UserInputChecker.checkEmail(email);
        etEmail.setError(errorMessage);
        if (errorMessage != null) {
            etEmail.requestFocus();
            return false;
        }

        errorMessage = UserInputChecker.checkPassword(password);
        etPassword.setError(errorMessage);
        if (errorMessage != null) {
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Attempt to sign in the user through email address and password credentials.
     */
    private void signInWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress(false);
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmailAndPassword:onComplete:"
                                    + task.getException());
                            Toast.makeText(LoginActivity.this, ERROR_AUTHENTICATION,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            launchChatActivity();
                        }
                    }
                });
    }

    private void launchChatActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Show/hide the progress bar based on the boolean input passed in.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}