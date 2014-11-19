package com.messenger.fade.rest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.Identity;
import com.messenger.fade.model.User;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by kkawai on 11/17/14.
 */
public final class FadeApi {

    private static final String API_BASE = "https://api.fadechat.com/v1";

    private static final int REQUEST_TIMEOUT_MS = 10000;

    /**
     * Remember to pass in the calling activity or fragment as the
     *
     * @param cancelTag
     * @param user
     * @param listener
     * @param errorListener
     */
    public static void saveUser(final Object cancelTag, final User user, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", user.toJSON().toString());
        Request request = new FadePostRequest(API_BASE + "/user", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);

    }

    public static void authenticateByUsername(final String username, final String password, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

    }

    public static void authenticateByEmail(final String username, final String password, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

    }

    public static void isUsernameExists(final String username, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

    }

    public static void isEmailExists(final String email, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

    }

    /**
     * Pass in user.getId();
     *
     * @param userid
     * @param listener
     * @param errorListener
     */
    public static void getIdentities(final long userid, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

    }

    public static void saveIdentity(final Identity identity, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

    }


}
