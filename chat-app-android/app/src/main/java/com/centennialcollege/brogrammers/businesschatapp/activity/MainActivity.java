package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.ChatsPagerAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.centennialcollege.brogrammers.businesschatapp.ui.login.LoginActivity;
import com.centennialcollege.brogrammers.businesschatapp.ui.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * The main chat screen where all messages sent by all users are visible.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private TabLayout tabLayout;
    private DrawerLayout drawer;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        ChatsPagerAdapter adapter = new ChatsPagerAdapter(getSupportFragmentManager());

        ViewPager chatsViewPager = findViewById(R.id.chats_view_pager);
        chatsViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout = findViewById(R.id.layout_tabs);
        tabLayout.setupWithViewPager(chatsViewPager);

        TextView userEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_user_email);
        userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        initUserData();
    }

    private void initUserData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;
        String userId = firebaseUser.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS_CHILD)
                .child(userId);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) return;

                    ((TextView) findViewById(R.id.tv_username)).setText(user.getUsername());
                    ((TextView) findViewById(R.id.tv_user_email)).setText(user.getEmail());

                    if (user.getAvatarURL().length() > 0) {
                        Glide.with(MainActivity.this)
                                .load(user.getAvatarURL())
                                .centerCrop()
                                .into((ImageView) findViewById(R.id.iv_user_image));
                    }
                } catch (Exception e) {
                    System.out.println("The read failed: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Checks if the user is still logged in or not.
     *
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_sign_out:
                // Sign out the current user and navigate to Login Screen.
                firebaseAuth.signOut();
                launchLoginActivity();
                break;
            case R.id.nav_contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                break;
            case R.id.nav_my_contacts:
                startActivity(new Intent(this, MyContactsActivity.class));
                break;
            case R.id.nav_my_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_new_group_chat:
                startActivity(new Intent(this, GroupChatActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
