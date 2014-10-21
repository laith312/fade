package com.messenger.fade.rest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.messenger.fade.util.FadeLog;

import org.json.JSONObject;

public class FadePostRequest extends JsonObjectRequest {

    private static final String TAG = FadePostRequest.class.getSimpleName();

    public FadePostRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Request.Method.POST, url, jsonRequest, listener, errorListener);

        FadeLog.d(TAG, "POST " + url + "  " + jsonRequest.toString());
    }
}
