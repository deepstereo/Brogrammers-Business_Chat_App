package com.centennialcollege.brogrammers.businesschatapp.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.contacts.ContactsRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.Chat;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;

public class GroupChatActivity extends AppCompatActivity {

    private Map<String, Boolean> selectedContacts;
    private FirebaseAuth firebaseAuth;
    private Map<String, Boolean> myContactsId;
    private ArrayList<User> myContacts;

    private RecyclerView mContactsRecyclerView;
    private ContactsRecyclerViewAdapter contactsRecyclerViewAdapter;
    private EditText etGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        init();
        fetchMyContactsIds();
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
        etGroupName = findViewById(R.id.et_group_name);
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
                    contactsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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

    public void onStartGroupChatButtonClicked(View view) {

        if (!areGroupCreationRequirementsBeingMet()) {
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        // Create a new chat and add all selected members
        Map<String, Boolean> members = new LinkedHashMap<>();
        members.put(currentUserId, true);
        for (String userId : selectedContacts.keySet()) {
            members.put(userId, true);
        }

        // Todo: Change the Chat name later on as per discussion.
        Chat newChat = new Chat(etGroupName.getText().toString(), true, members);

        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS_CHILD);
        String newChatId = chatReference.push().getKey();
        chatReference.child(newChatId).setValue(newChat);

        addChatIdInActiveGroupChats(newChatId, members.keySet());

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.KEY_CHAT_ID, newChatId);
        startActivity(intent);
    }

    private void addChatIdInActiveGroupChats(String newChatId, Set<String> members) {
        // Add the chat id in the list of active group chats of all group members.
        for (String userId : members) {
            updateActiveGroupChatsForUser(newChatId, userId);
        }
    }

    private void updateActiveGroupChatsForUser(final String newChatId, String userId) {
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_CHILD)
                .child(userId).child(Constants.ACTIVE_GROUP_CHATS);


        // Attach a listener to read the data at our posts reference
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Boolean> activeGroupChats = (HashMap<String, Boolean>) dataSnapshot.getValue();

                if (activeGroupChats == null) {
                    activeGroupChats = new LinkedHashMap<>();
                }

                activeGroupChats.put(newChatId, true);
                userReference.setValue(activeGroupChats);
                userReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private boolean areGroupCreationRequirementsBeingMet() {
        if (TextUtils.isEmpty(etGroupName.getText())) {
            Toast.makeText(this, "Please provide a Group Name.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (selectedContacts.size() == 0) {
            Toast.makeText(this, "Please select atleast one other Group member.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        contactsRecyclerViewAdapter.stopListening();
    }
}
