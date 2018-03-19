package com.centennialcollege.brogrammers.businesschatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyContactsActivity extends AppCompatActivity {

    private static final String USERS_CHILD = "users-android-test";

    private FirebaseListAdapter<User> adapter;
    private ListView listOfContacts;
    private FirebaseAuth firebaseAuth;

    private ArrayList<String> myContactsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contacts);

        init();
        fetchMyContactsIds();

    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        listOfContacts = findViewById(R.id.lv_contacts);
        myContactsId = new ArrayList<>();
    }

    private void fetchMyContactsIds() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(firebaseUser.getUid());

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    myContactsId = user.getContactList();
                }

                displayMyContacts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void displayMyContacts() {
        adapter = new FirebaseListAdapter<User>(this, User.class,
                R.layout.contact_list_item, FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)) {
            @Override
            protected void populateView(View v, final User model, int position) {
                if(!myContactsId.contains(model.getId())) {
                    v.setVisibility(View.GONE);
                } else {
                    TextView tvUsername = v.findViewById(R.id.tv_username);
                    TextView tvEmail = v.findViewById(R.id.tv_email);
                    final ImageView ivTick = v.findViewById(R.id.iv_tick);

                    tvUsername.setText("Username: " + model.getUsername());
                    tvEmail.setText("Email: " + model.getEmail());


                }
            }
        };
        listOfContacts.setAdapter(adapter);
    }
}
