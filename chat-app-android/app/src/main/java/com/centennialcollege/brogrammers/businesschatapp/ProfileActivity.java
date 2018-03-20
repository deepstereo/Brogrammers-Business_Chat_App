package com.centennialcollege.brogrammers.businesschatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String USERS_CHILD = "users-android-test";

    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private User user;

    private ProgressBar progressBar;
    private EditText etUsername;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);

        progressBar = findViewById(R.id.progress_bar);
        showProgress(true);

        initUserInfo();
    }

    private void initUserInfo() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child(USERS_CHILD)
                    .child(currentUser.getUid());
            if (userRef != null) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            etUsername.setText(user.getUsername());
                            etEmail.setText(user.getEmail());

                            showProgress(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                attemptChangeUserInfo();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private void attemptChangeUserInfo() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();

        if (performValidations(username, email)) {
            showProgress(true);
            changeUserInfo(username, email);
        }
    }

    private void changeUserInfo(String username, String email) {
        if (user != null && userRef != null) {
            user.setUsername(username);
            user.setEmail(email);

            currentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();

                                showProgress(false);
                                onBackPressed();
                            }
                        });
                    else {
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
            });
        }
    }

    private boolean performValidations(String username, String email) {
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

        return true;
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
