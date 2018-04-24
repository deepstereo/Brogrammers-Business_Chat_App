package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.MyContactsRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.USER_CONTACT_LIST;

public class MyContactsActivity extends AppCompatActivity {

    public static final int ACTION_NEW_CHAT = 1;
    public static final int ACTION_MY_CONTACTS_INFO = 2;

    private FirebaseAuth firebaseAuth;
    private Map<String, Boolean> myContactsId;

    private RecyclerView mContactsRecyclerView;
    private MyContactsRecyclerViewAdapter myContactsRecyclerViewAdapter;

    private ArrayList<User> myContacts;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contacts);

        // Set a different title in case user navigated here from new chat menu item or from my contacts nav menu item.
        if (getIntent().getIntExtra(Constants.MY_CONTACTS_ACTIVITY_ACTION, 0) == ACTION_NEW_CHAT) {
            setTitle(R.string.new_chat);
        } else if (getIntent().getIntExtra(Constants.MY_CONTACTS_ACTIVITY_ACTION, 0) == ACTION_MY_CONTACTS_INFO) {
            setTitle(R.string.my_contacts_title);
        }

        init();
        fetchMyContactsIds();
        fetchCurrentUser();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        mContactsRecyclerView = findViewById(R.id.rv_contacts);
        myContactsId = new HashMap<>();
        myContacts = new ArrayList<>();
    }

    private void fetchMyContactsIds() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)
                .child(firebaseUser.getUid()).child(USER_CONTACT_LIST);

        // Attach a listener to read the data at our posts reference
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String myContactId = dataSnapshot.getKey();
                if (!TextUtils.isEmpty(myContactId)) {
                    myContactsId.put(myContactId, true);
                    fetchMyContact(myContactId);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String myContactId = dataSnapshot.getKey();
                if (!TextUtils.isEmpty(myContactId) && myContactsId.containsKey(myContactId)) {
                    myContactsId.remove(myContactId);
                    User tempUser = new User();
                    tempUser.setId(myContactId);
                    myContacts.remove(tempUser);
                    myContactsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void fetchMyContact(String contactId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(contactId);

        // Attach a listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setId(dataSnapshot.getKey());
                myContacts.add(user);

                if (myContacts.size() == myContactsId.size()) {
                    myContactsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void fetchCurrentUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)
                .child(firebaseAuth.getCurrentUser().getUid());

        // Attach a listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                currentUser.setId(dataSnapshot.getKey());
                setupRecyclerView();
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

        // Send different actions based on whether user navigated here from new chat menu item or from my contacts nav menu item.
        if (getIntent().getIntExtra(Constants.MY_CONTACTS_ACTIVITY_ACTION, 0) == ACTION_NEW_CHAT) {
            myContactsRecyclerViewAdapter = new MyContactsRecyclerViewAdapter(currentUser, myContacts, this, ACTION_NEW_CHAT);
        } else if (getIntent().getIntExtra(Constants.MY_CONTACTS_ACTIVITY_ACTION, 0) == ACTION_MY_CONTACTS_INFO) {
            myContactsRecyclerViewAdapter = new MyContactsRecyclerViewAdapter(currentUser, myContacts, this, ACTION_MY_CONTACTS_INFO);
        }

        mContactsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mContactsRecyclerView.setAdapter(myContactsRecyclerViewAdapter);
    }

}
