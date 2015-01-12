package com.messenger.fade;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.rest.FadeApi;
import com.messenger.fade.util.DeviceUtil;
import com.messenger.fade.util.MLog;

/**
 * The important assumption is that GCM is already supported on this device
 * AND that Google Play Services is installed on the device.
 *
 * @author kkawai
 */
public final class GCMRegistrationManager {

    private static final String TAG = GCMRegistrationManager.class.getSimpleName();

    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "registration_id";
    private GoogleCloudMessaging mGcm;
    private String mRegId;
    private Context mContext;

    public GCMRegistrationManager(final Context context) {
        mContext = context;
    }

    public void registerGCM() {

        mGcm = GoogleCloudMessaging.getInstance(mContext);
        mRegId = getRegistrationId(mContext);

        if (mRegId.isEmpty()) {
            new Thread() {
                @Override
                public void run() {
                    register();
                }
            }.start();

        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void register() {

        try {
            if (mGcm == null) {
                mGcm = GoogleCloudMessaging.getInstance(mContext);
            }
            mRegId = mGcm.register(Constants.GCM_SENDER_ID);

            // You should send the registration ID to your server over
            // HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your
            // app.
            // The request to your server should be authenticated if
            // your app
            // is using accounts.
            FadeApi.gcmReg(null, FadeApplication.me().getId(), mRegId, DeviceUtil.getAndroidId(mContext), new Response.Listener<String>() {
                @Override
                public void onResponse(final String s) {
                    MLog.i(TAG, "FadeApi.gcmReg() response: " + s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    MLog.e(TAG, "FadeApi.gcmReg() error response: " + volleyError.getMessage());
                }
            });

            // Persist the regID - no need to registerIfNecessary again.
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            final int appVersion = getAppVersion(mContext);
            MLog.i(TAG, "debugx Saving regId on app version " + appVersion);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, mRegId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();

        } catch (final Exception e) {
            MLog.e(TAG, "", e);
        }

    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to registerIfNecessary.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            MLog.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        final int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        final int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            MLog.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static void removeRegistrationId(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final int appVersion = getAppVersion(context);
        MLog.i(TAG, "Removing regId on app version " + appVersion);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        editor.commit();
    }

    private static int getAppVersion(final Context context) {

        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (final Exception e) {
            return 1;
        }
    }
}
