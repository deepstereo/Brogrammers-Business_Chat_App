package com.centennialcollege.brogrammers.businesschatapp.data.db;

import com.centennialcollege.brogrammers.businesschatapp.model.User;

import java.util.Map;

public interface DbContract {

    interface Presenter {

        void onDbSuccess();

        void onDbFailure();

    }

    interface Model {

        void addAdditionalUserInfoToDb(String userId, User user);

        void addContactToMyContacts(String newContactId);

        void addContactsToMyContacts(Map<String, Object> newContactsId);

        String createChat(String chatName, String receiverContactId);

        void createMessage(String chatId, String content);

    }

}
