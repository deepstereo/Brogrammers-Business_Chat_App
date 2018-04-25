package com.centennialcollege.brogrammers.businesschatapp.ui.profile;

import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.centennialcollege.brogrammers.businesschatapp.ui.BasePresenter;

interface ProfileContract {

    enum Error {
        ERROR_EMAIL_REQUIRED,
        ERROR_USERNAME_REQUIRED,
        ERROR_EMAIL_INVALID,
        ERROR_UPDATE_USER_INFO,
        ERROR_USERNAME_TOO_SHORT,
        ERROR_EMAIL_ALREADY_IN_USE,
        ERROR_GET_USER_INFO,
        ERROR_WRONG_PASSWORD,
        ERROR_PASSWORD_REQUIRED,
        ERROR_ALL_FIELDS_REQUIRED,
        ERROR_NEW_PASSWORD_TOO_SHORT,
        ERROR_PASSWORDS_NOT_MATCHING
    }

    interface View {

        void showError(Error errorCode);

        void hideErrors();

        void showProgress();

        void hideProgress();

        void showUpdateSuccessMsg();

        void setUserInfo(User user);

        void setUserInfoReadOnly();

        String getUserId();

        void showReAuthenticationDialog();

        void closeReAuthenticationDialog();

        void showChangePasswordDialog();

        void closeChangePasswordDialog();
    }

    interface Presenter extends BasePresenter<View> {


        void attemptChangeUserInfo(String username, String email);

        void attemptReAuthentication(String password);

        void attemptChangePassword(String oldPassword, String newPassword, String newConfirmPassword);
    }

}
