package com.messenger.fade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.rest.FadeApi;
import com.messenger.fade.ui.fragments.BaseFragment;
import com.messenger.fade.util.FadeUtil;
import com.messenger.fade.util.MLog;
import com.messenger.fade.util.StringUtil;

import org.json.JSONObject;

/**
 * Created by kkawai on 1/5/15.
 */
public final class MockChatFragment extends BaseFragment {

    public static final String TAG = MockChatFragment.class.getSimpleName();

    private EditText mMessage;
    private int mToId;
    private String mToUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mock_chat_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToId = getArguments().getInt(MessageConstants.PROPERTY_USERID);
        mToUsername = getArguments().getString(MessageConstants.PROPERTY_USERNAME);
        final AQuery a = new AQuery(getView());
        a.id(R.id.send).clicked(this, "send");
        mMessage = (EditText) getView().findViewById(R.id.message);
        mMessage.setHint("Send FADE to " + mToUsername);

    }

    public void send() {
        final String m = mMessage.getText().toString();
        if (StringUtil.isEmpty(m)) {
            return;
        }

        final JSONObject msg = new JSONObject();
        try {
            msg.put(MessageConstants.PROPERTY_TEXT, m);
            msg.put(MessageConstants.PROPERTY_USERNAME, FadeApplication.me().getUsername());
            msg.put(MessageConstants.PROPERTY_USERID, FadeApplication.me().getId());
            FadeApi.gcmSend(this, mToId, msg.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    FadeUtil.FadeToast("FADE sent");
                    mMessage.setText("");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    MLog.e(TAG, "FadeApi.gcmSend() error: " + volleyError.getMessage());
                    FadeUtil.FadeToast("FADE could not be sent: " + volleyError.getMessage());
                }
            });
        } catch (final Exception e) {
            MLog.e(TAG, "", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FadeApi.cancelAll(this);
    }
}
