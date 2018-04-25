package com.centennialcollege.brogrammers.businesschatapp.ui.registration;

import android.support.v4.app.Fragment;

import com.centennialcollege.brogrammers.businesschatapp.ui.SingleFragmentActivity;

public class RegistrationActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new RegistrationFragment();
    }

}
