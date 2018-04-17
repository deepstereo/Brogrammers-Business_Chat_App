package com.centennialcollege.brogrammers.businesschatapp.data;

import android.support.annotation.NonNull;

import com.centennialcollege.brogrammers.businesschatapp.data.remote.FirebaseAuthExceptions;
import com.centennialcollege.brogrammers.businesschatapp.data.remote.FirebaseAuthHelper;
import com.centennialcollege.brogrammers.businesschatapp.data.remote.FirebaseAuthHelperImpl;
import com.centennialcollege.brogrammers.businesschatapp.data.remote.FirebaseDbHelper;
import com.centennialcollege.brogrammers.businesschatapp.data.remote.FirebaseDbHelperImpl;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DataManagerImpl implements DataManager {
    private FirebaseAuthHelper firebaseAuthHelper;
    private FirebaseDbHelper firebaseDbHelper;

    public DataManagerImpl() {
        this.firebaseAuthHelper = new FirebaseAuthHelperImpl();
        this.firebaseDbHelper = new FirebaseDbHelperImpl();
    }

    @NonNull
    @Override
    public FirebaseAuthExceptions getFirebaseException(Exception e) {
        FirebaseAuthExceptions firebaseAuthException = FirebaseAuthExceptions.ERROR_UNKNOWN;
        try {
            if (e instanceof FirebaseAuthException) {
                String errorCode = ((FirebaseAuthException) e).getErrorCode();
                firebaseAuthException = FirebaseAuthExceptions.valueOf(errorCode);
            }
        } catch (Exception ignore) {
        }
        return firebaseAuthException;
    }

    @Override
    public FirebaseAuthHelper getFirebaseAuthHelper() {
        return firebaseAuthHelper;
    }

    @Override
    public FirebaseDbHelper getFirebaseDbHelper() {
        return firebaseDbHelper;
    }

    @Override
    public Task<AuthResult> signIn(String email, String password) {
        return firebaseAuthHelper.signInWithEmailAndPassword(email, password);
    }

    @Override
    public Task<Void> createUser(final User user, String password) {
        return firebaseAuthHelper.registerUser(user.getEmail(), password)
                .continueWithTask(task -> firebaseDbHelper.replaceUserInfo(task.getResult().getUser().getUid(), user));
    }

    @Override
    public void getCurrentUserInfo(GetUserInfoCallback getUserInfoCallback) {
        FirebaseUser firebaseUser = firebaseAuthHelper.getCurrentUser();
        String userId = firebaseUser.getUid();

        firebaseDbHelper.getUserRef(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null)
                            getUserInfoCallback.onSuccess(user);
                        else
                            getUserInfoCallback.onFailure();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getUserInfoCallback.onFailure();
                    }
                });
    }

    @Override
    public Task<Void> replaceCurrentUserEmailAndUsername(String email, String username,
                                                         ReplaceUserEmailAndUsernameCallback replaceCallback) {
        FirebaseUser firebaseUser = firebaseAuthHelper.getCurrentUser();
        String userId = firebaseUser.getUid();

        return firebaseAuthHelper.updateEmail(email)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return firebaseDbHelper.replaceUserEmail(userId, email);
                    } else {
                        replaceCallback.onFailure(task.getException());
                        return null;
                    }
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return firebaseDbHelper.replaceUserUsername(userId, username);
                    } else {
                        return null;
                    }
                })
                .addOnSuccessListener(aVoid -> replaceCallback.onSuccess())
                .addOnFailureListener(replaceCallback::onFailure);
    }


}
