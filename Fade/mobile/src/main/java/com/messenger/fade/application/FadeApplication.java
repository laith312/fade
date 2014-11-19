package com.messenger.fade.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.messenger.fade.R;
import com.messenger.fade.model.User;
import com.messenger.fade.util.BitmapLruCache;
import com.messenger.fade.util.MLog;
import com.messenger.fade.util.ThreadWrapper;

import org.json.JSONObject;


public final class FadeApplication extends Application {

    private static String SHARED_PREFS_ME_KEY = "me";

    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;

    private static FadeApplication instance;

    private static SharedPreferences sharedPreferences;

    private static User me;


    @Override
    public void onCreate() {

        super.onCreate();

        ThreadWrapper.init();

        MLog.setEnabled(getPackageName(), getResources().getBoolean(R.bool.is_logging_enabled));

        instance = this;

        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new BitmapLruCache());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getMeFromPreferences();
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

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static FadeApplication getInstance() {
        return instance;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }
}
