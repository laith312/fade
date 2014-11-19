package com.messenger.fade.util;

import android.os.Handler;
import android.os.Looper;

public final class ThreadWrapper {

	private static final String TAG = ThreadWrapper.class.getName();
	
	private ThreadWrapper(){}

	private static Handler mHandler;

	public static void init() {
		mHandler = new Handler();
	}

	public static void executeInWorkerThread(final Runnable task) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			// UI thread
			new Thread(task).start();
		} else {
			// Worker
			try {
				task.run();
			} catch (final Throwable t) {
				MLog.e(TAG, "uncaught error in thread", t);
			}
		}
	}
	
	public static void executeInUiThread(final Runnable task) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			// Worker thread
			mHandler.post(task);
		} else {
			// UI thread
			task.run();
		}
	}

}
