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

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_registration).setOnClickListener(this);
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

            signInWithEmailAndPassword(email, password);
        }
    }

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
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
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