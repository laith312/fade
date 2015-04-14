package com.messenger.fade;

import android.test.AndroidTestCase;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.Identity;
import com.messenger.fade.model.User;
import com.messenger.fade.rest.FadeApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kkawai on 11/19/14.
 */
public class ApiTest extends AndroidTestCase {

    public void _testGetRegisteredEmailsFromList() {

        final String listOfEmails = "testGuy1234@gmail.com laith.alnagem@gmail.com laith.alnagem+1@gmail.com laith.alnagem+2@gmail.com testUser888@gmail.com laith.alnagem+3@gmail.com blahblahblah@blah.com fakeclown@TEST.COM";
        FadeApplication.setRequestQueue(getContext());
        System.out.println("Testing get registered emails from list of email");
        FadeApi.getEmailExists("",listOfEmails,new Response.Listener<String>() {
            @Override
            public void onResponse(final String s) {
                try {
                    final JSONObject response = new JSONObject(s);
                    final JSONArray data = response.getJSONArray("data");
                    for (int i=0;i<data.length();i++) {
                        System.out.println("User with email: "+ data.getJSONObject(i));
                    }
                }catch(final Exception e) {
                    System.out.println("Email error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("User with email error: "+ volleyError.toString());
            }
        });
    }

    public void testAuthUser() {

        _testGetRegisteredEmailsFromList();

        final AtomicBoolean isNetworkCallFinished = new AtomicBoolean(false);
        final AtomicBoolean isEverythingSuccess = new AtomicBoolean(false);

        FadeApplication.setRequestQueue(getContext());
        FadeApi.authenticateByUsername("", "testGuy123", "12345", new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String s) {

                        System.out.println("onResponse(): " + s);

                        try {
                            final JSONObject response = new JSONObject(s);
                            if (response.getBoolean(FadeApi.API_RESULT_SUCCESS_KEY)) {
                                final User user = User.from(response.getJSONObject(FadeApi.API_RESULT_DATA_KEY));

                        /*
                         * NOTE: In the actual app, make sure to call
                          * FadeApplication.setMe(user);
                          * after authenticating.
                          *
                          * It will crash in a unit test.
                         */
                                System.out.println("fetched user: " + user.toString());
                                _saveUser(user, isNetworkCallFinished, isEverythingSuccess);
                            } else {
                                //probably wrong password or invalid user
                                System.out.println(response.getString(FadeApi.API_RESULT_DESCR_KEY));
                                isNetworkCallFinished.set(true);
                            }

                        } catch (final Exception e) {
                            isNetworkCallFinished.set(true);
                            e.printStackTrace();
                        }

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

        assertTrue(isEverythingSuccess.get());
    }

    private void _saveUser(final User user, final AtomicBoolean isFinished, final AtomicBoolean isSuccess) {

        final String newBio = "new bio and all that set via android unit test";

        user.setBio(newBio);
        FadeApi.saveUser("", user, new Response.Listener<String>() {
            @Override
            public void onResponse(final String s) {
                try {
                    final User u = User.from(new JSONObject(s).getJSONObject(FadeApi.API_RESULT_DATA_KEY));
                    assertEquals(newBio, u.getBio());
                    _getIdentities(u, isFinished, isSuccess);
                } catch (final Exception e) {
                    e.printStackTrace();
                    isFinished.set(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("volleyError: " + volleyError);
                isFinished.set(true);
            }
        });
    }

    private void _getIdentities(final User user, final AtomicBoolean isFinished, final AtomicBoolean isSuccess) {

        FadeApi.getIdentities("",user.getId(),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject jsonObject) {
                try {
                    final JSONArray array = jsonObject.getJSONArray(FadeApi.API_RESULT_DATA_KEY);
                    final Identity identity = Identity.from(array.getJSONObject(0));
                    identity.setStatus((byte)2);
                    identity.setThirdPartyId("3rdPartyIdUnitTest");
                    _saveIdentity(identity, isFinished, isSuccess);

                } catch (final Exception e) {
                    e.printStackTrace();
                    isFinished.set(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                System.out.println("volleyError: " + volleyError);
                isFinished.set(true);
            }
        });
    }

    private void _saveIdentity(final Identity identity, final AtomicBoolean isFinished, final AtomicBoolean isSuccess) {

        FadeApi.saveIdentity("", identity, new Response.Listener<String>() {
            @Override
            public void onResponse(final String s) {
                try {

                    System.out.println("identity save response: "+s);
                    //_deleteIdentity(2, isFinished, isSuccess);

                    /*
                     * remove calling delete identity after ensuring
                     * it works fine.  don't want to accidentally delete
                     * anything to trip us up later.
                     */
                    isSuccess.set(true);
                    isFinished.set(true);

                } catch (final Exception e) {
                    e.printStackTrace();
                    isFinished.set(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                System.out.println("volleyError: " + volleyError);
                isFinished.set(true);
            }
        });
    }

    private void _deleteIdentity(final long id, final AtomicBoolean isFinished, final AtomicBoolean isSuccess) {
        FadeApi.deleteIdentity("", id, new Response.Listener<String>() {
            @Override
            public void onResponse(final String s) {
                try {

                    System.out.println("identity delete response: "+s);
                    isSuccess.set(true);
                    isFinished.set(true);

                } catch (final Exception e) {
                    e.printStackTrace();
                    isFinished.set(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                System.out.println("volleyError: " + volleyError);
                isFinished.set(true);
            }
        });
    }

}
