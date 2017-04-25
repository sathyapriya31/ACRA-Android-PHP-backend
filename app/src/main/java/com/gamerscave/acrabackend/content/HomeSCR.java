package com.gamerscave.acrabackend.content;

import com.gamerscave.acrabackend.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeSCR {
    public String HOMESCREEN_CONTENT;
    private String content = "Welcome to ACRA backend\n" +
            "This is your front page. There are currently %s errors" +
            " stored in your database.";
    private String ofthose = "Of those %s,\n";
    String appsstr = "\n%s are for your app %s. This app stands for %s";
    String appstr_2 = "% of total errors";
    final String fp = "\n\n\nThis backend was designed by Gamers Cave. It requires a server component to work properly." +
            " The server component, and this app's source code, is available on GitHub. The error reports stored here are" +
            " sent by ACRA. The files are then saved to the server in the form of a text file. From the server, this app will" +
            " retrieve the files and save them in a database on your device. This project is, as mentioned, open source. That means" +
            " you can contribute to the project. The repository is at this address: \nhttps://github.com/GamersCave/ACRA-Android-PHP-backend." +
            "\n\nYou are using ACRABackend %s";
    public HomeSCR(int total, List<App> app){
        int apps = app.size();

        HOMESCREEN_CONTENT = String.format(Locale.ENGLISH,
                content,
                Integer.toString(total));
        if(apps > 0){
            content += String.format(Locale.ENGLISH, ofthose, total);
        }
        for(int i = 0; i < apps; i++){
            String pck = app.get(i).pack;
            int count = app.get(i).timesRep;

            double percent = (count / total) * 100;
            HOMESCREEN_CONTENT += String.format(Locale.ENGLISH,
                    appsstr,
                    Integer.toString(count),
                    pck,
                    Double.toString(percent)) + appstr_2;
        }

        HOMESCREEN_CONTENT += String.format(Locale.ENGLISH, fp, BuildConfig.VERSION_NAME);
    }

    public static HomeSCR newInstance(List<Content.Item> items){

        int total = items.size();
        List<App> app = new ArrayList<>();
        for(Content.Item i : items){
            boolean fnd = false;
            String pck = i.error.getApp();
            for(App a : app){
                if(a.pack.equals(pck)){
                    a.timesRep++;
                    fnd = true;
                }
            }
            if(!fnd){
                app.add(new App(pck));
            }
        }
        return new HomeSCR(total, app);
    }
}