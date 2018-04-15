package com.centennialcollege.brogrammers.businesschatapp.ui.registration;

import com.centennialcollege.brogrammers.businesschatapp.ui.BasePresenter;

interface RegistrationContract {

    enum Error {
        ERROR_USERNAME_REQUIRED,
        ERROR_EMAIL_REQUIRED,
        ERROR_PASSWORD_REQUIRED,
        ERROR_PASSWORD_CONFIRMATION_REQUIRED,
        ERROR_EMAIL_INVALID,
        ERROR_USERNAME_TOO_SHORT,
        ERROR_PASSWORD_TOO_SHORT,
        ERROR_PASSWORDS_NOT_MATCHING,
        ERROR_EMAIL_EXISTS,
        ERROR_REGISTRATION
    }

    interface View {

        void showError(Error errorCode);

        void hideErrors();

        void showProgress();

        void hideProgress();

        void launchMainActivity();

    }

    interface Presenter extends BasePresenter<View> {

        void attemptRegistration(String username, String email, String password, String passwordConfirm);
    }

    interface Model {


    }

}
