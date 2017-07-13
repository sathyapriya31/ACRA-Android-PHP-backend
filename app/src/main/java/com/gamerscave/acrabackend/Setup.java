package com.gamerscave.acrabackend;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerscave.acrabackend.utils.Saver;

import java.util.HashMap;
import java.util.Map;

public class Setup extends Activity implements View.OnClickListener {
    EditText username, password, server_address, directory, deletescript, listscript, corescript;
    Button save;
    CheckBox persistent, allowbg, sync;
    @Override
    public void onCreate(Bundle sis){
        super.onCreate(sis);
        setContentView(R.layout.setup_backend);

        boolean exist = Saver.loadBoolean("settingsinit", this);
        save = (Button) findViewById(R.id.save);
        persistent = (CheckBox) findViewById(R.id.persistent);
        allowbg = (CheckBox) findViewById(R.id.allowbg);
        sync = (CheckBox) findViewById(R.id.sync);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        server_address = (EditText) findViewById(R.id.server_address);
        directory = (EditText) findViewById(R.id.directory);
        deletescript = (EditText) findViewById(R.id.delscript);
        listscript = (EditText) findViewById(R.id.filescript);
        corescript = (EditText) findViewById(R.id.core);
        if(exist){
            persistent.setChecked(Settings.SHOW_PERSISTENT);
            allowbg.setChecked(Settings.RUN_BACKGROUND);
            sync.setChecked(Settings.AUTO_REFRESH_WHILE_OPEN);
            username.setText(Settings.USERNAME);
            password.setText(Settings.PASSWORD);
            server_address.setText(Settings.DOMAIN);
            directory.setText(Settings.DIRECTORY);
            deletescript.setText(Settings.DELETE_SCRIPT);
            listscript.setText(Settings.LIST_FILES);
            corescript.setText(Settings.MAIN_SCRIPT);
        }else {
            persistent.setChecked(true);
            allowbg.setChecked(true);
            sync.setChecked(true);
            //This is to set the default setting for these settings.
            //These are very useful and should be on by default. They can
            //be disabled. It is easy to overlook these on setup, so they are automatically
            //set to true to prevent issues on github related to it not working, when it
            //in fact is a missing setting
        }
        save.setOnClickListener(this);
        Splash.CREATING_SETTINGS = false;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.save:
                if(!username.getText().toString().matches("") &&
                        !password.getText().toString().matches("") &&
                        !server_address.getText().toString().matches("") &&
                        !directory.getText().toString().matches("") &&
                        !deletescript.getText().toString().matches("") &&
                        !listscript.getText().toString().matches("") &&
                        !corescript.getText().toString().matches("")){
                    //Toast.makeText(this, "All data is in place", Toast.LENGTH_LONG).show();//Debug call


                    String script = server_address.getText().toString() + directory.getText().toString() + "checkpass.php";
                    check(script, username.getText().toString(), password.getText().toString());

                }else{
                    Toast.makeText(this, "Missing data", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    String response;
    boolean complete = false;
    boolean error = false;
    public void check(final String url, final String username, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            boolean resp = Boolean.parseBoolean(response);
                            if(resp){
                                Toast.makeText(Setup.this, "Everything appears to be in order.", Toast.LENGTH_LONG).show();
                                Settings.save(Setup.this, server_address.getText().toString(),
                                        directory.getText().toString(),
                                        corescript.getText().toString(),
                                        listscript.getText().toString(),
                                        deletescript.getText().toString(),
                                        Setup.this.username.getText().toString(),
                                        Setup.this.password.getText().toString(),
                                        sync.isChecked(), allowbg.isChecked(), persistent.isChecked());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(500);
                                        }catch (Exception e){
                                            //Ignore
                                        }
                                        Intent i = new Intent(Setup.this, ErrorListActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });

                                return;
                            }

                            Toast.makeText(Setup.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                            return;//Avoid the final Toast.makeText that prints out the response
                        }catch (Exception e){
                            //Ignore: This is caught if the response isn't a boolean. Thus, it is text
                            //and we want to treat it differently
                        }

                        Toast.makeText(Setup.this, response, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Setup.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(Setup.this);
        requestQueue.add(stringRequest);

    }

}
