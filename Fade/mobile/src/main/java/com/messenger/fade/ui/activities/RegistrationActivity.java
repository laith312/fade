package com.messenger.fade.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.messenger.fade.R;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.User;
import com.messenger.fade.rest.FadeApi;
import com.messenger.fade.util.FadeUtil;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity {

    private boolean mValidEmail;
    private boolean mValidUsername;
    private boolean mValidPassword;

    private boolean mCheckingEmail;
    private boolean mCheckingUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final EditText emailView = (EditText) findViewById(R.id.registration_email);
        emailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mValidEmail = false;

                    final String email = emailView.getText().toString();

                    if (email != null) {
                        validateEmail(email);
                    }
                }
            }
        });

        final EditText usernameView = (EditText) findViewById(R.id.registration_username);
        usernameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mValidUsername = false;

                    final String username = usernameView.getText().toString();

                    if (username != null) {
                        validateUsername(username);
                    }
                }
            }
        });


        final EditText passwordView = (EditText) findViewById(R.id.registration_password);
        passwordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus) {
                    mValidPassword = false;

                    final String password = passwordView.getText().toString();

                    if (password != null) {
                        validatePassword();
                    }
                }
            }
        });


        final Button mSubmitButton = (Button) findViewById(R.id.registration_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("zzz", "email:" + mValidEmail + " username:" + mValidUsername + " password:" + mValidPassword);

                //validateEmail(emailView.getText().toString());
                //validateUsername(usernameView.getText().toString());
                validatePassword();

                if (mValidEmail && mValidPassword && mValidUsername) {
                    Log.e("zzz", "createUser");
                    createUser(emailView.getText().toString(), usernameView.getText().toString(), passwordView.getText().toString());
                    startActivity(new Intent(RegistrationActivity.this, FadeNavActivity.class));
                    finish();
                } else if (mCheckingEmail || mCheckingUsername) {
                    // TODO add delay and check again, waiting for email || username verification
                }


            }
        });

    }

    private void createUser(String email, String username, String password) {
        User user = new User();

        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);

        Log.e("zzz", "createUser:: email:" + email + " username:" + username + " password:" + password);

        FadeApi.saveUser(RegistrationActivity.class.getSimpleName(), user, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.e("zzz", "createResponse:" + s);
                    final User u = User.from(new JSONObject(s).getJSONObject(FadeApi.API_RESULT_DATA_KEY));
                    FadeApplication.setMe(u);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_registration;
    }

    private void validateEmail(final String email) {
        mValidEmail = false;

        final Pattern pattern = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );

        if (!pattern.matcher(email).matches()) {
            FadeUtil.FadeToast("invalid email address");
        }

        mCheckingEmail = true;

        FadeApi.isEmailExists(RegistrationActivity.class.getSimpleName(), email, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject jsonObject) {
                Log.e("zzz", "response:" + jsonObject.toString());

                try {

                    if (jsonObject.getBoolean(FadeApi.API_RESULT_SUCCESS_KEY)) {
                        if (jsonObject.getJSONObject(FadeApi.API_RESULT_DATA_KEY).getBoolean(FadeApi.API_RESULT_EXISTS_KEY)) {
                            FadeUtil.FadeToast("Email is already registered, please log in");

                            mValidEmail = false;

                            Intent i = new Intent(RegistrationActivity.this, SignInActivity.class);
                            i.putExtra(SignInActivity.EMAIL, email);
                        } else {
                            mValidEmail = true;
                        }
                    }
                    mCheckingEmail = false;
                } catch (Exception e) {
                    mValidEmail = false;
                    mCheckingEmail = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                mValidEmail = false;
                mCheckingEmail = false;
            }
        });
    }

    private void validatePassword() {
        EditText passwordView = (EditText) findViewById(R.id.registration_password);

        final String password = passwordView.getText().toString();

        if (password.length() < 5) {
            FadeUtil.FadeToast("Invalid password length");
            mValidPassword = false;
        } else {
            mValidPassword = true;
        }
    }

    private void validateUsername(final String username) {
        mValidUsername = false;

        if (username.length() < 2) {
            FadeUtil.FadeToast("Invalid username, username is too short");
        }


        Pattern pattern = Pattern.compile("^[A-Za-z0-9-_@.]+$");

        if (!pattern.matcher(username).find()) {
            FadeUtil.FadeToast("Invalid username");
        }

        mCheckingUsername = true;
        FadeApi.isUsernameExists(RegistrationActivity.class.getSimpleName(), username, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("zzz", "response2:" + jsonObject.toString());

                try {

                    if (jsonObject.getBoolean(FadeApi.API_RESULT_SUCCESS_KEY)) {
                        if (jsonObject.getJSONObject(FadeApi.API_RESULT_DATA_KEY).getBoolean(FadeApi.API_RESULT_EXISTS_KEY)) {
                            FadeUtil.FadeToast("Username unavailable");

                            mValidUsername = false;

                            Intent i = new Intent(RegistrationActivity.this, SignInActivity.class);
                            i.putExtra(SignInActivity.USERNAME, username);
                        } else {
                            mValidUsername = true;
                        }
                    }
                    mCheckingUsername = false;
                } catch (Exception e) {
                    mValidUsername = false;
                    mCheckingUsername = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mValidUsername = false;
                mCheckingUsername = false;
            }
        });

    }
}
