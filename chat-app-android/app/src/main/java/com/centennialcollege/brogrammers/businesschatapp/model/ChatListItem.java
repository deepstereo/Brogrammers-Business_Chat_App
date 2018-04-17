package com.centennialcollege.brogrammers.businesschatapp.model;

import android.support.annotation.NonNull;

/**
 * Model class to store information about one single chat item to be displayed on chat list screen.
 */

public class ChatListItem implements Comparable<ChatListItem> {

    private String chatId;
    private String chatName;
    private Message lastMessage;

    public ChatListItem() {
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatListItem) {
            return chatId.equals(((ChatListItem) obj).chatId);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull ChatListItem chatListItem) {
        if (chatListItem.getLastMessage() != null && lastMessage != null) {
            return Long.compare(chatListItem.getLastMessage().getTimeSent(), lastMessage.getTimeSent());
        } else {
            return 0;
        }
    }
}
