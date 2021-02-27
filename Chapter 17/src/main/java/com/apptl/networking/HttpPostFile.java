package com.apptl.networking;

import android.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * @author Erik Hellman
 */
public class HttpPostFile {
    private static final long MAX_FIXED_SIZE = 1024 * 1024 * 5;
    private static final String CRLF = "\r\n";

    public int postFileToURL(File file, String mimeType, URL url)
            throws IOException {
        DataOutputStream requestData = null;
        try {
            long size = file.length();
            String fileName = file.getName();

            // Create a random boundary string
            Random random = new Random();
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);
            String boundary = Base64.
                    encodeToString(randomBytes, Base64.NO_WRAP);

            HttpURLConnection urlConnection
                    = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            // Set the HTTP headers
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            // If larger the MAX_FIXED_SIZE - use chunked streaming
            if (size > MAX_FIXED_SIZE) {
                urlConnection.setChunkedStreamingMode(0);
            } else {
                urlConnection.setFixedLengthStreamingMode((int) size);
            }

            // Open file for reading...
            FileInputStream fileInput = new FileInputStream(file);
            // Open connection to server...
            OutputStream outputStream = urlConnection.getOutputStream();
            requestData = new DataOutputStream(outputStream);

            // Write first boundary for this file
            requestData.writeBytes("--" + boundary + CRLF);
            // Let the server know the filename
            requestData.writeBytes("Content-Disposition: form-data; name=\""
                    + fileName + "\";filename=\"" + fileName + CRLF);
            // ...and the MIME type of the file
            requestData.writeBytes("Content-Type: " + mimeType + CRLF);

            // Read the local file and write to the server in one loop
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                requestData.write(buffer, 0, bytesRead);
            }

            // Write boundary indicating end of this file
            requestData.writeBytes(CRLF);
            requestData.writeBytes("--" + boundary + "--" + CRLF);
            requestData.flush();

            return urlConnection.getResponseCode();
        } finally {
            if (requestData != null) {
                requestData.close();
            }
        }
    }

}
