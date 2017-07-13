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

import com.gamerscave.acrabackend.utils.Saver;

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
                    Toast.makeText(this, "All data is in place", Toast.LENGTH_LONG).show();
                    Log.e("DEBUG", "USERNAME " + username.getText().toString() + "\n"
                            + password.getText().toString() + "\n"
                            + server_address.getText().toString() + "\n"
                            + directory.getText().toString() + "\n"
                            + deletescript.getText().toString() + "\n"
                            + listscript.getText().toString() + "\n"
                            + corescript.getText().toString() + "\n"
                            + persistent.isChecked() + "\n"
                            + allowbg.isChecked() + "\n"
                            + sync.isChecked() + "\n");
                    String script = server_address.getText().toString() + "/" + directory.getText().toString() + "/checkpass.php";
                    boolean correctPass = check(script, username.getText().toString(), password.getText().toString());
                    if(!correctPass){
                        return;
                    }
                    Settings.save(this, server_address.getText().toString(),
                            directory.getText().toString(),
                            corescript.getText().toString(),
                            listscript.getText().toString(),
                            deletescript.getText().toString(),
                            username.getText().toString(),
                            password.getText().toString(),
                            sync.isChecked(), allowbg.isChecked(), persistent.isChecked());
                    Intent i = new Intent(Setup.this, ErrorListActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(this, "Missing data", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    
    public boolean check(String url, String username, String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SCRIPTLINK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            boolean retval = Boolean.parseBoolean(response);
                            if(retval){
                                return true;
                            }else{
                                Toast.makeText(Setup.this, "Incorrect credentials", Toast.LENGTH_LONG).show();
                                return false;   
                            }
                        }catch(Exception e){
                            //We have text. Ignore the exception, it is only failure to parse the boolean.
                            //It isn't a boolean, so...
                        }
                        //...we end up here instead of returning at an earlier point. We show a toast
                        //to tell what is the issue (returned from the script):
                        Toast.makeText(Setup.this, response, Toast.LENGTH_LONG).show();
                        //And then return false to ensure the scrupt understands it failed
                        return false;
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
                params.put("username", username);
                params.put("password", password);
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
    
}
