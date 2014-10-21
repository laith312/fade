package com.messenger.fade.util;

import android.util.Log;

public class FadeLog {

    public static void e(final String tag, final String message) {
        Log.e(tag, message);
    }

    public static void d(final String tag, final String message) {
        Log.d(tag, message);
    }

    public static void i(final String tag, final String message) {
        Log.i(tag, message);
    }

    public static void w(final String tag, final String message) {
        Log.w(tag, message);
    }

    public static void v(final String tag, final String message) {
        Log.v(tag, message);
    }
}
