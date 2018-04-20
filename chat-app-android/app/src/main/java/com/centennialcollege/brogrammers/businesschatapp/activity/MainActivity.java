package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.centennialcollege.brogrammers.businesschatapp.util.UserAttributesUtils;
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

    private TextView tvPlaceholderAvatar;
    private CardView cvAvatar;
    private ImageView ivAvatar;

    private View headerView;

    private User currentUser;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_chat:
                if (tabLayout.getSelectedTabPosition() == 0) {
                    Intent intent = new Intent(this, MyContactsActivity.class);
                    intent.putExtra(Constants.ACTIVITY_TITLE, getString(R.string.new_chat));
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, GroupChatActivity.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize stuff.
     */
    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        headerView = navigationView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.tv_user_email);
        userEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        tvPlaceholderAvatar = headerView.findViewById(R.id.tv_placeholder_avatar);
        cvAvatar = headerView.findViewById(R.id.cv_avatar);
        ivAvatar = headerView.findViewById(R.id.iv_avatar);

        initUserData();
    }

    private void initUserData() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
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
                    currentUser = dataSnapshot.getValue(User.class);
                    currentUser.setId(dataSnapshot.getKey());
                    if (currentUser == null) {
                        return;
                    }

                    ((TextView) headerView.findViewById(R.id.tv_username)).setText(currentUser.getUsername());
                    ((TextView) headerView.findViewById(R.id.tv_user_email)).setText(currentUser.getEmail());

                    if (currentUser.getAvatar()) {
                        cvAvatar.setVisibility(View.VISIBLE);
                        Glide.with(MainActivity.this)
                                .load(currentUser.getAvatarURL())
                                .centerCrop()
                                .into(ivAvatar);
                        tvPlaceholderAvatar.setVisibility(View.GONE);
                    } else {
                        cvAvatar.setVisibility(View.GONE);
                        tvPlaceholderAvatar.setVisibility(View.VISIBLE);
                        UserAttributesUtils.setAccountColor(tvPlaceholderAvatar, currentUser.getUsername(), MainActivity.this);
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
            case R.id.nav_my_profile:
                launchProfileScreen();
                break;
            case R.id.nav_contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                break;
            case R.id.nav_my_contacts:
                startActivity(new Intent(this, MyContactsActivity.class));
                break;
            case R.id.nav_sign_out:
                // Sign out the current user and navigate to Login Screen.
                firebaseAuth.signOut();
                launchLoginActivity();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onAvatarClicked(View view) {
        launchProfileScreen();
    }

    private void launchProfileScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(Constants.USER_AVATAR_URL, currentUser.getAvatarURL());
        startActivity(intent);
    }
}
