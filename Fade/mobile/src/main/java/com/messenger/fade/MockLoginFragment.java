package com.messenger.fade;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.User;
import com.messenger.fade.rest.FadeApi;
import com.messenger.fade.ui.fragments.BaseFragment;
import com.messenger.fade.util.FadeUtil;
import com.messenger.fade.util.MLog;

import org.json.JSONObject;

/**
 *
 * Just a mock fragment to help demonstrate sending fades to each other.
 *
 * Created by kkawai on 1/4/15.
 */
public final class MockLoginFragment extends BaseFragment {

    public static final String TAG = MockLoginFragment.class.getSimpleName();

    public static final String MOCK_USER_123 = "testGuy123";
    public static final String MOCK_USER_888 = "testUser888";

    public static final int MOCK_USER_ID_123 = 1;
    public static final int MOCK_USER_ID_888 = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mock_login_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AQuery a = new AQuery(getView());
        a.id(R.id.testguy123).clicked(this, "loginTestGuy123");
        a.id(R.id.testuser888).clicked(this, "loginTestUser888");
    }

    public void loginTestGuy123() {
        login(MOCK_USER_123, "12345");

    }

    public void loginTestUser888() {
        login(MOCK_USER_888, "12345");
    }

    private void login(final String username, final String password) {
        FadeApi.authenticateByUsername(this, username, password, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    final JSONObject response = new JSONObject(s);
                    final User user = User.from(response.getJSONObject("data"));
                    FadeApplication.setMe(user);
                    MLog.i(TAG, "logged in: " + user.toString());
                    FadeUtil.FadeToast(user.getUsername() + " logged in...");
                    goToMockContacts();
                }catch(final Exception e) {
                    MLog.e(TAG,"",e);
                    FadeUtil.FadeToast("failed to login: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                MLog.e(TAG,"failed: " + volleyError.getMessage());
                FadeUtil.FadeToast("failed to login: " + volleyError.getMessage());
            }
        });
    }

    private void goToMockContacts() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final BaseFragment fragment = new MockContactsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FadeApi.cancelAll(this);
    }
}
