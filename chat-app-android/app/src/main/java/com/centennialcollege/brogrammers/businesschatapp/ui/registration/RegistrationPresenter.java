package com.centennialcollege.brogrammers.businesschatapp.ui.registration;

import android.text.TextUtils;

import com.centennialcollege.brogrammers.businesschatapp.data.authorization.AuthorizationContract;
import com.centennialcollege.brogrammers.businesschatapp.data.authorization.AuthorizationModel;
import com.centennialcollege.brogrammers.businesschatapp.data.db.DbContract;
import com.centennialcollege.brogrammers.businesschatapp.data.db.DbModel;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.MINIMUM_PASSWORD_LENGTH;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.MINIMUM_USERNAME_LENGTH;


class RegistrationPresenter implements RegistrationContract.Presenter,
        AuthorizationContract.Presenter, DbContract.Presenter {

    private RegistrationContract.View view;
    private AuthorizationContract.Model authorizationModel;
    private DbContract.Model dbModel;

    private String currentUsername = "";

    RegistrationPresenter() {
        authorizationModel = new AuthorizationModel(this);
        dbModel = new DbModel(this);
    }

    @Override
    public void takeView(RegistrationContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    @Override
    public void attemptRegistration(String username, String email, String password, String passwordConfirm) {
        view.hideErrors();

        username = username.trim();
        email = email.trim();
        password = password.trim();
        passwordConfirm = passwordConfirm.trim();

        if (TextUtils.isEmpty(username)) {
            view.showError(RegistrationContract.Error.ERROR_USERNAME_REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            view.showError(RegistrationContract.Error.ERROR_EMAIL_REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            view.showError(RegistrationContract.Error.ERROR_PASSWORD_REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            view.showError(RegistrationContract.Error.ERROR_PASSWORD_CONFIRMATION_REQUIRED);
            return;
        }

        if (!isEmailValid(email)) {
            view.showError(RegistrationContract.Error.ERROR_EMAIL_INVALID);
            return;
        }

        if (!isUsernameLongEnough(username)) {
            view.showError(RegistrationContract.Error.ERROR_USERNAME_TOO_SHORT);
            return;
        }

        if (!isPasswordLongEnough(password)) {
            view.showError(RegistrationContract.Error.ERROR_PASSWORD_TOO_SHORT);
            return;
        }

        if (!password.equals(passwordConfirm)) {
            view.showError(RegistrationContract.Error.ERROR_PASSWORDS_NOT_MATCHING);
            return;
        }

        view.showProgress();

        currentUsername = username;
        authorizationModel.createUser(email, password);
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameLongEnough(String username) {
        return username.length() >= MINIMUM_USERNAME_LENGTH;
    }

    private boolean isPasswordLongEnough(String password) {
        return password.length() >= MINIMUM_PASSWORD_LENGTH;
    }

    @Override
    public void onSuccessAuthorization(FirebaseUser firebaseUser) {
        addAdditionalUserInfoToDb(firebaseUser);
    }

    private void addAdditionalUserInfoToDb(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                currentUsername, email);

        dbModel.addAdditionalUserInfoToDb(userId, user);
    }

    @Override
    public void onFailureAuthorization(String errorCode) {
        view.hideProgress();

        if (errorCode != null) {
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    view.showError(RegistrationContract.Error.ERROR_EMAIL_INVALID);
                    break;
                case "ERROR_EMAIL_ALREADY_IN_USE":
                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                    view.showError(RegistrationContract.Error.ERROR_EMAIL_EXISTS);
                    break;
                default:
                    view.showError(RegistrationContract.Error.ERROR_REGISTRATION);
            }
        }
    }

    @Override
    public void onDbSuccess() {
        view.hideProgress();

        view.launchMainActivity();
    }

    @Override
    public void onDbFailure() {
        authorizationModel.deleteCurrentUser();

        view.hideProgress();

        view.showError(RegistrationContract.Error.ERROR_REGISTRATION);
    }
}