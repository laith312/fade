package com.messenger.fade.util;

import android.widget.Toast;

import com.messenger.fade.application.FadeApplication;

public class FadeUtil {

    public static void FadeToast(final String message) {
        Toast.makeText(FadeApplication.getInstance().getApplicationContext(), message, Toast.LENGTH_LONG);
    }
}
