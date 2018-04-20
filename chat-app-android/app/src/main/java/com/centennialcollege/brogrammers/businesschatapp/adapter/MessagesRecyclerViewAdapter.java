package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RecyclerView adapter to display the messages on chat screen.
 */

public class MessagesRecyclerViewAdapter extends FirebaseRecyclerAdapter<Message, MessagesRecyclerViewAdapter.MessageViewHolder> {

    private static final int MESSAGE_TYPE_MINE = 0;
    private static final int MESSAGE_TYPE_THEIR = 1;

    private FirebaseUser currentUser;

    public MessagesRecyclerViewAdapter(FirebaseRecyclerOptions<Message> options) {
        super(options);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public MessagesRecyclerViewAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_MINE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_mine, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_their, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {
        holder.bind(model);
    }

    @Override
    public int getItemViewType(int position) {
        return (TextUtils.equals(currentUser.getUid(), getItem(position).getSenderId()))
                ? MESSAGE_TYPE_MINE : MESSAGE_TYPE_THEIR;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageText;
        private TextView tvMessageTime;
        private ImageView ivMediaMessageContent;
        private CardView cvMessage;
        private View view;

        MessageViewHolder(View v) {
            super(v);
            view = v;
            tvMessageText = v.findViewById(R.id.tv_message_text);
            tvMessageTime = v.findViewById(R.id.tv_message_time);
            ivMediaMessageContent = v.findViewById(R.id.iv_media_message_content);
            cvMessage = v.findViewById(R.id.cv_message);
        }

        void bind(Message model) {
            if (model.getIsMultimedia()) {
                tvMessageText.setVisibility(View.GONE);
                cvMessage.setVisibility(View.VISIBLE);
                Glide.with(view.getContext())
                        .load(model.getContent())
                        .centerCrop()
                        .into(ivMediaMessageContent);
            } else {
                tvMessageText.setVisibility(View.VISIBLE);
                cvMessage.setVisibility(View.GONE);
                tvMessageText.setText(model.getContent());
            }
            Date date = new Date(model.getTimeSent());
            SimpleDateFormat dateFormat = new SimpleDateFormat(view.getContext().getString(R.string.messages_date_format_type));
            tvMessageTime.setText(dateFormat.format(date));
        }
    }

}
