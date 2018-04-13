package com.centennialcollege.brogrammers.businesschatapp.data.autharization;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;


public class AuthorizationModel implements AuthorizationContract.Model, OnFailureListener, OnSuccessListener<AuthResult> {

    private AuthorizationContract.Presenter presenter;

    private FirebaseAuth firebaseAuth;

    private OnSuccessListener<Void> onUpdateUserSuccessListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {

        }
    };

    public AuthorizationModel(AuthorizationContract.Presenter presenter) {
        this.presenter = presenter;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void deleteCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.delete();
        }
    }

    @Override
    public void updatePassword(String password) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updatePassword(password)
                    .addOnSuccessListener(onUpdateUserSuccessListener)
                    .addOnFailureListener(this);
        }
    }

    @Override
    public void updateEmail(String email) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updateEmail(email)
                    .addOnSuccessListener(onUpdateUserSuccessListener)
                    .addOnFailureListener(this);
        }
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
    }

    @Override
    public void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void signInWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();
        presenter.onSuccessAuthorization(firebaseUser);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        String errorCode = "";
        if (e instanceof FirebaseAuthException) {
            errorCode = ((FirebaseAuthException) e).getErrorCode();
        }
        presenter.onFailureAuthorization(errorCode);
    }

}
