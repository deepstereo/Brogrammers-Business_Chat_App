package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.centennialcollege.brogrammers.businesschatapp.ui.profile.ProfileActivity;
import com.centennialcollege.brogrammers.businesschatapp.util.UserAttributesUtils;

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
        private TextView tvPlaceholderAvatar;
        private CardView cvAvatar;
        private ImageView ivAvatar;
        private View view;
        private Context context;

        ContactViewHolder(View v) {
            super(v);
            view = v;
            tvUsername = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
            tvPlaceholderAvatar = v.findViewById(R.id.tv_placeholder_avatar);
            cvAvatar = v.findViewById(R.id.cv_avatar);
            ivAvatar = v.findViewById(R.id.iv_avatar);
            context = view.getContext();
        }

        void bind(final User user) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            if (user.getAvatar()) {
                cvAvatar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(user.getAvatarURL())
                        .centerCrop()
                        .into(ivAvatar);
                tvPlaceholderAvatar.setVisibility(View.GONE);
            } else {
                cvAvatar.setVisibility(View.GONE);
                tvPlaceholderAvatar.setVisibility(View.VISIBLE);
                UserAttributesUtils.setAccountColor(tvPlaceholderAvatar, user.getUsername(), context);
            }

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(Constants.USER_ID, user.getId());
                context.startActivity(intent);
            });
        }
    }
}
