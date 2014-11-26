package com.messenger.fade.util;


import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;


public class BitmapLruCache implements ImageCache {

    private final static int BITMAP_CACHE_MEMORY_DENOMINATOR = 16;
    private LruCache<String, Bitmap> mMemoryCache;

    public BitmapLruCache() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / BITMAP_CACHE_MEMORY_DENOMINATOR;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mMemoryCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (getBitmap(url) == null) {
            mMemoryCache.put(url, bitmap);
        }
    }

    public void removeBitmap(final String url) {

        for (final String key : mMemoryCache.snapshot().keySet()) {
            MLog.i(BitmapLruCache.class.getSimpleName(), "cache key: " + key);
            if (key.contains(url)) {
                final Bitmap b = mMemoryCache.remove(key);
                MLog.i(BitmapLruCache.class.getSimpleName(), "removed bitmap from cache: " + b);
                break;
            }
        }


    }

    /*public void evictAll() {
        mMemoryCache.evictAll();
    }*/
}
