package com.centennialcollege.brogrammers.businesschatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ERROR_REGISTRATION = "Registration failed";
    private static final String TAG = "RegistrationActivity";
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

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
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();

        if (performValidations(username, email, password, password2)) {
            showProgress(true);
            createUserWithEmailAndPassword(username, email, password);
        }
    }

    private boolean performValidations(String username,String email, String password, String password2) {
        String errorMessage = UserInputChecker.checkUsername(username);
        etUsername.setError(errorMessage);
        if (errorMessage != null) {
            etUsername.requestFocus();
            return false;
        }

        errorMessage = UserInputChecker.checkEmail(email);
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
        } else {
            errorMessage = UserInputChecker.checkPasswords(password, password2);
            etPassword2.setError(errorMessage);
            if (errorMessage != null) {
                etPassword2.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void createUserWithEmailAndPassword(final String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmailAndPassword:onComplete:"
                                    + task.getException());
                            Toast.makeText(RegistrationActivity.this, ERROR_REGISTRATION,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            setDisplayName(username);
                        }
                    }
                });
    }

    private void setDisplayName(String username) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updateProfile(
                    new UserProfileChangeRequest.Builder().setDisplayName(username).build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showProgress(false);
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "updateProfile:setDisplayName:"
                                        + task.getException());
                                Toast.makeText(RegistrationActivity.this, ERROR_REGISTRATION,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(RegistrationActivity.this, ChatActivity.class));
                                finish();
                            }
                        }
                    });
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