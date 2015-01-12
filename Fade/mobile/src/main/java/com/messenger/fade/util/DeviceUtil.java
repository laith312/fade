package com.messenger.fade.util;

import android.content.Context;
import android.provider.Settings.Secure;

public final class DeviceUtil {
    private DeviceUtil(){}
	public static String getAndroidId(final Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
}
