package com.k2view.cdbms.usercode.common;

import java.util.*;
import java.sql.*;
import java.math.*;
import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.sync.*;
import com.k2view.broadway.model.Actor;
import com.k2view.broadway.model.Data;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import static com.k2view.cdbms.shared.user.UserCode.log;


public class ConvertUrlToBuffer implements Actor {
    public void action(Data input, Data output) throws Exception{
        URL url = new URL(input.string("imageUrl"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method to GET
        connection.setRequestMethod("GET");

        // Connect to the URL
        connection.connect();

        // Check if the response code indicates success
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Read the image content as a byte array
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                output.put("buffer", outputStream.toByteArray());
            }
        } else {
            throw new IOException("Failed to read image from URL. HTTP response code: " + connection.getResponseCode());
        }
    }
	
}

