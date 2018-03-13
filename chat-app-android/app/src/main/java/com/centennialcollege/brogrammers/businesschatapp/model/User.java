package com.centennialcollege.brogrammers.businesschatapp.model;

import java.util.ArrayList;

public class User {
    private String id;
    private String username;
    private String email;
    private ArrayList<String> contactList = new ArrayList<>();
    public User() {
    }
    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    public User(String id, String username, String email, ArrayList<String> contactList) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contactList = contactList;
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
    public ArrayList<String> getContactList() {
        return contactList;
    }
    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }
}
