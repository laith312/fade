package com.messenger.fade.rest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.messenger.fade.util.NetworkUtil;

import org.json.JSONObject;

public class FadeGetRequest extends JsonObjectRequest {

    private static final String TAG = FadeGetRequest.class.getSimpleName();

    public FadeGetRequest(final String url, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        super(Request.Method.GET, url, null, listener, errorListener);

        NetworkUtil.logToCurlRequest(this);
    }
}
