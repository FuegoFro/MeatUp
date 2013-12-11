package com.cs160.fall13.MeatUp;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServerAccess {
//    private static final String SERVER_ADDRESS = "http://10.142.7.133:5000";
    private static final String SERVER_ADDRESS = "http://192.168.1.90:5000";
    private static final String UPDATE_PATH = "/update";

    public static String getUpdateFromServer(String identifier) {
        try {
            String url = SERVER_ADDRESS + UPDATE_PATH + "?identifier=" + identifier;
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                return out.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            Log.e("RendezfoodServerInterface", "Unable to poll server");
            return null;
        }
    }
}
