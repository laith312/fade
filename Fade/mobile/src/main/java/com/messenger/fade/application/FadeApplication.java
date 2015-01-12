package com.messenger.fade.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.messenger.fade.R;
import com.messenger.fade.model.User;
import com.messenger.fade.util.BitmapLruCache;
import com.messenger.fade.util.HttpMessage;
import com.messenger.fade.util.MLog;
import com.messenger.fade.util.ThreadWrapper;

import org.json.JSONObject;


public final class FadeApplication extends Application {

    private static final String TAG = FadeApplication.class.getSimpleName();

    private static String SHARED_PREFS_ME_KEY = "me";

    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private static BitmapLruCache bitmapLruCache;

    private static FadeApplication instance;

    private static SharedPreferences sharedPreferences;

    /**
     * In most normal cases, the device either supports amazon cloud messaging
     * or google cloud messaging.
     *
     * This is determined at runtime.
     */
    public static boolean sIsGcmSupported;
    public static boolean sIsAdmSupported;

    private static User me;


    @Override
    public void onCreate() {

        super.onCreate();

        ThreadWrapper.init();
        initCloudMessaging();

        MLog.setEnabled(getPackageName(), getResources().getBoolean(R.bool.is_logging_enabled));

        instance = this;

        setRequestQueue(this);
        bitmapLruCache = new BitmapLruCache();
        imageLoader = new ImageLoader(requestQueue, bitmapLruCache);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getMeFromPreferences();
    }

    /**
     * Also used in small unit tests where we only have a context
     *
     * @param context
     */
    public static void setRequestQueue(final Context context) {
        try {
            HttpMessage.setAllowAllHostnameVerified();
        }catch(final Exception e) {
            MLog.e(TAG, "", e);
        }
        requestQueue = Volley.newRequestQueue(context);
    }

    private static User getMeFromPreferences() {
        if (sharedPreferences.contains(SHARED_PREFS_ME_KEY)) {
            try {
                me = User.from(new JSONObject(sharedPreferences.getString(SHARED_PREFS_ME_KEY, null)));
            } catch (final Exception e) {

            }
        }
        return me;
    }

    /**
     * Invoke after authenticating or creating a new account
     *
     * @param user
     */
    public static void setMe(final User user) {
        me = user;
        sharedPreferences.edit().putString(SHARED_PREFS_ME_KEY, user.toJSON().toString()).apply();
    }

    /**
     * Invoke when user logs out
     */
    public static void removeMe() {
        me = null;
        sharedPreferences.edit().remove(SHARED_PREFS_ME_KEY).apply();
    }

    /**
     * Returns myself if logged id, null otherwise.
     *
     * @return
     */
    public static User me() {
        if (me != null) {
            return me;
        }
        return getMeFromPreferences();
    }

    /**
     * basic initialization; there will be further
     * init in the activity
     */
    private void initCloudMessaging() {

        try {
            Class.forName("com.amazon.device.messaging.ADM");
            sIsAdmSupported = true;
        } catch (final ClassNotFoundException e) {
            sIsAdmSupported = false;
        }

        try {
            sIsGcmSupported = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
                    == ConnectionResult.SUCCESS;
        } catch (final Throwable t) {
            MLog.e(TAG, "Device does not support GCM", t);
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static FadeApplication getInstance() {
        return instance;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static BitmapLruCache getBitmapLruCache() {
        return bitmapLruCache;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }
}
