package com.example.cpapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fetcher {
    public static final String TAG="fetcher";
    public static final String userId="atharva_sarage";
    public static final String API_KEY="";
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
        out.close();
        return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public JSONArray fetchItems(){
        JSONArray userJsonArray=null;
        try{
            String url = Uri.parse("https://codeforces.com/api/user.status")
                    .buildUpon()
                    .appendQueryParameter("handle",userId)
                    .appendQueryParameter("from","1")
                    .appendQueryParameter("count","10000")
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            userJsonArray = jsonBody.getJSONArray("result");
            System.out.println("????");
            Log.i(TAG,"Recieved Json: "+jsonString+"\n");
        } catch (IOException | JSONException ioe){
            Log.e(TAG,"failed to fetch",ioe);
        }
        Log.i(TAG,userJsonArray.toString());
        return userJsonArray;
    }

}
