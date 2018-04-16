package com.centennialcollege.brogrammers.businesschatapp.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public interface FirebaseAuthHelper {

    void signOut();

    Task<Void> deleteCurrentUser();

    Task<Void> updatePassword(String password);

    Task<Void> updateEmail(String email);

    Task<AuthResult> registerUser(String email, String password);

    Task<AuthResult> signInWithEmailAndPassword(String email, String password);

    FirebaseUser getCurrentUser();
}
