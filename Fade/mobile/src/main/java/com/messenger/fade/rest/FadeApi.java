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

    private static final String TAG = FadeApi.class.getSimpleName();

    public static final String API_RESULT_OK = "OK";
    public static final String API_RESULT_FAIL = "fail";
    public static final String API_RESULT_STATUS_KEY = "status";
    public static final String API_RESULT_DATA_KEY = "data";
    public static final String API_RESULT_DESCR_KEY = "descr";

    private static final String API_BASE = "https://api.fadechat.com/v1";

    private static final int REQUEST_TIMEOUT_MS = 10000;

    private static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(
            REQUEST_TIMEOUT_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    /**
     * Remember to pass in the calling activity or fragment as the tag
     * so you can cancel the request onDestroy
     *
     * @param cancelTag
     * @param user
     * @param listener
     * @param errorListener
     */
    public static void saveUser(final Object cancelTag, final User user, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", user.toJSON().toString());
        final Request request = new FadePostRequest(API_BASE + "/user", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);

    }

    public static void authenticateByUsername(final Object cancelTag, final String username, final String password, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("p", password);
        final Request request = new FadePostRequest(API_BASE + "/auth", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void authenticateByEmail(final Object cancelTag, final String email, final String password, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("p", password);
        final Request request = new FadePostRequest(API_BASE + "/auth", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void isUsernameExists(final Object cancelTag, final String username, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/user/"+username, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void isEmailExists(final Object cancelTag, final String email, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/email/"+email, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    /**
     * Pass in user.getId();
     *
     * @param userid
     * @param listener
     * @param errorListener
     */
    public static void getIdentities(final Object cancelTag, final long userid, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {
        final Request request = new FadeGetRequest(API_BASE + "/i/"+userid, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void saveIdentity(final Object cancelTag, final Identity identity, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("identity", identity.toJSON().toString());
        final Request request = new FadePostRequest(API_BASE + "/i", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void getUserById(final Object cancelTag, final long id, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/user/"+id, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

}
