package com.messenger.fade.rest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class FadePutRequest extends JsonObjectRequest {

    private static final String TAG = FadePutRequest.class.getSimpleName();

    public FadePutRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Request.Method.PUT, url, jsonRequest, listener, errorListener);

    }
}
