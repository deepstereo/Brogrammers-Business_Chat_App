package com.centennialcollege.brogrammers.businesschatapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.ChatsRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.ChatListItem;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.CHATS_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.CHAT_NAME;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.MESSAGES_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;

/**
 * Fragment to show the list of all active personal chats between the currently logged in user and other users.
 */
public class PersonalChatsFragment extends Fragment {

    public static final String TITLE = "Chats";

    private FirebaseAuth firebaseAuth;
    private Map<String, Boolean> activePersonalChatIds;

    private RecyclerView mChatsRecyclerView;
    private com.centennialcollege.brogrammers.businesschatapp.adapter.ChatsRecyclerViewAdapter chatsRecyclerViewAdapter;

    private ArrayList<ChatListItem> chatListItems;

    private User currentUser;

    public PersonalChatsFragment() {
        // Required empty public constructor
    }

    public static PersonalChatsFragment newInstance() {
        return new PersonalChatsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_personal_chats, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mChatsRecyclerView = rootView.findViewById(R.id.rv_chats);
        activePersonalChatIds = new HashMap<>();
        chatListItems = new ArrayList<>();

        fetchCurrentUser();
//        fetchActivePersonalChatIds();
        setupRecyclerView();
        return rootView;
    }

    private void fetchCurrentUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)
                .child(firebaseAuth.getCurrentUser().getUid());

        // Attach a listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                fetchActivePersonalChatIds();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void fetchActivePersonalChatIds() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_CHILD)
                .child(firebaseUser.getUid()).child(Constants.ACTIVE_PERSONAL_CHATS);

        // Attach a listener to read and observe the list of personal chat Ids.
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String chatId = dataSnapshot.getKey();
                if (!TextUtils.isEmpty(chatId)) {
                    activePersonalChatIds.put(chatId, true);
                }

                if (activePersonalChatIds != null && activePersonalChatIds.size() > 0) {
                    ChatListItem chatListItem = new ChatListItem();
                    chatListItem.setChatId(chatId);
                    populateChatName(chatListItem);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Do Nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String chatId = dataSnapshot.getKey();
                activePersonalChatIds.remove(chatId);
                ChatListItem chatToRemove = null;
                for (ChatListItem item : chatListItems) {
                    if (TextUtils.equals(item.getChatId(), chatId)) {
                        chatToRemove = item;
                        break;
                    }
                }
                chatListItems.remove(chatToRemove);
                chatsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Todo: Think of what could trigger this.
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
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatName = dataSnapshot.getValue(String.class);
                chatListItem.setChatName(chatName.replace(currentUser.getUsername(), ""));
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

        Query query = ref.orderByChild("messageTime").limitToLast(1);

        // Attach a listener to read the data at our posts reference
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("PERSONAL_CHATS_FRAGMENT", "Listener CALLED with chatId : " + chatListItem.getChatId());
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    Message message = dataSnapshot.getChildren().iterator().next().getValue(Message.class);
                    if (message != null) {
                        // If it's a new chat item, then add it in the list, otherwise, just modify
                        // the last message text in existing chat item and refresh adapter.
                        chatListItem.setLastMessage(message);

                        if (!chatListItems.contains(chatListItem)) {
                            chatListItems.add(chatListItem);
                        }

                        if (chatListItems.size() == activePersonalChatIds.size()) {
                            Collections.sort(chatListItems);
                            chatsRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        Log.d("PERSONAL_CHATS_FRAGMENT", "Listener ADDED with chatId : " + chatListItem.getChatId());
    }

    /**
     * Setup the recycler view to display the list of my contacts from the firebase database.
     */
    private void setupRecyclerView() {
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());

        chatsRecyclerViewAdapter = new ChatsRecyclerViewAdapter(chatListItems);

        mChatsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChatsRecyclerView.setAdapter(chatsRecyclerViewAdapter);
    }

}
