package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.ChatMembersRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;

public class ChatInfoActivity extends AppCompatActivity {

    private Map<String, Boolean> chatMembersId;

    private RecyclerView membersRecyclerView;
    private ChatMembersRecyclerViewAdapter membersRecyclerViewAdapter;

    private ArrayList<User> chatMembers;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        init();
        fetchChatMembersIds();
        setupRecyclerView();
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);
        membersRecyclerView = findViewById(R.id.rv_contacts);
        chatMembersId = new HashMap<>();
        chatMembers = new ArrayList<>();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchChatMembersIds() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHATS_CHILD).child(chatId).child(Constants.CHATS_MEMBERS);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    chatMembersId = (Map<String, Boolean>) dataSnapshot.getValue();

                    for (String chatMemberId : chatMembersId.keySet()) {
                        fetchChatMembers(chatMemberId);
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

    private void fetchChatMembers(String contactId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD).child(contactId);

        // Attach a listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setId(dataSnapshot.getKey());
                chatMembers.add(user);

                if (chatMembers.size() == chatMembersId.size()) {
                    membersRecyclerViewAdapter.notifyDataSetChanged();
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

        membersRecyclerViewAdapter = new ChatMembersRecyclerViewAdapter(chatMembers);

        membersRecyclerView.setLayoutManager(mLinearLayoutManager);
        membersRecyclerView.setAdapter(membersRecyclerViewAdapter);
    }

}
