package com.centennialcollege.brogrammers.businesschatapp.ui.profile;

import android.text.TextUtils;

import com.centennialcollege.brogrammers.businesschatapp.data.DataManager;
import com.centennialcollege.brogrammers.businesschatapp.data.DataManagerImpl;
import com.centennialcollege.brogrammers.businesschatapp.model.User;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.MINIMUM_PASSWORD_LENGTH;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.MINIMUM_USERNAME_LENGTH;


class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileContract.View view;
    private DataManager dataManager;
    private String username;
    private String email;

    ProfilePresenter() {
        dataManager = new DataManagerImpl();
    }

    @Override
    public void takeView(ProfileContract.View view) {
        this.view = view;
        updateUserInfo();
    }


    @Override
    public void dropView() {
        this.view = null;
    }

    boolean isViewNotNull() {
        return view != null;
    }

    private void updateUserInfo() {
        if (isViewNotNull() && !TextUtils.isEmpty(view.getUserId())) {
            view.showProgress();
            dataManager.getUserInfo(new DataManager.GetUserInfoCallback() {
                @Override
                public void onSuccess(User user) {
                    if (isViewNotNull()) {
                        view.hideProgress();

                        view.setUserInfo(user);

                        // Setting read only here so that click listeners on profile image holding views
                        // are set before we reach here.
                        if (!TextUtils.equals(view.getUserId(), dataManager.getFirebaseAuthHelper().getCurrentUser().getUid())) {
                            view.setUserInfoReadOnly();
                        }
                    }
                }

                @Override
                public void onFailure() {
                    if (isViewNotNull()) {
                        view.hideProgress();

                        view.showError(ProfileContract.Error.ERROR_GET_USER_INFO);
                    }
                }
            }, view.getUserId());
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameLongEnough(String username) {
        return username.length() >= MINIMUM_USERNAME_LENGTH;
    }

    @Override
    public void attemptChangeUserInfo(String username, String email) {
        view.hideErrors();

        username = username.trim();
        email = email.trim();

        if (TextUtils.isEmpty(email)) {
            view.showError(ProfileContract.Error.ERROR_EMAIL_REQUIRED);
            return;
        }

        if (!isEmailValid(email)) {
            view.showError(ProfileContract.Error.ERROR_EMAIL_INVALID);
            return;
        }

        if (TextUtils.isEmpty(username)) {
            view.showError(ProfileContract.Error.ERROR_USERNAME_REQUIRED);
            return;
        }

        if (!isUsernameLongEnough(username)) {
            view.showError(ProfileContract.Error.ERROR_USERNAME_TOO_SHORT);
            return;
        }

        view.showProgress();

        DataManager.ReplaceUserEmailAndUsernameCallback replaceCallback =
                new DataManager.ReplaceUserEmailAndUsernameCallback() {
                    @Override
                    public void onSuccess() {
                        view.hideProgress();

                        view.showUpdateSuccessMsg();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        view.hideProgress();

                        switch (dataManager.getFirebaseException(e)) {
                            case ERROR_INVALID_EMAIL:
                                view.showError(ProfileContract.Error.ERROR_EMAIL_INVALID);
                                break;
                            case ERROR_EMAIL_ALREADY_IN_USE:
                                view.showError(ProfileContract.Error.ERROR_EMAIL_ALREADY_IN_USE);
                                break;
                            case ERROR_REQUIRES_RECENT_LOGIN:
                                view.showReAuthenticationDialog();
                                break;
                            default:
                                view.showError(ProfileContract.Error.ERROR_UPDATE_USER_INFO);
                        }
                    }
                };

        this.username = username;
        this.email = email;

        dataManager.replaceCurrentUserEmailAndUsername(email, username, replaceCallback);
    }

    @Override
    public void attemptReAuthentication(String password) {
        if (TextUtils.isEmpty(password)) {
            view.showError(ProfileContract.Error.ERROR_PASSWORD_REQUIRED);
            return;
        }

        dataManager.getFirebaseAuthHelper()
                .reAuthentication(password)
                .addOnSuccessListener(aVoid -> {
                    view.closeReAuthenticationDialog();
                    attemptChangeUserInfo(username, email);
                })
                .addOnFailureListener(e ->
                        view.showError(ProfileContract.Error.ERROR_WRONG_PASSWORD));
    }

    @Override
    public void attemptChangePassword(String oldPassword, String newPassword, String newConfirmPassword) {
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newConfirmPassword)) {
            view.showError(ProfileContract.Error.ERROR_ALL_FIELDS_REQUIRED);
            return;
        }

        if (!isPasswordLongEnough(newPassword)) {
            view.showError(ProfileContract.Error.ERROR_NEW_PASSWORD_TOO_SHORT);
            return;
        }

        if (!newPassword.equals(newConfirmPassword)) {
            view.showError(ProfileContract.Error.ERROR_PASSWORDS_NOT_MATCHING);
            return;
        }

        dataManager
                .updatePassword(oldPassword, newPassword)
                .addOnSuccessListener(aVoid -> {
                    view.closeChangePasswordDialog();
                    view.showUpdateSuccessMsg();
                })
                .addOnFailureListener(e ->
                        view.showError(ProfileContract.Error.ERROR_WRONG_PASSWORD));
    }

    private boolean isPasswordLongEnough(String password) {
        return password.length() >= MINIMUM_PASSWORD_LENGTH;
    }

}
