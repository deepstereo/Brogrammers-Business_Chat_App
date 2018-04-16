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
    public Task<AuthResult> signIn(String email, String password) {
        return firebaseAuthHelper.signInWithEmailAndPassword(email, password);
    }

    @Override
    public Task<Void> createUser(final User user, String password) {
        return firebaseAuthHelper.registerUser(user.getEmail(), password)
                .continueWithTask(task -> firebaseDbHelper.addUserInfo(task.getResult().getUser().getUid(), user));
    }

}
