package com.centennialcollege.brogrammers.businesschatapp.ui.registration;

import com.centennialcollege.brogrammers.businesschatapp.ui.BasePresenter;

interface RegistrationContract {

    interface View {

        void showErrorEmailRequired();

        void showErrorPasswordRequired();

        void showErrorEmailInvalid();

        void showErrorRegistration();

        void hideErrors();

        void showProgress();

        void hideProgress();

        void launchMainActivity();

        void showErrorEmailExist();

        void showErrorUsernameRequired();

        void showErrorPasswordConfirmRequired();

        void showErrorPasswordsNotSame();

        void showErrorUsernameTooShort();

        void showErrorPasswordTooShort();
    }

    interface Presenter extends BasePresenter<View> {

        void attemptRegistration(String username, String email, String password, String passwordConfirm);
    }

    interface Model {


    }

}
