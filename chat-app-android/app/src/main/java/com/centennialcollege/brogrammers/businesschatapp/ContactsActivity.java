package com.centennialcollege.brogrammers.businesschatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;


public class ContactsActivity extends AppCompatActivity {
    private static final String USERS_CHILD = "users-android-test";

    private FirebaseListAdapter<User> adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        displayContacts();
    }

    private void displayContacts() {
        ListView listOfContacts = findViewById(R.id.lv_contacts);
        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.contact_list_item, FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)) {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView tvUsername = v.findViewById(R.id.tv_username);
                TextView tvEmail = v.findViewById(R.id.tv_email);

                tvUsername.setText("Username: " + model.getUsername());
                tvEmail.setText("Email: " + model.getEmail());
            }
        };
        listOfContacts.setAdapter(adapter);
    }
}
