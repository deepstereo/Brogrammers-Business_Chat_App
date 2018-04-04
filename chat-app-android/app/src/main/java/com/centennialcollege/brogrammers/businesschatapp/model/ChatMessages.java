package com.centennialcollege.brogrammers.businesschatapp.model;

import java.util.List;

/**
 * Model class to store list of chat messages.
 */

public class ChatMessages {

    private List<Message> messageList;

    public ChatMessages() {
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
