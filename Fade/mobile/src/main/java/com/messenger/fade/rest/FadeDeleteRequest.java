package com.messenger.fade.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.messenger.fade.util.NetworkUtil;

import java.util.Map;

public class FadeDeleteRequest extends StringRequest {

    private static final String TAG = FadeDeleteRequest.class.getSimpleName();

    private Map<String,String> mParams;

    public FadeDeleteRequest(final String url, final Map<String,String> params, final Response.Listener<String> responder, final Response.ErrorListener errorListener) {

        super(Request.Method.DELETE, url, responder, errorListener);
        mParams = params;

        NetworkUtil.logToCurlRequest(this);

    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        return mParams;
    }
}
