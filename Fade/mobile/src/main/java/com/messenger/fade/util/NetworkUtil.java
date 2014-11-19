

package com.messenger.fade.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;


/**
 * Various networking utilities including:
 * (1) Logging Volley api requests to curl requests
 * (2) Checking whether a device is connected to a network, or WiFi specifically
 */
public final class NetworkUtil {


    private static final String TAG = NetworkUtil.class.getCanonicalName();

    private NetworkUtil() {
    }


    /**
     *
     * ============================================================================================

     *  Logging Volley API requests to curl requests
     *
     *  ===========================================================================================
     *
     */

    /**
     * Usage: in the constructor of the base API call object, e.g. ApiGetRequest, insert or uncomment
     * the following line at the end of the constructor:  NetworkUtil.logToCurlRequest(this);
     */
    public static void logToCurlRequest(Request<?> request) {

      /*
       * avoid all this string operations, if we're
       * not going to use it anyways.
       */
        if (!MLog.isEnabled()) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("curl ");
        builder.append("-X \"");
        switch (request.getMethod()) {
            case Method.POST:
                builder.append("POST");
                break;
            case Method.GET:
                builder.append("GET");
                break;
            case Method.PUT:
                builder.append("PUT");
                break;
            case Method.DELETE:
                builder.append("DELETE");
                break;
        }

        builder.append("\"");

        try {
            if (request.getBody() != null) {
                builder.append(" -d ");
                String data = new String(request.getBody());
                data = data.replaceAll("\"", "\\\"");
                builder.append("\"");
                builder.append(data);
                builder.append("\"");
            }
            for (String key : request.getHeaders().keySet()) {
                builder.append(" -H '");
                builder.append(key);
                builder.append(": ");
                builder.append(request.getHeaders().get(key));
                builder.append("'");
            }
            builder.append(" \"");
            builder.append(request.getUrl());
            builder.append("\"");

            MLog.i(TAG, builder.toString());
        } catch (AuthFailureError e) {
            MLog.e(TAG, "Unable to get body of response or headers for curl logging");
        }
    }


    /**
     *
     * ============================================================================================

     *  Checking connection status
     *
     *  ===========================================================================================
     *
     */

    /**
     * Make sure <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * is added to manifest!
     */
    public static boolean isConnected(final Context context) {
        try {
            final NetworkInfo networkInfo = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();

            return networkInfo != null && networkInfo.isConnected();
        } catch (final Exception e) {
            MLog.w(TAG, "network detection exception..", e);
        }
        return true;
    }


    public static boolean isConnectedToWifi(final Context context) {
        try {
            final ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
        } catch (final Exception e) {
            MLog.e(TAG, "", e);
        }
        return false;
    }


}
