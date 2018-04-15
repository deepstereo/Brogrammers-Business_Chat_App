package com.centennialcollege.brogrammers.businesschatapp.ui.login;

import android.text.TextUtils;

import com.centennialcollege.brogrammers.businesschatapp.data.authorization.AuthorizationContract;
import com.centennialcollege.brogrammers.businesschatapp.data.authorization.AuthorizationModel;
import com.google.firebase.auth.FirebaseUser;


class LoginPresenter implements LoginContract.Presenter, AuthorizationContract.Presenter {

    private LoginContract.View view;
    private AuthorizationContract.Model model;

    LoginPresenter() {
        model = new AuthorizationModel(this);
    }

    @Override
    public void takeView(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void attemptLogin(String email, String password) {
        view.hideErrors();

        email = email.trim();
        password = password.trim();

        if (TextUtils.isEmpty(email)) {
            view.showError(LoginContract.Error.ERROR_EMAIL_REQUIRED);
            return;
        }

        if (!isEmailValid(email)) {
            view.showError(LoginContract.Error.ERROR_EMAIL_INVALID);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            view.showError(LoginContract.Error.ERROR_PASSWORD_REQUIRED);
            return;
        }

        view.showProgress();

        model.signInWithEmailAndPassword(email, password);
    }

    @Override
    public void onSuccessAuthorization(FirebaseUser user) {
        view.hideProgress();

        view.launchMainActivity();
    }

    @Override
    public void onFailureAuthorization(String errorCode) {
        view.hideProgress();

        if (errorCode != null) {
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    view.showError(LoginContract.Error.ERROR_EMAIL_INVALID);
                    break;
                case "ERROR_WRONG_PASSWORD":
                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                    view.showError(LoginContract.Error.ERROR_WRONG_PASSWORD);
                    break;
                case "ERROR_USER_NOT_FOUND":
                    view.showError(LoginContract.Error.ERROR_EMAIL_DOES_NOT_EXIST);
                    break;
                default:
                    view.showError(LoginContract.Error.ERROR_AUTHORIZATION);
            }
        }
    }
}
