package com.centennialcollege.brogrammers.businesschatapp.ui.login;

import android.support.v4.app.Fragment;

import com.centennialcollege.brogrammers.businesschatapp.ui.SingleFragmentActivity;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

}
