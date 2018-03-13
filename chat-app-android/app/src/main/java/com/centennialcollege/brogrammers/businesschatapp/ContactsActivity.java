package com.centennialcollege.brogrammers.businesschatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ContactsActivity extends AppCompatActivity {
    private static final String TAG = ContactsActivity.class.getSimpleName();
    private static final String USERS_CHILD = "users-android-test";

    private FirebaseListAdapter<User> adapter;
    private ArrayList<String> selectedContacts;
    private ListView listOfContacts;
    private FirebaseAuth firebaseAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        init();

        displayContacts();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        selectedContacts = new ArrayList<>();
        listOfContacts = findViewById(R.id.lv_contacts);
    }

    private void displayContacts() {
        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.contact_list_item, FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)) {
            @Override
            protected void populateView(View v, final User model, int position) {
                TextView tvUsername = v.findViewById(R.id.tv_username);
                TextView tvEmail = v.findViewById(R.id.tv_email);
                final ImageView ivTick = v.findViewById(R.id.iv_tick);

                tvUsername.setText("Username: " + model.getUsername());
                tvEmail.setText("Email: " + model.getEmail());

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedContacts.contains(model.getId())) {
                            selectedContacts.remove(model.getId());
                            ivTick.setVisibility(View.INVISIBLE);
                        } else {
                            selectedContacts.add(model.getId());
                            ivTick.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        listOfContacts.setAdapter(adapter);
    }

    void addContacts(){
        Toast.makeText(getApplicationContext(), "Added",Toast.LENGTH_SHORT).show();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String currentUserId = user.getUid();
            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(currentUserId)
                    .child("contactList").setValue(selectedContacts);
        }
    }

    public void onAddContactsButtonClicked(View view) {
        addContacts();
    }
}
