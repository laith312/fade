package com.messenger.fade.rest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.messenger.fade.util.NetworkUtil;

public class FadeDeleteRequest extends StringRequest {

    private static final String TAG = FadeDeleteRequest.class.getSimpleName();

    public FadeDeleteRequest(final String url, final Response.Listener<String> responder, final Response.ErrorListener errorListener) {

        super(Request.Method.DELETE, url, responder, errorListener);

        NetworkUtil.logToCurlRequest(this);

    }

}
