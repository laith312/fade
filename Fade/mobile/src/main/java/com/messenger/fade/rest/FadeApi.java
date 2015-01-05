package com.messenger.fade.rest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.Identity;
import com.messenger.fade.model.User;
import com.messenger.fade.model.UserMedia;
import com.messenger.fade.util.FileUploader;
import com.messenger.fade.util.MLog;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by kkawai on 11/17/14.
 */
public final class FadeApi {

    private static final String TAG = FadeApi.class.getSimpleName();

    public static final String API_RESULT_SUCCESS_KEY = "success";
    public static final String API_RESULT_DATA_KEY = "data";
    public static final String API_RESULT_DESCR_KEY = "descr";
    public static final String API_RESULT_EXISTS_KEY = "exists";

    private static final String API_BASE = "https://api.fadechat.com/v1";

    private static final int REQUEST_TIMEOUT_MS = 10000;

    private static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(
            REQUEST_TIMEOUT_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    /**
     * Remember to call FadeApplication.setMe(user) after receiving the
     * saved user in the response.
     * <p/>
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
        params.put("u", user.toJSON().toString());
        final Request request = new FadePostRequest(API_BASE + "/u", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);

    }

    /**
     * Saves user display pic to S3.
     * <p/>
     * It is assumed that user.getProfilePicUrl() will always return
     * http://dp.fade.s3.amazonaws.com/[userid].jpg
     *
     * @param imageFile
     * @param userid
     * @param progressListener - optional. so you can track progress, for small
     *                         files probably isn't necessary.
     * @param responseListener - optional. standard response listener when upload is finished
     * @param errorListener    - optional. standard error listener.
     * @return - FileUploader object, so you can cancel the request, if you want.
     */
    public static FileUploader saveUserDisplayPic(final File imageFile, final int userid, final FileUploader.ProgressListener progressListener, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {

        final FileUploader f = new FileUploader();
        f.postFile(userid + ".jpg", imageFile, "dp.fade", progressListener, responseListener, errorListener);
        return f;
    }

    /**
     * Adds photo to user image gallery.
     *
     * Several steps:
     * 1) gets the next available image slot # for user
     * 2) saves file to S3
     * 3) saves UserMedia object to server
     *
     * @param cancelTag
     * @param imageFile
     * @param media
     * @param progressListener
     * @param responseListener
     * @param errorListener
     * @return
     */
    public static FileUploader saveUserMediaToGallery(final Object cancelTag, final File imageFile, final UserMedia media, final FileUploader.ProgressListener progressListener, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {

        final FileUploader f = new FileUploader();
        //first, get the next available photo slot (nextMedia)
        final Request request = new FadeGetRequest(API_BASE + "/m?n=x&b=" + media.getUserid(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject jsonObject) {

                if (!jsonObject.optBoolean(API_RESULT_SUCCESS_KEY, false)) {
                    errorListener.onErrorResponse(new VolleyError("save failed. could not get nextMedia"));
                    return;
                }

                media.setNum((short) jsonObject.optJSONObject("data").optInt("nextMedia"));

                //now store file in S3
                f.postFile("" + media.getUserid() + '_' + media.getNum() + ".jpg", imageFile, "pics.fade", progressListener,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String s) {
                                try {
                                    final JSONObject postFileResponse = new JSONObject(s);

                                    if (!postFileResponse.optBoolean(API_RESULT_SUCCESS_KEY, false)) {
                                        errorListener.onErrorResponse(new VolleyError("failed to post file"));
                                    }

                                    final HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("f", media.toJSON().toString());
                                    final Request postRequest = new FadePostRequest(API_BASE + "/m", params, responseListener, errorListener);
                                    postRequest.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
                                    FadeApplication.getRequestQueue().add(postRequest);

                                } catch (final Exception e) {
                                    errorListener.onErrorResponse(new VolleyError(e));
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(final VolleyError volleyError) {
                                errorListener.onErrorResponse(volleyError);
                            }
                        });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                errorListener.onErrorResponse(volleyError);
            }
        });
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        MLog.i(TAG, "FadeApplication.getRequestQueue() "+FadeApplication.getRequestQueue());
        FadeApplication.getRequestQueue().add(request);
        return f;
    }

    /**
     * Fetches a jsonarray of most recently posted UserMedia objects.
     *
     * @param cancelTag
     * @param userid
     * @param floorid       - id of the last UserMedia object in the previously returned list
     * @param count         - for paging; recommendation is around 30
     * @param listener
     * @param errorListener
     */
    public void getUserMedia(final Object cancelTag, final int userid, final int floorid, final int count, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {
        final Request request = new FadeGetRequest(API_BASE + "/m?n=a&b=" + userid + "&g=" + count + "&k=" + floorid, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    /**
     * Fetches the "mediaCount" of the user, which is the total # of media objects saved by the user.
     * Useful for displaying on a user's profile page.
     *
     * @param cancelTag
     * @param userid
     * @param listener
     * @param errorListener
     */
    public void getUserMediaCount(final Object cancelTag, final int userid, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {
        final Request request = new FadeGetRequest(API_BASE + "/m?n=w&b=" + userid, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    /**
     * Remember to call FadeApplication.setMe(user) after receiving the
     * saved user in the response.
     */
    public static void authenticateByUsername(final Object cancelTag, final String username, final String password, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("r", username);
        params.put("q", password);
        final Request request = new FadePostRequest(API_BASE + "/ti", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void authenticateByEmail(final Object cancelTag, final String email, final String password, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("y", email);
        params.put("q", password);
        final Request request = new FadePostRequest(API_BASE + "/ti", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void isUsernameExists(final Object cancelTag, final String username, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/u/" + username, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void isEmailExists(final Object cancelTag, final String email, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/e/" + email, listener, errorListener);
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
        final Request request = new FadeGetRequest(API_BASE + "/i/" + userid, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void saveIdentity(final Object cancelTag, final Identity identity, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("i", identity.toJSON().toString());
        final Request request = new FadePostRequest(API_BASE + "/i", params, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void getUserById(final Object cancelTag, final long id, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/u/" + id, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    public static void getSettings(final Object cancelTag, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeGetRequest(API_BASE + "/se", listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);
    }

    /**
     * @param cancelTag
     * @param id            - Identity id
     * @param listener
     * @param errorListener
     */
    public static void deleteIdentity(final Object cancelTag, final long id, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeDeleteRequest(API_BASE + "/i?z=" + id, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);

    }

    /**
     * Deletes file from S3
     *
     * @param cancelTag
     * @param bucket        - S3 bucket
     * @param key           - the key
     * @param listener
     * @param errorListener
     */
    public static void deleteFile(final Object cancelTag, final String bucket, final String key, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final Request request = new FadeDeleteRequest(API_BASE + "/f?b=" + bucket + "&k=" + key, listener, errorListener);
        request.setShouldCache(false).setRetryPolicy(DEFAULT_RETRY_POLICY).setTag(cancelTag);
        FadeApplication.getRequestQueue().add(request);

    }
}
