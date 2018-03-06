package com.centennialcollege.brogrammers.businesschatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPassword2 = findViewById(R.id.et_password_2);

        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.button_registration).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_registration:
                attemptRegistration();
                break;
        }
    }

    private void attemptRegistration() {
        etEmail.setError(null);
        etPassword.setError(null);

        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();

        View focusView = null;

        String errorMessage = UserInputChecker.checkUsername(username);
        if (errorMessage != null) {
            etUsername.setError(errorMessage);
            focusView = etUsername;
        }

        errorMessage = UserInputChecker.checkEmail(email);
        if (errorMessage != null) {
            etEmail.setError(errorMessage);
            focusView = etEmail;
        }

        errorMessage = UserInputChecker.checkPassword(password);
        if (errorMessage != null) {
            etPassword.setError(errorMessage);
            focusView = etPassword;
        } else {
            errorMessage = UserInputChecker.checkPasswords(password, password2);
            if (errorMessage != null) {
                etPassword2.setError(errorMessage);
                focusView = etPassword2;
            }
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            //ToDo Registration with firebase
        }
    }

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