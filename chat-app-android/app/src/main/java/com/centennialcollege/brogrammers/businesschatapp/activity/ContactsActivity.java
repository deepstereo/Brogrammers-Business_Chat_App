package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.ContactsRecyclerViewAdapter;
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
import java.util.List;
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;


public class ContactsActivity extends AppCompatActivity {
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private Map<String, Boolean> selectedContacts;
    private List<User> allUsers;
    private FirebaseAuth firebaseAuth;

    private RecyclerView mContactsRecyclerView;
    private ContactsRecyclerViewAdapter contactsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        init();
        fetchAllUsers();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        selectedContacts = new HashMap<>();
        mContactsRecyclerView = findViewById(R.id.rv_contacts);
        allUsers = new ArrayList<>();
    }

    /**
     * Setup the recycler view to display the list of all contacts from the firebase database.
     */
    private void setupRecyclerView() {
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        contactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(allUsers, selectedContacts);

        mContactsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mContactsRecyclerView.setAdapter(contactsRecyclerViewAdapter);
    }

    private void fetchAllUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_CHILD);

        // Attach a listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            user.setId(snapshot.getKey());
                            if (!TextUtils.equals(firebaseAuth.getCurrentUser().getUid(), user.getId())) {
                                allUsers.add(user);
                            }
                        }
                        setupRecyclerView();
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

    void addContacts() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String currentUserId = user.getUid();
            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(currentUserId)
                    .child("contactList").setValue(selectedContacts);
        }
    }

    public void onAddContactsButtonClicked(View view) {
        addContacts();
        finish();
    }

}
