package com.messenger.fade.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.  Also, supports cancel and progress
 * monitoring.
 *
 * @author www.codejava.net -- heavily modified by kkawai by
 * adding 'cancel' and 'progress' features
 */
public class MultipartUtility {

   private static final int CHUNK_SIZE = 4096;

   private final String boundary;
   private static final String LINE_FEED = "\r\n";
   private HttpURLConnection httpConn;
   private final String charset = "UTF-8";
   private OutputStream outputStream;
   private PrintWriter writer;
   private FileUploader.ProgressListener listener;
   private boolean isCancelled;

   /*
    * Purpose of a progressMap is to not post too many
    * progress updates on the UI thread.
    * A 400k file sent in chunks of 4000 will have
    * 1000 progress updates.  Let's not do that.
    */
   private final HashMap<Integer, Integer> progressMap = new HashMap<Integer, Integer>();

   /**
    * This constructor initializes a new HTTP POST request with content type
    * is set to multipart/form-data
    *
    * @param requestURL
    * @param params
    * @throws java.io.IOException
    */
   public MultipartUtility(final String requestURL, final HashMap<String, String> params)
           throws IOException {

      // creates a unique boundary based on time stamp
      boundary = "===" + System.currentTimeMillis() + "===";

      final URL url = new URL(requestURL);
      httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setUseCaches(false);
      httpConn.setDoOutput(true); // indicates POST method
      httpConn.setDoInput(true);
      httpConn.setRequestProperty("Content-Type",
              "multipart/form-data; boundary=" + boundary);
      httpConn.setRequestProperty("User-Agent", "Android");//TODO better agent name
      setRequestParams(httpConn, params);
      outputStream = httpConn.getOutputStream();
      writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
              true);
   }

   public void setTransferListener(final FileUploader.ProgressListener listener) {
      this.listener = listener;
   }

   public void cancel() {
      isCancelled = true;
   }

   public boolean isCancelled() {
      return isCancelled;
   }

   private void setRequestParams(final HttpURLConnection httpConn, final HashMap<String, String> params) {
      if (params == null) return;
      for (final String key : params.keySet()) {
         httpConn.setRequestProperty(key, params.get(key));
      }
   }

   /**
    * Adds a upload file section to the request
    * <p/>
    * fieldName  name attribute in <input type="file" name="..." />
    *
    * @param uploadFile a File to be uploaded
    * @throws java.io.IOException
    */
   public void addFilePart(final String fileName, final File uploadFile)
           throws IOException {
      final String fieldName = "fileUpload";
      writer.append("--" + boundary).append(LINE_FEED);
      writer.append(
              "Content-Disposition: form-data; name=\"" + fieldName
                      + "\"; filename=\"" + fileName + "\"")
              .append(LINE_FEED);
      writer.append(
              "Content-Type: "
                      + URLConnection.guessContentTypeFromName(fileName))
              .append(LINE_FEED);
      writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
      writer.append(LINE_FEED);
      writer.flush();

      final FileInputStream inputStream = new FileInputStream(uploadFile);
      final byte[] buffer = new byte[CHUNK_SIZE];
      int bytesRead = -1;
      int totalBytesRead = 0;
      final int TOTAL_BYTES = (int) uploadFile.length();
      while ((bytesRead = inputStream.read(buffer)) != -1) {

         if (isCancelled) {
            break;
         }

         outputStream.write(buffer, 0, bytesRead);
         totalBytesRead += bytesRead;
         final float progress = ((float) totalBytesRead / TOTAL_BYTES) * 100;
         updateProgress((int) progress);

      }
      outputStream.flush();
      inputStream.close();

      writer.append(LINE_FEED);
      writer.flush();
   }

   private void updateProgress(final int progress) {

      if (listener != null) {

         //%5 increments will limit UI strain
         if (!progressMap.containsKey(progress) && progress % 5 == 0) {
            progressMap.put(progress, 0);
            ThreadWrapper.executeInUiThread(new Runnable() {
               @Override
               public void run() {
                  listener.onProgress(progress);
               }
            });
         }
      }
   }

   /**
    * Completes the request and receives response from the server.
    *
    * @return a list of Strings as response in case the server returned
    * status OK, otherwise an exception is thrown.
    * @throws java.io.IOException
    */
   public List<String> finish() throws IOException {
      final List<String> response = new ArrayList<String>();

      writer.append(LINE_FEED).flush();
      writer.append("--" + boundary + "--").append(LINE_FEED);
      writer.close();

      // checks server's status code first
      final int status = httpConn.getResponseCode();
      if (status == HttpURLConnection.HTTP_OK) {
         final BufferedReader reader = new BufferedReader(new InputStreamReader(
                 httpConn.getInputStream()));
         String line = null;
         while ((line = reader.readLine()) != null) {
            response.add(line);
         }
         reader.close();
         httpConn.disconnect();
      } else {
         throw new IOException("Server returned non-OK status: " + status);
      }

      return response;
   }
}