package com.messenger.fade;

import android.content.Intent;
import android.os.Bundle;

import com.messenger.fade.ui.SecretTextView;
import com.messenger.fade.ui.activities.BaseActivity;
import com.messenger.fade.util.ThreadWrapper;

/**
 * Created by kkawai on 1/5/15.
 */
public final class MockDisplayFadeActivity extends BaseActivity {

    private SecretTextView mFade;
    private boolean mIsDestroyed;

    @Override
    protected int getLayoutResource() {
        return R.layout.mock_fade_display;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFade = (SecretTextView)findViewById(R.id.fade);
        mFade.setText("");
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String text = getIntent().getStringExtra(Constants.PROPERTY_TEXT);
        mFade.setText(text);
        mFade.show();
        startTimer();
    }

    private void startTimer() {
        ThreadWrapper.executeInWorkerThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if (mIsDestroyed) return;
                    hide();
                    Thread.sleep(2000);
                    if (mIsDestroyed) return;
                    finishActivity();
                }catch(final InterruptedException e) {

                }
            }
        });
    }

    private void hide() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFade.hide();
            }
        });
    }

    private void finishActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mIsDestroyed = true;
        super.onDestroy();
    }
}
