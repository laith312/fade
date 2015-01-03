package com.messenger.fade.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.messenger.fade.R;
import com.messenger.fade.util.FadeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity {

    Button mSubmitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubmitButton = (Button) findViewById(R.id.registration_submit);

        final EditText emailView = (EditText) findViewById(R.id.registration_email);
        final EditText usernameView = (EditText) findViewById(R.id.registration_username);
        final EditText passwordView = (EditText) findViewById(R.id.registration_password);


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidInput(emailView.getText().toString(), usernameView.getText().toString(), passwordView.getText().toString())) {
                    // TODO save/create user FadeApi.
                }


            }
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_registration;
    }

    private boolean isValidInput(String email, String username, String password) {

        // TODO: check strings

        // check email string
        if (!isValidEmail(email)) {
            FadeUtil.FadeToast("Please enter a valid email");
            return false;
        }

        // check username
        if (!isValidUserName(username)) {
            FadeUtil.FadeToast("Please enter a valid username");
            return false;
        }

        // check password
        if (!isValidPassword(password)) {
            FadeUtil.FadeToast("Please enter a valid username");
            return false;
        }

        // TODO check for existing email

        // TODO: check  existing username


        return true;
    }

    private static boolean isValidEmail(final String email) {

        final Pattern pattern = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );

        return pattern.matcher(email).matches();
    }

    private static boolean isValidPassword(final String password) {

        return password.length() < 5 ? false : true;
    }

    private static boolean isValidUserName(final String name) {

        if (name.length() < 2)
            return false;

        Matcher matcher = Pattern.compile("^[A-Za-z0-9-_@.]+$").matcher(name);
        return matcher.find();
    }
}
