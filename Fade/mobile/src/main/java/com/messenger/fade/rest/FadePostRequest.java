package com.messenger.fade.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.messenger.fade.util.NetworkUtil;

import java.util.Map;

public class FadePostRequest extends StringRequest {

    private static final String TAG = FadePostRequest.class.getSimpleName();

    private Map<String,String> mParams;

    public FadePostRequest(final String url, final Map<String,String> params, final Response.Listener<String> responder, final Response.ErrorListener errorListener) {

        super(Request.Method.POST, url, responder, errorListener);
        mParams = params;

        NetworkUtil.logToCurlRequest(this);

    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    /*
     * we are not using header session token authorization
     * yes, fade is totally insecure for now lolz
     */
//    @Override
//    public HashMap<String, String> getHeaders() {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(ApiV2.AUTH_HEADER, ApiV2.AUTH_HEADER_BEARER + User.getSessionToken());
//        return params;
//    }
}
