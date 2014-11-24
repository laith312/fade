package com.messenger.fade.util;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;


/**
 * Created by kkawai on 11/22/14.
 */
public final class FileUploader {

   private static final String URL = "http://api.fadechat.com/v1/fs";

   private MultipartUtility multipartUtility;

   public interface ProgressListener {
      void onProgress(int percentComplete);
   }

   public void cancel() {
      if (multipartUtility == null)
         return;
      multipartUtility.cancel();
   }

   /**
    *
    * @param fileName - S3 key.  make sure that has a UUID
    * @param file - actual file to post
    * @param targetBucket - target S3 bucket
    * @param listener - progress
    * @param responder - when it's done
    * @param errorListener - error listener
    */
   public void postFile(final String fileName, final File file, final String targetBucket, final ProgressListener listener, final Response.Listener<String> responder, final Response.ErrorListener errorListener) {

      final HashMap<String,String> params = new HashMap<String, String>();
      params.put("b", targetBucket);
      postFile(fileName, file, params, listener, responder, errorListener);
   }

   /**
    *
    * @param fileName - S3 key.  make sure that has a UUID
    * @param file - actual file to post
    * @param params
    * @param listener - progress
    * @param responder - when it's done
    * @param errorListener - error listener
    */
   public void postFile(final String fileName, final File file, final HashMap<String, String> params, final ProgressListener listener, final Response.Listener<String> responder, final Response.ErrorListener errorListener) {

      ThreadWrapper.executeInWorkerThread(new Runnable() {
         public void run() {

            try {
               MLog.i("test", "about to send file: " + file.getName() + " fileName: " + fileName);
               multipartUtility = new MultipartUtility(URL, params);
               multipartUtility.setTransferListener(listener);
               multipartUtility.addFilePart(fileName, file);
               final List<String> responses = multipartUtility.finish();
               String response = new JSONObject().toString();
               for (final String r : responses) {
                  MLog.i(FileUploader.class.getSimpleName(), "response: " + r);
                  response = r;
                  break; //TODO consider more response lines later
               }
               MLog.i(FileUploader.class.getSimpleName(), "uploaded file to servlet. file: " + file.getName() + " fileName: " + fileName);

               if (responder == null || multipartUtility.isCancelled()) {
                  return;
               }
               final String finalResponse = response;

               ThreadWrapper.executeInUiThread(new Runnable() {
                  @Override
                  public void run() {
                     responder.onResponse(finalResponse);
                  }
               });


            } catch (final Exception e) {

               if (errorListener == null || multipartUtility.isCancelled()) {
                  e.printStackTrace();
                  return;
               }
               ThreadWrapper.executeInUiThread(new Runnable() {
                  @Override
                  public void run() {
                     errorListener.onErrorResponse(new VolleyError(e));
                  }
               });

            }
         }
      });

   }

}
