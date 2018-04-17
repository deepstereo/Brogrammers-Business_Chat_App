package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        private View view;

        ContactViewHolder(View v) {
            super(v);
            view = v;
            tvUsername = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
        }

        void bind(final User model) {
            tvUsername.setText("Username: " + model.getUsername());
            tvEmail.setText("Email: " + model.getEmail());
        }
    }
}
