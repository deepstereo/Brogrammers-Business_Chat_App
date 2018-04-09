package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.UserContactsRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;

public class UserContactsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Map<String, Boolean> myContactsId;

    private RecyclerView mContactsRecyclerView;
    private UserContactsRecyclerViewAdapter userContactsRecyclerViewAdapter;

    private ArrayList<User> myContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contacts);

        init();
        fetchMyContactsIds();
        setupRecyclerView();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        mContactsRecyclerView = findViewById(R.id.rv_contacts);
        myContactsId = new HashMap<>();
        myContacts = new ArrayList<>();
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
                    myContactsId.putAll(user.getContactList());

                    for (String myContactId : user.getContactList().keySet()) {
                        fetchUser(myContactId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void fetchUser(String contactId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(contactId);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                myContacts.add(user);

                if (myContacts.size() == myContactsId.size()) {
                    userContactsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Setup the recycler view to display the list of my contacts from the firebase database.
     */
    private void setupRecyclerView() {
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        userContactsRecyclerViewAdapter = new UserContactsRecyclerViewAdapter(myContacts, this);

        mContactsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mContactsRecyclerView.setAdapter(userContactsRecyclerViewAdapter);
    }

}
