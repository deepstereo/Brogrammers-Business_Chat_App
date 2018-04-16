package com.centennialcollege.brogrammers.businesschatapp.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelperImpl implements FirebaseAuthHelper {

    @Override
    public Task<Void> deleteCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.delete();
        }
        return null;
    }

    @Override
    public Task<Void> updatePassword(String password) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.updatePassword(password);
        }
        return null;
    }

    @Override
    public Task<Void> updateEmail(String email) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.updateEmail(email);
        }
        return null;
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public Task<AuthResult> registerUser(String email, String password) {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);
    }

    @Override
    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {

        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
