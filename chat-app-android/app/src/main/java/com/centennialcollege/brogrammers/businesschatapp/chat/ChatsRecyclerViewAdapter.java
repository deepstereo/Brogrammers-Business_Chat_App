package com.centennialcollege.brogrammers.businesschatapp.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.ChatListItem;

import java.util.ArrayList;

/**
 * RecyclerView adapter to display the list of chats on personal and group chats list screens.
 */

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ChatViewHolder> {

    private Context context;
    private ArrayList<ChatListItem> chatListItems;

    public ChatsRecyclerViewAdapter(ArrayList<ChatListItem> personalChats, Context context) {
        this.chatListItems = personalChats;
        this.context = context;
    }

    @Override
    public ChatsRecyclerViewAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, parent, false);
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
        private View view;

        ChatViewHolder(View v) {
            super(v);
            view = v;
            tvChatname = v.findViewById(R.id.tv_chatname);
        }

        void bind(final ChatListItem chat) {

            tvChatname.setText(chat.getChatName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Constants.KEY_CHAT_ID, chat.getChatId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
