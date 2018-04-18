package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.User;

import java.util.ArrayList;

/**
 * RecyclerView adapter to display the list of my contacts.
 */

public class ChatMembersRecyclerViewAdapter extends RecyclerView.Adapter<ChatMembersRecyclerViewAdapter.ContactViewHolder> {

    private ArrayList<User> chatMembers;

    public ChatMembersRecyclerViewAdapter(ArrayList<User> chatMembers) {
        this.chatMembers = chatMembers;
    }

    @Override
    public ChatMembersRecyclerViewAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ChatMembersRecyclerViewAdapter.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.bind(chatMembers.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMembers.size();
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

        void bind(final User model) {
            tvUsername.setText(model.getUsername());
            tvEmail.setText(model.getEmail());

            // Todo: Once Avatar images are available, set avatar if available, otherwise, set a placeholder avatar with First character of user name.
            boolean isAvatarImageAvailable = false;
            if (isAvatarImageAvailable) {
                cvAvatar.setVisibility(View.VISIBLE);
                // Todo : set avatar
                tvPlaceHolderAvatar.setVisibility(View.GONE);
            } else {
                cvAvatar.setVisibility(View.GONE);
                tvPlaceHolderAvatar.setVisibility(View.VISIBLE);
                tvPlaceHolderAvatar.setText(String.valueOf(model.getUsername().toUpperCase().charAt(0)));
            }
        }
    }
}
