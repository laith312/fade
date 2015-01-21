package com.messenger.fade;

/**
 * Created by kkawai on 1/4/15.
 */
public final class MessageConstants {

    private MessageConstants(){}

    public static final String GCM_SENDER_ID = "483704506104";

    /**
     * All message properties defined here
     */
    public static final String PROPERTY_TEXT = "m";
    public static final String PROPERTY_USERID = "i";
    public static final String PROPERTY_USERNAME = "u";
    public static final String PROPERTY_MESSAGE_ID = "x";
    public static final String PROPERTY_PAYLOAD = "msg";

    public static final String PROPERTY_NOTIFICATION_ID = "nid";

    public static final int MAX_LOCAL_MESSAGE_FETCH_SIZE = 30;
}
