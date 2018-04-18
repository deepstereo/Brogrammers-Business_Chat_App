package com.centennialcollege.brogrammers.businesschatapp.adapter;

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
import com.centennialcollege.brogrammers.businesschatapp.model.ChatListItem;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * RecyclerView adapter to display the list of chats on personal and group chats list screens.
 */

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ChatViewHolder> {

    private ArrayList<ChatListItem> chatListItems;

    public ChatsRecyclerViewAdapter(ArrayList<ChatListItem> personalChats) {
        this.chatListItems = personalChats;
    }

    @Override
    public ChatsRecyclerViewAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatsRecyclerViewAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatsRecyclerViewAdapter.ChatViewHolder holder, int position) {
        holder.bind(chatListItems.get(position));
    }

    @Override
    public int getItemCount() {
        return chatListItems.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChatname;
        private TextView tvDate;
        private TextView tvLastMessage;
        private TextView tvPlaceHolderAvatar;
        private CardView cvAvatar;
        private ImageView ivAvatar;
        private View view;

        ChatViewHolder(View v) {
            super(v);
            view = v;
            tvChatname = v.findViewById(R.id.tv_chat_name);
            tvDate = v.findViewById(R.id.tv_date);
            tvLastMessage = v.findViewById(R.id.tv_last_message);
            tvPlaceHolderAvatar = v.findViewById(R.id.tv_placeholder_avatar);
            cvAvatar = v.findViewById(R.id.cv_avatar);
            ivAvatar = v.findViewById(R.id.iv_avatar);
        }

        void bind(final ChatListItem chat) {

            tvChatname.setText(chat.getChatName());

            Message message = chat.getLastMessage();
            if (message != null) {
                Date date = new Date(message.getTimeSent());
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, MMM dd");
                tvDate.setText(dateFormat.format(date));

                tvLastMessage.setText(message.getContent());

                boolean isAvatarImageAvailable = !TextUtils.isEmpty(chat.getAvatarUrl());

                if (isAvatarImageAvailable) {
                    cvAvatar.setVisibility(View.VISIBLE);
                    Glide.with(view.getContext())
                            .load(chat.getAvatarUrl())
                            .centerCrop()
                            .into(ivAvatar);
                    tvPlaceHolderAvatar.setVisibility(View.GONE);
                } else {
                    cvAvatar.setVisibility(View.GONE);
                    tvPlaceHolderAvatar.setVisibility(View.VISIBLE);
                    tvPlaceHolderAvatar.setText(String.valueOf(chat.getChatName().toUpperCase().charAt(0)));
                }
            }

            view.setOnClickListener(v -> {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_CHAT_ID, chat.getChatId());
                intent.putExtra(Constants.KEY_CHAT_NAME, chat.getChatName());
                view.getContext().startActivity(intent);
            });
        }
    }
}
