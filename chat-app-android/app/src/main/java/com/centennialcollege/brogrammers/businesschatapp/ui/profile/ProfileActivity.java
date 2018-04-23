package com.centennialcollege.brogrammers.businesschatapp.ui.profile;

import android.support.v4.app.Fragment;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.ui.SingleFragmentActivity;

public class ProfileActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ProfileFragment.getInstance(getIntent().getStringExtra(Constants.USER_ID));
    }
}
