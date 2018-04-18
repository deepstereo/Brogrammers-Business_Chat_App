package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.activity.ChatActivity;
import com.centennialcollege.brogrammers.businesschatapp.model.Chat;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.centennialcollege.brogrammers.businesschatapp.util.ChatAttributesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RecyclerView adapter to display the list of my contacts.
 */

public class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ContactViewHolder> {

    private Activity context;
    private User currentUser;
    private ArrayList<User> myContacts;

    public MyContactsRecyclerViewAdapter(User currentUser, ArrayList<User> myContacts, Activity context) {
        this.currentUser = currentUser;
        this.myContacts = myContacts;
        this.context = context;
    }

    @Override
    public MyContactsRecyclerViewAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new MyContactsRecyclerViewAdapter.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.bind(myContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvEmail;
        private TextView tvPlaceHolderAvatar;
        private CardView cvAvatar;
        private ImageView ivAvatar;
        private View view;

        ContactViewHolder(View v) {
            super(v);
            view = v;
            tvUsername = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
            tvPlaceHolderAvatar = v.findViewById(R.id.tv_placeholder_avatar);
            cvAvatar = v.findViewById(R.id.cv_avatar);
            ivAvatar = v.findViewById(R.id.iv_avatar);
        }

        void bind(final User user) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            boolean isAvatarImageAvailable = !TextUtils.isEmpty(user.getAvatarURL());

            if (isAvatarImageAvailable) {
                cvAvatar.setVisibility(View.VISIBLE);
                Glide.with(view.getContext())
                        .load(user.getAvatarURL())
                        .centerCrop()
                        .into(ivAvatar);
                tvPlaceHolderAvatar.setVisibility(View.GONE);
            } else {
                cvAvatar.setVisibility(View.GONE);
                tvPlaceHolderAvatar.setVisibility(View.VISIBLE);
                tvPlaceHolderAvatar.setText(String.valueOf(user.getUsername().toUpperCase().charAt(0)));
            }

            view.setOnClickListener(v -> {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String currentUserId = firebaseAuth.getCurrentUser().getUid();

                // Create a new chat
                Map<String, Boolean> members = new LinkedHashMap<>();
                members.put(currentUserId, true);
                members.put(user.getId(), true);

                // Chat name is the concatenation of the userId of recipient and sender.
                Chat newChat = new Chat(user.getId() + currentUser.getId(), false, members);

                DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.CHATS_CHILD);
                String newChatId = ChatAttributesHelper.getPersonalChatID(currentUserId, user.getId());
                chatReference.child(newChatId).setValue(newChat);

                addChatIdInActivePersonalChats(newChatId, currentUserId, user.getId());

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(Constants.KEY_CHAT_ID, newChatId);
                intent.putExtra(Constants.KEY_CHAT_NAME, user.getUsername());
                context.startActivity(intent);
                context.finish();
            });
        }
    }

    private void addChatIdInActivePersonalChats(String newChatId, String senderId, String receiverId) {
        // Add the chat id in the list of active personal chats of the currently logged in user as well as receiver user.
        updateActivePersonalChatsForUser(newChatId, senderId);
        updateActivePersonalChatsForUser(newChatId, receiverId);
    }

    private void updateActivePersonalChatsForUser(final String newChatId, String userId) {
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_CHILD)
                .child(userId).child(Constants.USER_ACTIVE_PERSONAL_CHATS);


        // Attach a listener to read the data at our posts reference
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Boolean> activePersonalChats = (HashMap<String, Boolean>) dataSnapshot.getValue();

                if (activePersonalChats == null) {
                    activePersonalChats = new LinkedHashMap<>();
                }

                activePersonalChats.put(newChatId, true);
                userReference.setValue(activePersonalChats);
                userReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
