package com.messenger.fade.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.messenger.fade.R;
import com.messenger.fade.model.UserMedia;
import com.messenger.fade.photo.PhotoChooser;
import com.messenger.fade.rest.FadeApi;
import com.messenger.fade.util.FileUploader;
import com.messenger.fade.util.MLog;

import java.io.File;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private Fragment _this = this;
    private PhotoChooser photoChooser;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final AQuery a = new AQuery(getView());
        a.id(R.id.profile_image).clicked(this, "onProfileClicked");
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (requestCode == PhotoChooser.ACTIVITY_REQUEST_SELECT_PHOTO || requestCode == PhotoChooser.ACTIVITY_REQUEST_SNAP_A_PHOTO) {
            photoChooser.onActivityResult(requestCode,resultCode,data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onProfileClicked() {
        photoChooser = new PhotoChooser(getActivity()) {
            @Override
            public void onPhotoReady(final File pic) {

                MLog.i(TAG, "onPhotoReady(): " + pic.getPath());
                final UserMedia media = new UserMedia();
                media.setCaption("test photo");
                media.setLikes(3);
                media.setMediaType(UserMedia.MEDIA_TYPE_IMAGE);
                media.setUserid(1);
                FadeApi.saveUserMediaToGallery(_this, pic, media, new FileUploader.ProgressListener () {
                    @Override
                    public void onProgress(final int percentComplete) {
                        MLog.i(TAG, "file post: " + percentComplete + "%");
                    }
                }, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String s) {
                        MLog.i(TAG, "onResponse(): file complete: " +s);
                    }
                }, new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(final VolleyError volleyError) {
                        MLog.i(TAG, "onErrorResponse(): file complete: " +volleyError.toString());
                    }
                });
            }

            @Override
            public void startIndProgressForPhotoProcessing() {

            }

            @Override
            public void dismissIndProgressForPhotoProcessing() {

            }
        };
        photoChooser.startPhotoChoosingActivityFromFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photoChooser.onDestroy();
    }
}
