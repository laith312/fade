/**
 * 
 */
package com.messenger.fade.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.messenger.fade.R;


/**
 * Helper class for getting screen dimensions and doing dimensional pixel conversion;
 * @author petey
 * 
 */
public final class Screen {
	
	private Screen() {}

	public static final int SCREEN_DENSITY_LOW = 120,
							SCREEN_DENSITY_MEDIUM = 160,
							SCREEN_DENSITY_TV = 213,
							SCREEN_DENSITY_HIGH = 240,
							SCREEN_DENSITY_XTRA_HIGH = 320;

	private static int screenDpi = -1,
		screenWidth = -1, 
		screenHeight = -1;
	private static float density = -1;

	public static int dpToPx(Context context, int dp) {
    	float density = getDensity(context);
		return (int) (dp * density + 0.5f);
	}
	

	public static Drawable scaleDrawable(Context context,
			int drawableResourceId, int width, int height) {
		Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResourceId);
		return new BitmapDrawable(Bitmap.createScaledBitmap(sourceBitmap,
				width, height, true));
	}
	
	
	static class Dimension {
		int width, height;
		float density;
	}
	
	public static Dimension getDimensions(Context context){
		return getDimensions(context, 0);	
	}
	/**
	 * Get Screen Dimension related to a specified orientation
	 * @param context
	 * @param rotation
	 * @return
	 */
	public static Dimension getDimensions(Context context, int rotation){
		Dimension d = new Dimension();
		d.height = getScreenHeight(context);
		d.width = getScreenWidth(context);
		d.density = getDensity(context);
		return d;
	}
	
	private static Display getDefaultDisplay(Context context){
		return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
	public static boolean isTablet(final Context context){
		return context.getResources().getBoolean(R.bool.isTablet);
	}
	
	public static boolean isLandscapeOrientation(final Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	public static boolean isPortraitOrientation(final Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}
	
	public static float getDensity(Context context){
		if (density == -1){
			DisplayMetrics metrics = new DisplayMetrics();
			getDefaultDisplay(context).getMetrics(metrics);
			density =  metrics.density;
		}
		return density;
	}
	
	public static int getBetterOrientation(Context context){
		screenDpi = -1;
		screenWidth = -1; 
		screenHeight = -1;
		Dimension d = getDimensions(context);
		if (d.width < d.height){
			return Configuration.ORIENTATION_PORTRAIT;
		} else if (d.width > d.height){
			return Configuration.ORIENTATION_LANDSCAPE;
		} else if (d.width == d.height){
			return Configuration.ORIENTATION_SQUARE;
		}
		return Configuration.ORIENTATION_UNDEFINED;
	}



	public static int getScreenWidth(Context context) {
		if (screenWidth == -1){
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			screenWidth = display.getWidth();
		}
		return screenWidth;
	}
	
	
	public static void disableRotation(Activity activity) {
		final int orientation = activity.getResources().getConfiguration().orientation;
		final int rotation = activity.getWindowManager().getDefaultDisplay()
				.getOrientation();

		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} else if (rotation == Surface.ROTATION_180
				|| rotation == Surface.ROTATION_270) {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			}
		}
	}

	public static void enableRotation(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
	public static int getScreenHeight(Context context) {
		if (screenHeight == -1){
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			screenHeight = display.getHeight();
		}
		return screenHeight;
	}

	public static int getScreenDpi(Context context) {
		if (screenDpi == -1) {
			DisplayMetrics displayMetrics = context.getResources()
					.getDisplayMetrics();
			try {
				screenDpi = DisplayMetrics.class.getField("densityDpi")
						.getInt(displayMetrics);
			} catch (Exception e) {
				screenDpi = SCREEN_DENSITY_MEDIUM;
			}
		}
		return screenDpi;
	}

	/**
	 * Attempts to apply a 'safe-zone' padding to a given view.
	 * To be used in tv-related apps.
	 * 
	 * @param view
	 */
	public static void applySafeZoneToView(final View view) {

		final Display display = getDefaultDisplay(view.getContext());
		final Point size = new Point();
		display.getSize(size);
		final int padWidthPixels = (size.x / 10) / 2;
		final int padHeightPixels = (size.y / 10) / 2;
		view.setPadding(padWidthPixels, padHeightPixels, padWidthPixels,
				padHeightPixels);
	}
	
	public static boolean isLowResolutionDevice(final Activity a) {
		
		Display display = a.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		// as a rule of thumb, any screen 480 pixels or smaller is pretty low resolution,
		// regardless of screen density which probably won't be great either
		if ( size.x <= 480 )  
			return true;
		
		return false;
	}
	
	public static boolean isAutoScreenRotateEnabled(final Context context) {
		return Settings.System.getInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
	}
	
	//https://developer.android.com/training/system-ui/immersive.html
	public static void showSystemUI(final Window window) {
		
		/*
		 * no matter how tempting it is to 
		 * modify the following code, don't.  it's a rabbit hole.
		 */
		if ( Build.VERSION.SDK_INT >= 19 ) {
			window.getDecorView().setSystemUiVisibility(
		            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		} 


	}
	
	//https://developer.android.com/training/system-ui/immersive.html
	public static void hideSystemUI(final Window window) {

		/*
		 * no matter how tempting it is to 
		 * modify the following code, don't.  it's a rabbit hole.
		 */
		if ( Build.VERSION.SDK_INT >= 19 ) {  //kitkat
			window.getDecorView().setSystemUiVisibility(
		            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
		            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
		            | View.SYSTEM_UI_FLAG_IMMERSIVE);
		} 

		
	}
	
	/**
	 * 
	 * @param view - The view whose window token
	 * has an attached keyboard.
	 */
	public static void hideVirtualKeyboard(final View view) {
		try {
		    ((InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
		}catch(final Throwable t){}
	}

	/**
	 * Calculates preferred/optimal portrait-mode video height assuming:
	 * 
	 * 1) 16:9 aspect ratio
	 * 2) full-screen width
	 * 
	 * @param c
	 * @return
	 */
	public static int getPortraitVideoHeight(final Context c) {
		final WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		final Display display = wm.getDefaultDisplay();
		final Point size = new Point();
		display.getSize(size);
		final int deviceWidth = size.x < size.y ? size.x : size.y;
		return 9 * deviceWidth / 16;
	}
	
	
	public static int getPortraitHeight(final Context c) {
		final WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		final Display display = wm.getDefaultDisplay();
		final Point size = new Point();
		display.getSize(size);
		return size.y > size.x ? size.y : size.x;
	}
	
	/**
	 * If device has physical navigation keys (BACK KEY) return 0.
	 * However, if device does not have physical buttons, return
	 * the height of the navigation bar.
	 * 
	 * @return
	 */
	public static int getNavigationBarHeight(final Context c) {
		
		final boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

		if (!hasBackKey) {
			final int resourceId = c.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			return resourceId > 0 ? c.getResources().getDimensionPixelSize(resourceId) : 0;
		} else {
			return 0;
		}
	}	
}
