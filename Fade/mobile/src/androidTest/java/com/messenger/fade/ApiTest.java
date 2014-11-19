package com.messenger.fade;

import android.test.AndroidTestCase;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.User;
import com.messenger.fade.rest.FadeApi;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kkawai on 11/19/14.
 */
public class ApiTest extends AndroidTestCase {

    public void testAuthUser() {

        final AtomicBoolean isNetworkCallFinished = new AtomicBoolean(false);
        final AtomicBoolean isFetchedUserOK = new AtomicBoolean(false);

        FadeApplication.setRequestQueue(getContext());
        FadeApi.authenticateByUsername("", "testGuy123", "12345", new Response.Listener<String>() {
            @Override
            public void onResponse(final String s) {

                System.out.println("onResponse(): " + s);

                try {
                    final JSONObject response = new JSONObject(s);
                    if (response.getString(FadeApi.API_RESULT_STATUS_KEY).equals(FadeApi.API_RESULT_OK)) {
                        final User user = User.from(response.getJSONObject(FadeApi.API_RESULT_DATA_KEY));

                        /*
                         * NOTE: In the actual app, make sure to call
                          * FadeApplication.setMe(user);
                          * after authenticating.
                          *
                          * It will crash in a unit test.
                         */
                        System.out.println("fetched user: " + user.toString());
                        isFetchedUserOK.set(true);
                    } else {
                        //probably wrong password or invalid user
                        System.out.println(response.getString(FadeApi.API_RESULT_DESCR_KEY));
                    }

                }catch(final Exception e) {
                    e.printStackTrace();
                }
                isNetworkCallFinished.set(true);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                System.out.println("volleyError: " + volleyError);
                isNetworkCallFinished.set(true);
            }
        });

        while (isNetworkCallFinished.get() == false) {
            try {
                System.out.println("waiting for network calls to finish....");
                Thread.sleep(1000);
            }catch(final InterruptedException e) {
                break;
            }
        }

        assertTrue(isFetchedUserOK.get());
    }

}
