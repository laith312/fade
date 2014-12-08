package com.messenger.fade.ui.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.messenger.fade.FadeConstants;
import com.messenger.fade.util.MLog;

import java.io.IOException;

public class GooglePlusLoginActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GooglePlusLoginActivity.class.getSimpleName();
    private static final int GOOGLE_REQUEST = 9007;

    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signInToGooglePlus();
    }

    private void signInToGooglePlus() {
        final Intent intent = AccountPicker
                .newChooseAccountIntent(null, null, new String[]{"com.google"},
                        false, null, null, null, null);
        startActivityForResult(intent, GOOGLE_REQUEST);
    }

    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, FadeConstants.REQUEST_CODE_GOOGLE_PLUS);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                final String scope = "oauth2:https://www.googleapis.com/auth/plus.login";

                //User.setGoogleUserName(email.substring(0, email.indexOf('@')));

                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(final Void... params) {
                        try {

                            return GoogleAuthUtil.getToken(GooglePlusLoginActivity.this, email, scope);

                        } catch (final IOException transientEx) {
                            Log.e(TAG, transientEx.toString());
                        } catch (final UserRecoverableAuthException e) {
                            Log.e(TAG, e.toString());
                        } catch (final GoogleAuthException authEx) {
                            Log.e(TAG, authEx.toString());
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(final String token) {

                        MLog.e(TAG, "Google Plus :: email:" + email + " token:" + token);

                        final Intent intent = getIntent();
                        intent.putExtra(FadeConstants.EXTRA_ACCESS_TOKEN, token);
                        intent.putExtra(FadeConstants.EXTRA_EMAIL, email);
                        setResult(RESULT_OK, intent);

                        mGoogleApiClient.disconnect();
                        finish();
                    }
                };
                task.execute();
            } else {
                Log.e(TAG, "Person information is null");
                Toast.makeText(this, "Error Fetching User Data", Toast.LENGTH_LONG).show();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }

        if (requestCode == GOOGLE_REQUEST) {
            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).setAccountName(accountName)
                    .build();
            mGoogleApiClient.connect();
            mIntentInProgress = false;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        signOutFromGplus();
        revokeGplusAccess();
    }

    @Override
    public void onConnected(final Bundle bundle) {
        MLog.i(TAG, "onConnected - GooglePlus()");
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(final int i) {
        MLog.i(TAG, "onConnectionSuspended - GooglePlus()");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    private void revokeGplusAccess() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(final Status arg0) {
                            MLog.i(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                        }
                    });
        }
    }
}
