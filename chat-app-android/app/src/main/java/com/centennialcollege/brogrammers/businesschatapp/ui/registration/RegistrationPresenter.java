package com.centennialcollege.brogrammers.businesschatapp.ui.registration;

import android.text.TextUtils;

import com.centennialcollege.brogrammers.businesschatapp.data.DataManager;
import com.centennialcollege.brogrammers.businesschatapp.data.DataManagerImpl;
import com.centennialcollege.brogrammers.businesschatapp.model.User;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.MINIMUM_PASSWORD_LENGTH;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.MINIMUM_USERNAME_LENGTH;


class RegistrationPresenter implements RegistrationContract.Presenter {

    private RegistrationContract.View view;
    private DataManager dataManager;

    RegistrationPresenter() {
        dataManager = new DataManagerImpl();
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

        User newUser = new User(username, email);

        dataManager.createUser(newUser, password)
                .addOnSuccessListener(aVoid -> {
                    view.hideProgress();

                    view.launchMainActivity();
                })
                .addOnFailureListener(e -> {
                    view.hideProgress();

                    switch (dataManager.getFirebaseException(e)) {
                        case ERROR_INVALID_EMAIL:
                            view.showError(RegistrationContract.Error.ERROR_EMAIL_INVALID);
                            break;
                        case ERROR_EMAIL_ALREADY_IN_USE:
                        case ERROR_CREDENTIAL_ALREADY_IN_USE:
                        case ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL:
                            view.showError(RegistrationContract.Error.ERROR_EMAIL_EXISTS);
                            break;
                        default:
                            view.showError(RegistrationContract.Error.ERROR_REGISTRATION);
                    }
                });
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

}