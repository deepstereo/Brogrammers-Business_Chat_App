package com.centennialcollege.brogrammers.businesschatapp.data.autharization;

import com.google.firebase.auth.FirebaseUser;

public interface AuthorizationContract {

    interface Presenter {

        void onSuccessAuthorization(FirebaseUser user);

        void onFailureAuthorization(String errorCode);

    }

    interface Model {

        void deleteCurrentUser();

        void updatePassword(String password);

        void updateEmail(String email);

        void signOut();

        void createUser(String email, String password);

        void signInWithEmailAndPassword(String email, String password);

    }

}
