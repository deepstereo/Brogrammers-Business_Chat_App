package com.centennialcollege.brogrammers.businesschatapp.data.remote;

import com.centennialcollege.brogrammers.businesschatapp.model.Chat;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public interface FirebaseDbHelper {

    Task<Void> addUserInfo(String userId, User user);

    Task<Void> addContactToUserContacts(String userId, String newContactId);

    Task<Void> addContactsToUserContacts(String userId, Map<String, Object> newContactsId);

    Task<Void> addActivePersonalChats(String userId, Map<String, Object> newChatsId);

    Task<Void> createChat(String chatId, Chat chat);

    Task<Void> createMessage(String chatId, Message message);

}
