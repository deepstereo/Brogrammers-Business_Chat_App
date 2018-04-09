package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

/**
 * RecyclerView adapter to display the messages on chat screen.
 */

public class MessagesRecyclerViewAdapter extends FirebaseRecyclerAdapter<Message, MessagesRecyclerViewAdapter.MessageViewHolder> {

    private Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessagesRecyclerViewAdapter(FirebaseRecyclerOptions<Message> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public MessagesRecyclerViewAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {
        holder.bind(model);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageText;
        private TextView tvMessageSender;
        private TextView tvMessageTime;

        MessageViewHolder(View v) {
            super(v);
            tvMessageText = v.findViewById(R.id.tv_message_text);
            tvMessageSender = v.findViewById(R.id.tv_message_sender);
            tvMessageTime = v.findViewById(R.id.tv_message_time);
        }

        void bind(Message model) {
            tvMessageText.setText(model.getMessageText());
            tvMessageSender.setText(model.getMessageUser());
            tvMessageTime.setText(DateFormat.format(context.getString(R.string.message_date_format), model.getMessageTime()));
        }
    }

}
