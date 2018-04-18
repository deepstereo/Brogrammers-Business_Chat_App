package com.centennialcollege.brogrammers.businesschatapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model class to store information about a user.
 */

@IgnoreExtraProperties
public class User {
    @Deprecated
    private String id;
    private String username;
    private String email;
    private String avatarURL;
    private Map<String, Boolean> contactList = new LinkedHashMap<>();
    private Map<String, Boolean> activePersonalChats = new LinkedHashMap<>();
    private Map<String, Boolean> activeGroupChats = new LinkedHashMap<>();

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Deprecated
    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Boolean> getContactList() {
        return contactList;
    }

    public void setContactList(Map<String, Boolean> contactList) {
        this.contactList = contactList;
    }

    public Map<String, Boolean> getActivePersonalChats() {
        return activePersonalChats;
    }

    public void setActivePersonalChats(Map<String, Boolean> activePersonalChats) {
        this.activePersonalChats = activePersonalChats;
    }

    public Map<String, Boolean> getActiveGroupChats() {
        return activeGroupChats;
    }

    public void setActiveGroupChats(Map<String, Boolean> activeGroupChats) {
        this.activeGroupChats = activeGroupChats;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
