package com.centennialcollege.brogrammers.businesschatapp.model;

import java.util.Map;

/**
 * Model class to store information about a chat.
 */

public class Chat {

    private String chatName;
    private boolean isGroupChat;
    private Map<String, Boolean> members;

    public Chat() {
    }

    public Chat(String chatName, boolean isGroupChat, Map<String, Boolean> members) {
        this.chatName = chatName;
        this.isGroupChat = isGroupChat;
        this.members = members;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public boolean getIsGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }
}
