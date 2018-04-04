package com.centennialcollege.brogrammers.businesschatapp.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.ChatListItem;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
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
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.CHATS_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.CHAT_NAME;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.MESSAGES_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatsFragment extends Fragment {

    public static final String TITLE = "Groups";

    private FirebaseAuth firebaseAuth;
    private Map<String, Boolean> activeGroupChatIds;

    private RecyclerView mChatsRecyclerView;
    private ChatsRecyclerViewAdapter chatsRecyclerViewAdapter;

    private ArrayList<ChatListItem> chatListItems;

    public GroupChatsFragment() {
        // Required empty public constructor
    }

    public static GroupChatsFragment newInstance() {
        return new GroupChatsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_chats, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mChatsRecyclerView = rootView.findViewById(R.id.rv_chats);
        activeGroupChatIds = new HashMap<>();
        chatListItems = new ArrayList<>();

        fetchActiveGroupChatIds();
        setupRecyclerView();
        return rootView;
    }

    private void fetchActiveGroupChatIds() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)
                .child(firebaseUser.getUid()).child(Constants.ACTIVE_GROUP_CHATS);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Boolean> chatIds = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (chatIds != null) {
                    activeGroupChatIds.putAll(chatIds);
                }

                if (activeGroupChatIds != null && activeGroupChatIds.size() > 0) {
                    for (String activePersonalChatId : activeGroupChatIds.keySet()) {
                        ChatListItem chatListItem = new ChatListItem();
                        chatListItem.setChatId(activePersonalChatId);
                        populateChatName(chatListItem);
                    }
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void populateChatName(final ChatListItem chatListItem) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(CHATS_CHILD)
                .child(chatListItem.getChatId()).child(CHAT_NAME);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatName = dataSnapshot.getValue(String.class);
                chatListItem.setChatName(chatName);
                populateLastMessage(chatListItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void populateLastMessage(final ChatListItem chatListItem) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD)
                .child(chatListItem.getChatId());

        Query query = ref.orderByKey().limitToLast(1);

        // Attach a listener to read the data at our posts reference
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // FixMe : Json data being received is not being parsed properly into a Message object.
                Message message = dataSnapshot.getValue(Message.class);
//                if (message != null) {
//                    chatListItem.setLastMessage(message.getMessageText());
                chatListItems.add(chatListItem);

                if (chatListItems.size() == activeGroupChatIds.size()) {
                    chatsRecyclerViewAdapter.notifyDataSetChanged();
                }
//                }
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
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());

        chatsRecyclerViewAdapter = new ChatsRecyclerViewAdapter(chatListItems, getContext());

        mChatsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChatsRecyclerView.setAdapter(chatsRecyclerViewAdapter);
    }

}
