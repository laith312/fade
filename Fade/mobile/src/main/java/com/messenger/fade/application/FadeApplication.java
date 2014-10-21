package com.messenger.fade.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.messenger.fade.util.BitmapLruCache;


public class FadeApplication extends Application {

    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;

    private static FadeApplication instance;


    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;

        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new BitmapLruCache());
    }

    public static FadeApplication getInstance() {
        return instance;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }
}
