package com.messenger.fade;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.messenger.fade.util.MLog;

import org.json.JSONObject;

import java.util.Random;

/**
 * https://developer.android.com/google/gcm/client.html#sample-registerIfNecessary
 *
 * @author kkawai
 */
public final class GCMIntentService extends IntentService {

    private static final String TAG = GCMIntentService.class.getSimpleName();

    public GCMIntentService() {
        super(TAG);
    }

    private void onMessage(final Context context, final Intent data) throws Exception {

        final JSONObject message = new JSONObject(data.getStringExtra(MessageConstants.PROPERTY_PAYLOAD));
        MLog.i(TAG, "onMessage: ", message.toString());

        final int userid = message.getInt(MessageConstants.PROPERTY_USERID);
        final String username = message.getString(MessageConstants.PROPERTY_USERNAME);
        final String text = message.getString(MessageConstants.PROPERTY_TEXT);
        final Notification notification = new Notification(
                R.drawable.ic_launcher, "Fade via: " + username,
                System.currentTimeMillis());

        final int notificationId = new Random().nextInt(1000000);
        final Intent intent = new Intent(context, MockDisplayFadeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MessageConstants.PROPERTY_NOTIFICATION_ID, notificationId);
        intent.putExtra(MessageConstants.PROPERTY_USERID, userid);
        intent.putExtra(MessageConstants.PROPERTY_USERNAME, username);
        intent.putExtra(MessageConstants.PROPERTY_TEXT, text);

        final PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        showNotification(context, userid, username, text,
                notificationId,
                notification, pendingIntent);

    }

    private static void showNotification(final Context context,
                                         final int userid, final String username,
                                         final String text,
                                         final int notificationId,
                                         final Notification notification,
                                         final PendingIntent pendingIntent) {

        try {

            // Set the info for the views that show in the notification
            // panel.
            notification.setLatestEventInfo(context, "Fade via:", username,
                    pendingIntent);

            notification.ledARGB = 0xff00ff00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            //notification.defaults |= Notification.DEFAULT_VIBRATE; //TODO: turn on later
            //notification.defaults |= Notification.DEFAULT_SOUND; //TODO: turn on later

            ((NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE)).notify(
                    notificationId, notification);

            MLog.i(TAG, "showing notification");
        } catch (final Exception e) {
            MLog.e(TAG, "could not show notification", e);
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        try {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            final String messageType = gcm.getMessageType(intent);
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                onMessage(this, intent);
            }
        } catch (final Exception e) {
            //AnalyticsHelper.logException(Events.GCM_INCOMING_MSG_FAIL, e);
            MLog.e(TAG, "", e);
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

}
