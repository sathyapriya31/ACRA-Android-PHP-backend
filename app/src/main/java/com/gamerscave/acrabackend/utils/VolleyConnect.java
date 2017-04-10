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
import com.gamerscave.acrabackend.Splash;
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
                            /*
                            In our app development process, debug follows some patterns. On critical errors(Or critical debug) we use DEBUG as the tag.
                            IT is added here because this app(in development) used DEBUG to print out stacktraces, which contained ACRA.
                            But it is still a nice security to have for other apps.
                             */
                        }else if(str.contains("E/ACRA") && !str.contains("E/DEBUG") && (str.contains("at") || str.contains("Exception") || str.contains("exception") ||
                                str.contains("Caused by:") || str.contains("Caused by"))){

                            stacktrace += str + "\n";
                        }else if(str.contains("PHONE_MODEL =")){
                            devices = str.replace("PHONE_MODEL = ", "");
                        }else if(str.contains("ANDROID_VERSION =")){
                            android = str.replace("ANDROID_VERSION = ", "");
                        }

                    }

                    final String regex = "[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}";
                    final String sreg = "\\(.*\\):";
                    String aug = stacktrace.replaceAll(regex, "");
                    aug = aug.replaceAll(sreg, "");
                    aug = aug.replaceAll("LOGCAT = ", "");
                    Log.e("DEBUG", aug);
                    stacktrace = aug;

                    Error e = new Error(c, devices, stacktrace, lastreported, appversion, app, android);
                    if(!e.merged) {
                        Content.addItem(new Content.Item(e));
                    }
                    in.close();
                } catch (Exception e) {}
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
