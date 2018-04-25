package com.centennialcollege.brogrammers.businesschatapp.ui.login;

import com.centennialcollege.brogrammers.businesschatapp.ui.BasePresenter;

interface LoginContract {

    enum Error {
        ERROR_EMAIL_REQUIRED,
        ERROR_PASSWORD_REQUIRED,
        ERROR_EMAIL_INVALID,
        ERROR_WRONG_PASSWORD,
        ERROR_AUTHORIZATION,
        ERROR_EMAIL_DOES_NOT_EXIST,
    }

    interface View {

        void showError(Error errorCode);

        void hideErrors();

        void showProgress();

        void hideProgress();

        void launchMainActivity();

        void launchRegistrationActivity();

    }

    interface Presenter extends BasePresenter<View> {

        void attemptLogin(String email, String password);

    }

    interface Model {


    }

}
