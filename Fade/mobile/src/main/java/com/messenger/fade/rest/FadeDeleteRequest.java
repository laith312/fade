package com.messenger.fade.rest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.messenger.fade.util.FadeLog;

import org.json.JSONObject;

public class FadeDeleteRequest extends JsonObjectRequest {

    private static final String TAG = FadeDeleteRequest.class.getSimpleName();

    public FadeDeleteRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Request.Method.DELETE, url, jsonRequest, listener, errorListener);

        FadeLog.d(TAG, "DELETE " + url + "  " + jsonRequest.toString());
    }
}
