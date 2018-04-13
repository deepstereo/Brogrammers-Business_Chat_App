package com.centennialcollege.brogrammers.businesschatapp.data.db;

import android.support.annotation.NonNull;

import com.centennialcollege.brogrammers.businesschatapp.model.Chat;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.centennialcollege.brogrammers.businesschatapp.Constants.CHATS_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.MESSAGES_CHILD;
import static com.centennialcollege.brogrammers.businesschatapp.Constants.USERS_CHILD;


public class DbModel implements DbContract.Model, OnSuccessListener<Void>, OnFailureListener {

    private DbContract.Presenter presenter;

    public DbModel(DbContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addAdditionalUserInfoToDb(String userId, User user) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_CHILD)
                .child(userId)
                .setValue(user)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void addContactToMyContacts(String newContactId) {
        Map<String, Object> map = new HashMap<>();
        map.put(newContactId, true);
        addContactsToMyContacts(map);
    }

    @Override
    public void addContactsToMyContacts(Map<String, Object> newContactsId) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("contactList")
                .updateChildren(newContactsId)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public String createChat(String chatName, String secondMemberContactId) {
        Map<String, Boolean> map = new HashMap<>();
        map.put(secondMemberContactId, true);
        map.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
        Chat chat = new Chat(chatName, false, map);
        String chatId = UUID.randomUUID().toString();

        FirebaseDatabase.getInstance()
                .getReference()
                .child(CHATS_CHILD)
                .child(chatId)
                .setValue(chat)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
        return chatId;
    }

    @Override
    public void createMessage(String chatId, String content) {
        Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), content);

        FirebaseDatabase.getInstance()
                .getReference()
                .child(MESSAGES_CHILD)
                .child(chatId)
                .child(UUID.randomUUID().toString())
                .setValue(message)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    @Override
    public void onSuccess(Void aVoid) {
        presenter.onDbSuccess();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        presenter.onDbFailure();
    }
}