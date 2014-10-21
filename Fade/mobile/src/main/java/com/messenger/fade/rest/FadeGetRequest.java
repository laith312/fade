package com.messenger.fade.rest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.messenger.fade.util.FadeLog;

import org.json.JSONObject;

public class FadeGetRequest extends JsonObjectRequest {

    private static final String TAG = FadeGetRequest.class.getSimpleName();

    public FadeGetRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Request.Method.GET, url, jsonRequest, listener, errorListener);

        FadeLog.d(TAG, "GET " + url + "  " + jsonRequest.toString());
    }
}
