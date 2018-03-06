package com.centennialcollege.brogrammers.businesschatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        progressBar = findViewById(R.id.login_progress);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_sing_up).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                attemptLogin();
                break;
            case R.id.button_sing_up:
                //ToDo Go to sing up activity
                break;
        }
    }

    private void attemptLogin() {
        etEmail.setError(null);
        etPassword.setError(null);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        View focusView = null;

        String errorMessage = UserInputChecker.checkEmail(email);
        if (errorMessage != null) {
            etEmail.setError(errorMessage);
            focusView = etEmail;
        }

        errorMessage = UserInputChecker.checkPassword(password);
        if (errorMessage != null) {
            etPassword.setError(errorMessage);
            focusView = etPassword;
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            //ToDo Login with firebase
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