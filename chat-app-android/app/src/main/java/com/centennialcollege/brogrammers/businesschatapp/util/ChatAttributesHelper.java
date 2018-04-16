package com.centennialcollege.brogrammers.businesschatapp.util;

import java.util.Arrays;

public class ChatAttributesHelper {

    public static String getPersonalChatID(String senderId, String recipientId) {
        String[] chatIds = {senderId, recipientId};
        Arrays.sort(chatIds);
        String chatIdString = chatIds[0] + chatIds[1];
        return HashHelper.getMd5(chatIdString);
    }

}
