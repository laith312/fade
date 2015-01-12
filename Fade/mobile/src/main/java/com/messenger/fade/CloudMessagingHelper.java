package com.messenger.fade;

import android.app.Activity;
import android.content.Context;

import com.amazon.device.messaging.ADM;
import com.amazon.device.messaging.development.ADMManifest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.util.MLog;
import com.messenger.fade.util.StringUtil;

public final class CloudMessagingHelper {

    private static final String TAG = CloudMessagingHelper.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private CloudMessagingHelper() {
    }

    public static void registerIfNecessary(final Context context) {

        MLog.i(TAG, "registerIfNecessary()");

        if (FadeApplication.sIsAdmSupported && context.getResources().getBoolean(R.bool.is_amazon_build)) {

            try {
                MLog.i(TAG, "about to verify if adm is really supported");
                ADMManifest.checkManifestAuthoredProperly(context);
                MLog.i(TAG, "after check adm");
                final ADM adm = new ADM(context);
                MLog.i(TAG, "after instantiate adm");
                if (StringUtil.isEmpty(adm.getRegistrationId())) {
                    // startRegister() is asynchronous; your app is notified via
                    // the
                    // onRegistered() callback when the registration ID is
                    // available.
                    MLog.i(TAG, "registerIfNecessary adm");
                    adm.startRegister();
                }

            } catch (final Throwable t) {
                MLog.e(TAG, "Device does not support ADM", t);
                FadeApplication.sIsAdmSupported = false;
            }
            return;
        }

        if (FadeApplication.sIsGcmSupported && !context.getResources().getBoolean(R.bool.is_amazon_build)) {
            MLog.i(TAG, "debugx about to register gcm.  isGcmSupported:" + FadeApplication.sIsGcmSupported);
            new GCMRegistrationManager(context).registerGCM();
        }

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices(final Activity activity) {

        if (activity.getResources().getBoolean(R.bool.is_amazon_build)) {
            return false;
        }

        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                MLog.i(TAG, "This device does not have google play services support, so cannot do gcm.  Too bad.");
                //AnalyticsHelper.logEvent(Events.NO_GOOGLE_PLAY_SERVICES);
            }
            return false;
        }
        FadeApplication.sIsGcmSupported = true;
        return true;
    }

}
