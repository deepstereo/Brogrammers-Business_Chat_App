package com.centennialcollege.brogrammers.businesschatapp.ui.login;

import com.centennialcollege.brogrammers.businesschatapp.ui.BasePresenter;

interface LoginContract {

    interface View {

        void showErrorEmailRequired();

        void showErrorPasswordRequired();

        void showErrorEmailInvalid();

        void showErrorWrongPassword();

        void showErrorAuthorization();

        void hideErrors();

        void showProgress();

        void hideProgress();

        void launchMainActivity();

        void launchRegistrationActivity();

        void showErrorEmailNotExist();
    }

    interface Presenter extends BasePresenter<View> {

        void attemptLogin(String email, String password);

    }

    interface Model {


    }

}
