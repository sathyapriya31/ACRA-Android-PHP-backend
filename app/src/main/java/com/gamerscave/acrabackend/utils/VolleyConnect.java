package com.gamerscave.acrabackend.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerscave.acrabackend.content.Content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class VolleyConnect {
    public static final String ROOT_DIR = "https://gamers-cave-world.com/crash/";
    public static final String SCRIPTLINK = ROOT_DIR + "android.php";
    public static final String DELETELINK = ROOT_DIR + "androiddelete.php";
    Context c;
    public void connect(final Context c){
        this.c = c;
        Toast.makeText(c, "Initializing", Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SCRIPTLINK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] files = response.split("\n");
                        new NWT().execute(files);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c,error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }


    private class NWT extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... files) {
            Error stb[] = new Error[files.length];
            for(int i = 0; i < files.length; i++){
                Log.e("File", "File: " + files[i]);

                String devices = "", stacktrace = "", lastreported = "", appversion = "", app = "", android = "";

                try {
                    // Create a URL for the desired page
                    URL url = new URL(ROOT_DIR + files[i]);

                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while ((str = in.readLine()) != null) {
                        if(str.contains("PACKAGE_NAME =")){
                            app = str.replace("PACKAGE_NAME = ", "");
                        }else if(str.contains("APP_VERSION_NAME =")){
                            appversion = str.replace("APP_VERSION_NAME = ", "");
                        }else if(str.contains("at ") || str.contains("Exception") || str.contains("exception") ||
                                str.contains("Caused by:") || str.contains("Caused by")){
                            String aug = str.replace("LOGCAT = ", "");
                            aug = aug.replace("{0-9}-{0-9} {0-9}:{0-9}:{0-9}.{0-9} {A-Z}/{A-Z} ({0-9}):", "");
                            stacktrace += aug;
                        }else if(str.contains("PHONE_MODEL =")){
                            devices = str.replace("PHONE_MODEL = ", "");
                        }else if(str.contains("ANDROID_VERSION =")){
                            android = str.replace("ANDROID_VERSION = ", "");
                        }

                    }

                    new Error(c, devices, stacktrace, lastreported, appversion, app, android);
                    in.close();
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
            for(Error e : stb){
                Content.addItem(new Content.Item(e));
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETELINK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(c,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(c);
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}


    }
}
