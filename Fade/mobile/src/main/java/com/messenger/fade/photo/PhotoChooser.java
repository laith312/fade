package com.messenger.fade.photo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.messenger.fade.util.ImageUtil;
import com.messenger.fade.util.MLog;
import com.messenger.fade.util.ThreadWrapper;

import java.io.File;

/**
 * This activity should be sub-classed by activities that need to
 * select photos from the gallery or snap a photo and have the
 * file ready for transfer to server.
 * <p/>
 * Scales down the photo and cannot be bigger than <code>maxPhotoSize</code> in terms
 * of bytes.
 *
 * @author kevin
 */
public abstract class PhotoChooser {

    private static final String TAG = PhotoChooser.class.getSimpleName();
    private DisplayMetrics DISPLAY_METRICS = new DisplayMetrics();

    public static final int ACTIVITY_REQUEST_SELECT_PHOTO = 388;
    public static final int ACTIVITY_REQUEST_SNAP_A_PHOTO = 389;

    private static final int EVENT_SEL_PHOTO_WRITTEN_TO_DISK = 0;
    private static final int EVENT_SEL_PHOTO_ERROR = 2;

    private static final int EVENT_PHOTO_PROC_SUCCESS = 0;
    private static final int EVENT_PHOTO_PROC_ERROR = 1;

    private Bitmap photo;
    private File photoFile;
    private int maxPhotoSize = 450000;
    private String photoFilePath;
    private Context context;

    public PhotoChooser(final Context context) {

        this.context = context;
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(DISPLAY_METRICS);

        photoFilePath = context.getCacheDir().getPath() + "/photo.jpg";
        photoFile = new File(photoFilePath);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {

        MLog.i(TAG, "onActivityResult() resultCode=" + (resultCode == Activity.RESULT_OK) + " requestCode == " + requestCode);

        if (requestCode == ACTIVITY_REQUEST_SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            final Uri uri = intent.getData();
            preProcessPhoto(uri);
        } else if (requestCode == ACTIVITY_REQUEST_SNAP_A_PHOTO && resultCode == Activity.RESULT_OK) {
            final File file = new File(photoFilePath);
            final Uri uri = Uri.fromFile(file);
            preProcessPhoto(uri);
        }
    }

    /**
     * For thumbnails/avatars you may want a size less than 200k.
     *
     * For larger photos, 450000 is recommended.
     *
     * @param maxPhotoSize - in bytes
     */
    public void setMaxPhotoSize(final int maxPhotoSize) {
        this.maxPhotoSize = maxPhotoSize;
    }

    private Intent getPhotoChoosingIntent() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }
    public void startPhotoChoosingActivityFromFragment(final Fragment fragment) {
        final Intent intent = getPhotoChoosingIntent();
        fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_REQUEST_SELECT_PHOTO);
    }

    public void startPhotoChoosingActivityFromActivity(final Activity activity) {
        final Intent intent = getPhotoChoosingIntent();
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_REQUEST_SELECT_PHOTO);
    }

    private void preProcessPhotoRetryable(final Uri uri) throws Exception {

        MLog.i(TAG, "preProcessPhotoRetryable() uri: " + uri.getPath());
        if (photo != null) {
            photo.recycle();
        }

        photo = ImageUtil.getBitmap(context, uri, maxPhotoSize);

        if (photo != null) {
            ImageUtil.writeBitmapToFile(photo, photoFilePath);
            photoFile = new File(photoFilePath);
            _photoSelectionHandler.sendEmptyMessage(EVENT_SEL_PHOTO_WRITTEN_TO_DISK);
            MLog.i(TAG, "preProcessPhotoRetryable() success");
        } else {
            MLog.i(TAG, "preProcessPhotoRetryable() fail");
            throw new Exception("Failed to get photo");
        }
    }

    /**
     * Control the size of the photo chosen from the gallery or snapped fresh
     * from the camera!
     * <p/>
     * If the photo is bigger than some threshold then reduce the size!
     *
     * @param uri
     */
    private void preProcessPhoto(final Uri uri) {

        startIndProgressForPhotoProcessing();
        ThreadWrapper.executeInWorkerThread(new Runnable() {
            @Override
            public void run() {

                final Message msg = new Message();
                try {
                    int retries = 0;
                    while (retries++ < 5) {
                        try {
                            preProcessPhotoRetryable(uri);
                            return;
                        } catch (final Exception e) {
                            Thread.sleep(750);
                        }
                    }
                    throw new Exception("Cannot read photo. Please try again..");
                } catch (final Exception e) {
                    msg.what = EVENT_SEL_PHOTO_ERROR;
                    msg.obj = e.getMessage() + "";
                    MLog.i(TAG, "preProcessPhoto() failed: ", e);
                    _photoSelectionHandler.sendMessage(msg);
                }
            }
        });
    }

    private Handler _photoDoneHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {

            dismissIndProgressForPhotoProcessing();

            MLog.i(TAG, "_photoDoneHandler msg.arg1 = " + msg.arg1);

            if (msg.arg1 == EVENT_PHOTO_PROC_SUCCESS) {

                onPhotoReady(photoFile);

            } else {
                //TODO: localize strings
                Toast.makeText(context, "Oops sorry!  Request failed.  Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * call within thread
     */
    private void adjustFinalPic() {

        MLog.i(TAG, "adjusting photo...");

        final Bitmap picToBeRecycled = photo;

        int newWidth = 0, newHeight = 0;

        int shorter, longer;
        if (DISPLAY_METRICS.widthPixels > DISPLAY_METRICS.heightPixels) {
            longer = DISPLAY_METRICS.widthPixels;
            shorter = DISPLAY_METRICS.heightPixels;
        } else {
            longer = DISPLAY_METRICS.heightPixels;
            shorter = DISPLAY_METRICS.widthPixels;
        }

	    /*
         * If it's a portrait mode oriented photo, then don't allow the height
		 * to be greater than the longest length of the phone.
		 * 
		 * If it's a landscape mode oriented photo, then don't allow the width
		 * to be longer than the longest length of the phone
		 * 
		 * Lastly, we can't keep the original dimensions of the photo, because
		 * it's just too big for mobile viewing.
		 */
        if (photo.getHeight() > photo.getWidth()) { // it's a portrait mode
            // picture
            newWidth = shorter;
            newHeight = longer;
        } else { // it's a landscape mode picture
            newWidth = longer;
            newHeight = shorter;
        }

		/*
         * lastly, if the photo is actually smaller than the dimensions of the
		 * phone, then just use those dimensions
		 */
        if (photo.getWidth() < newWidth) {
            newWidth = photo.getWidth();
        }

        if (photo.getHeight() < newHeight) {
            newHeight = photo.getHeight();
        }

        photo = ImageUtil.scale(photo, newWidth, newHeight);

        if (picToBeRecycled != null && !picToBeRecycled.isRecycled()) {
            picToBeRecycled.recycle();
        }

        MLog.i(TAG, "final pic width=" + photo.getWidth() + " pic Height=" + photo.getHeight());
    }

    /**
     * Should be called after preprocessPhoto
     */
    private void postProcessPhoto() {

        startIndProgressForPhotoProcessing();
        ThreadWrapper.executeInWorkerThread(new Runnable() {
            @Override
            public void run() {
                final Message msg = new Message();
                try {

                    adjustFinalPic();

                } catch (final Exception e) {
                    MLog.e(TAG, "Error in processing photo: ", e);
                    msg.arg1 = EVENT_PHOTO_PROC_ERROR;
                    msg.obj = "" + e.getMessage();
                }
                _photoDoneHandler.sendMessage(msg);
            }
        });
    }

    private Handler _photoSelectionHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {

            dismissIndProgressForPhotoProcessing();

            switch (msg.what) {
                case EVENT_SEL_PHOTO_WRITTEN_TO_DISK:

                    postProcessPhoto();
                    break;

                case EVENT_SEL_PHOTO_ERROR:
                    //TODO localize
                    Toast.makeText(context, "Error selecting photo. Please try again.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * This will be invoked as a callback handler when the pic raw
     * bitmaps are ready in memory and you can do whatever you want with them
     */
    public abstract void onPhotoReady(final File pic);

    public File getPicFile() {
        return photoFile;
    }

    protected void recycle() {

        if (photo != null) {
            photo.recycle();
            photo = null;
            MLog.i(TAG, "recycle() image ");
        }

    }

    public void onDestroy() {
        recycle();
        deleteTempFromDisk();
    }

    private Intent getPhotoShootingIntent() {
        deleteTempFromDisk();
        final Intent takePictureFromCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureFromCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoFilePath)));
        return takePictureFromCameraIntent;
    }

    /**
     * we have to do it like this because
     * android.provider.MediaStore.ACTION_IMAGE_CAPTURE does not work on my G1
     * with the latest cyan and 1.6.
     */
    public void startPhotoShootingActivityFromActivity(final Activity activity) {
        activity.startActivityForResult(getPhotoShootingIntent(), ACTIVITY_REQUEST_SNAP_A_PHOTO);
    }

    public void startPhotoShootingActivityFromFragment(final Fragment fragment) {
        fragment.startActivityForResult(getPhotoShootingIntent(), ACTIVITY_REQUEST_SNAP_A_PHOTO);
    }

    private void deleteTempFromDisk() {
        photoFile.delete();
    }

    public abstract void dismissIndProgressForPhotoProcessing();

    public abstract void startIndProgressForPhotoProcessing();
}