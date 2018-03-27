package com.centennialcollege.brogrammers.businesschatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.centennialcollege.brogrammers.businesschatapp.chat.ChatsPagerAdapter;
import com.centennialcollege.brogrammers.businesschatapp.chat.GroupChatActivity;
import com.centennialcollege.brogrammers.businesschatapp.contacts.ContactsActivity;
import com.centennialcollege.brogrammers.businesschatapp.contacts.MyContactsActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The main chat screen where all messages sent by all users are visible.
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If the user is not logged in anymore, navigate to the Login Screen.
        if (!isUserLoggedIn()) {
            launchLoginActivity();
        }
    }

    /**
     * Initialize stuff.
     */
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        ChatsPagerAdapter adapter = new ChatsPagerAdapter(getSupportFragmentManager());

        ViewPager chatsViewPager = findViewById(R.id.chats_view_pager);
        chatsViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout = findViewById(R.id.layout_tabs);
        tabLayout.setupWithViewPager(chatsViewPager);
    }

    /**
     * Checks if the user is still logged in or not.
     * @return : True if logged in, false otherwise.
     */
    private boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private void launchLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                // Sign out the current user and navigate to Login Screen.
                firebaseAuth.signOut();
                launchLoginActivity();
                return true;
            case R.id.menu_contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            case R.id.menu_my_contacts:
                startActivity(new Intent(this, MyContactsActivity.class));
                return true;
            case R.id.menu_my_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.menu_new_group_chat:
                startActivity(new Intent(this, GroupChatActivity.class));
                return true;
        }
        return true;
    }
}
