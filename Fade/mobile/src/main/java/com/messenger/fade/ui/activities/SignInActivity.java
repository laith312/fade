package com.messenger.fade.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.SignInButton;
import com.messenger.fade.FadeConstants;
import com.messenger.fade.R;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.User;
import com.messenger.fade.rest.FadeApi;

import org.json.JSONObject;

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private TextView mLoginInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Button signInButton = (Button) findViewById(R.id.sign_in_button);
        final TextView signUpTextView = (TextView) findViewById(R.id.sign_up_text);
        final SignInButton googleButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mLoginInfo = (TextView) findViewById(R.id.login_info);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignIn();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignInActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        googleButton.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sign_in;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.google_sign_in_button) {
            Intent intent = new Intent(this, GooglePlusLoginActivity.class);
            startActivityForResult(intent, FadeConstants.REQUEST_CODE_GOOGLE_PLUS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == FadeConstants.REQUEST_CODE_GOOGLE_PLUS) {
            // Login with 3rd party.

            final String email = data.getStringExtra(FadeConstants.EXTRA_EMAIL);
            final String token = data.getStringExtra(FadeConstants.EXTRA_ACCESS_TOKEN);

            mLoginInfo.setText(email + " : " + token);
        }
    }

    private void userSignIn() {


        FadeApi.authenticateByUsername("", "testGuy123", "12345", new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String s) {

                        System.out.println("onResponse(): " + s);

                        try {
                            final JSONObject response = new JSONObject(s);

                            Log.e("zzz", "response:" + response.toString());

                            if (response.getBoolean(FadeApi.API_RESULT_SUCCESS_KEY)) {
                                final User user = User.from(response.getJSONObject(FadeApi.API_RESULT_DATA_KEY));

                                FadeApplication.setMe(user);

                                Intent i = new Intent(SignInActivity.this, FadeNavActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                //probably wrong password or invalid user
                                System.out.println(response.getString(FadeApi.API_RESULT_DESCR_KEY));
                            }

                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError volleyError) {
                        System.out.println("volleyError: " + volleyError);
                    }
                });
    }
}
