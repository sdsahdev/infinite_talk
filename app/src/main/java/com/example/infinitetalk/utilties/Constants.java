package com.example.infinitetalk.utilties;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLAECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCENAME = "chatApppreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedin";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmtoken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLATION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLATION_CONVERSATION = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "reciverName";
    public static final String KEY_SENDER_IMAGE = "sendermage";
    public static final String KEY_RECEIVER_IMAGE = "reciverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRETION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA0D3E7r4:APA91bEklJKg0WcnNt99LyXM58TtM57qijF23lghFRa980RZd_qaQkUhWWgg1lAQ_Xzr223qrzvdeYLsA6-Bs04xbnKvRXY43AFS4vmC0jabVPLPx3Z3XBDxuyrNEt1GkDj-QMQdlfXU"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE, "application/json"
            );
        }
        return remoteMsgHeaders;
    }


}

