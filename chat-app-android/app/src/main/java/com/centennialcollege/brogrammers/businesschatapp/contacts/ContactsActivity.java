package com.centennialcollege.brogrammers.businesschatapp.contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;


public class ContactsActivity extends AppCompatActivity {
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private Map<String, Boolean> selectedContacts;
    private FirebaseAuth firebaseAuth;

    private RecyclerView mContactsRecyclerView;
    private ContactsRecyclerViewAdapter contactsRecyclerViewAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        init();
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        contactsRecyclerViewAdapter.startListening();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        selectedContacts = new HashMap<>();
        mContactsRecyclerView = findViewById(R.id.rv_contacts);
    }

    /**
     * Setup the recycler view to display the list of all contacts from the firebase database.
     */
    private void setupRecyclerView() {
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        Query query = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_CHILD);

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        contactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(options, selectedContacts);

        mContactsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mContactsRecyclerView.setAdapter(contactsRecyclerViewAdapter);
    }

    void addContacts(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String currentUserId = user.getUid();
            FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(currentUserId)
                    .child("contactList").setValue(selectedContacts);
            Toast.makeText(getApplicationContext(), "Added",Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddContactsButtonClicked(View view) {
        addContacts();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        contactsRecyclerViewAdapter.stopListening();
    }
}
