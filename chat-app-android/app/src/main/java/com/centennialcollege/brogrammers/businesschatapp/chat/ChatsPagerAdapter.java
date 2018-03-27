package com.centennialcollege.brogrammers.businesschatapp.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Adapter class for the viewpager displaying the Personal Chats and Group Chats in adjacent tabs.
 */

public class ChatsPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_TABS = 2;

    private static final String TAG = ChatsPagerAdapter.class.getSimpleName();

    public ChatsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PersonalChatsFragment.newInstance();
        } else {
            return GroupChatsFragment.newInstance();
        }
    }

    // This determines the title for each tab.
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on TAB position
        if (position == 0) {
            return PersonalChatsFragment.TITLE;
        } else {
            return GroupChatsFragment.TITLE;
        }
    }
}
