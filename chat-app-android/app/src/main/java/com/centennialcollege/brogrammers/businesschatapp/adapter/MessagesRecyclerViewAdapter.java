package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        MessageViewHolder(View v) {
            super(v);
            tvMessageText = v.findViewById(R.id.tv_message_text);
            tvMessageTime = v.findViewById(R.id.tv_message_time);
        }

        void bind(Message model) {
            // FixMe: Populate it when support for multimedia msgs is added.
            if (model.getIsMultimedia()) {
                return;
            }
            tvMessageText.setText(model.getContent());
            Date date = new Date(model.getTimeSent());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, MMM dd");
            tvMessageTime.setText(dateFormat.format(date));
        }
    }

}
