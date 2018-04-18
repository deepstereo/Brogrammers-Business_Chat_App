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
        ERROR_PASSWORD_REQUIRED
    }

    interface View {

        void showError(Error errorCode);

        void hideErrors();

        void showProgress();

        void hideProgress();

        void showUpdateSuccessMsg();

        void setUserInfo(User user);

        void showReAuthenticationDialog();

        void closeReAuthenticationDialog();
    }

    interface Presenter extends BasePresenter<View> {


        void attemptChangeUserInfo(String username, String email);

        void attemptReAuthentication(String password);
    }

}
