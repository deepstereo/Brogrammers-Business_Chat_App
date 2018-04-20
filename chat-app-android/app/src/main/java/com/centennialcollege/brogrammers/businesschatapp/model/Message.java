package com.centennialcollege.brogrammers.businesschatapp.model;

/**
 * Model class for a chat message.
 */

public class Message {

    private String content;
    private boolean isMultimedia;
    private String senderId;
    private long timeSent;

    public Message(String content, boolean isMultimedia, String senderId, long timeStamp) {
        this.content = content;
        this.isMultimedia = isMultimedia;
        this.senderId = senderId;
        this.timeSent = timeStamp;
    }

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsMultimedia() {
        return isMultimedia;
    }

    public void setIsMultimedia(boolean multimedia) {
        isMultimedia = multimedia;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

}
