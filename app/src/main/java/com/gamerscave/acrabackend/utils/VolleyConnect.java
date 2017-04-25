package com.gamerscave.acrabackend.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerscave.acrabackend.BuildConfig;
import com.gamerscave.acrabackend.R;
import com.gamerscave.acrabackend.Settings;
import com.gamerscave.acrabackend.Splash;
import com.gamerscave.acrabackend.content.Content;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class VolleyConnect {
    /**
     * Imports details from Settings. IT is a separate variable in here, because we need to make sure it is
     * set, and to merge domain and directory together
     *
     */
    public static final String ROOT_DIR = (Settings.DOMAIN == null ? getRTX() : Settings.DOMAIN + "" + Settings.DIRECTORY + "");
    public static final String SCRIPTLINK = ROOT_DIR + "android.php";
    public static final String DELETELINK = ROOT_DIR + "androiddelete.php";
    Context c;
    public void connect(final Context c){
        this.c = c;
        if(Settings.DOMAIN == null)
            new Settings(c);
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
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",Settings.USERNAME);
                params.put("password",Settings.PASSWORD);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }


    private class NWT extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... files) {

            for(int i = 0; i < files.length; i++){


                String devices = "", stacktrace = "", lastreported = "", appversion = "", app = "", android = "", otherinfo = "Other data:\n";

                try {
                    // Create a URL for the desired page
                    URL url = new URL(ROOT_DIR + files[i]);

                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;

                    while ((str = in.readLine()) != null) {
                        str = str.replaceAll("'", "");//Avoid SQL injection
                        str = str.replaceAll("`", "");//Avoid SQL injection
                        str = str.replaceAll("\"", "");//Avoid SQL injection
                        if(str.contains("PACKAGE_NAME =")){
                            app = str.replace("PACKAGE_NAME = ", "");
                        }else if(str.contains("APP_VERSION_NAME =")){
                            appversion = str.replace("APP_VERSION_NAME = ", "");
                            /*
                            In our app development process, debug follows some patterns. On critical errors(Or critical debug) we use DEBUG as the tag.
                            IT is added here because this app(in development) used DEBUG to print out stacktraces, which contained ACRA.
                            But it is still a nice security to have for other apps.

                            Basically: If the stacktrace contains ACRA, it is reported by acra. If it contains debug, it is printed
                            If it contains "at", "Exception" and similar words, it is a stacktrace

                             */
                        }else if(str.contains("E/ACRA") && !str.contains("E/DEBUG") && (str.contains("at") || str.contains("Exception") || str.contains("exception") ||
                                str.contains("Caused by:") || str.contains("Caused by"))){

                            stacktrace += str + "\n";
                        }else if(str.contains("PHONE_MODEL =")){
                            devices = str.replace("PHONE_MODEL = ", "");
                        }else if(str.contains("ANDROID_VERSION =")){
                            android = str.replace("ANDROID_VERSION = ", "");
                        }else{

                            if(!Pattern.compile("[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}").matcher(str).find() && !str.contains("LOGCAT")
                                    ) {
                                String info = str.replace(" =", ":");
                                otherinfo += info + "\n";
                            }
                        }

                    }

                    final String regex = "[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}";
                    final String sreg = "\\(.*\\):";
                    String aug = stacktrace.replaceAll(regex, "");
                    aug = aug.replaceAll(sreg, "");
                    aug = aug.replaceAll("LOGCAT = ", "");
                    Log.e("DEBUG", aug);
                    stacktrace = aug;

                    Error e = new Error(c, devices, stacktrace, lastreported, appversion, app, android, otherinfo);
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
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username",Settings.USERNAME);
                    params.put("password",Settings.PASSWORD);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };

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
    public static String getRTX(){
        throw new RuntimeException("Domain cannot be null");
    }

}
