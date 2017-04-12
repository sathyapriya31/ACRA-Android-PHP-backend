package com.gamerscave.acrabackend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gamerscave.acrabackend.utils.Saver;

/**
 * This class handles the Settings. It saves user settings and server password and username.
 * The password and username is used for authentication to prevent abuse and deletion of
 * log files when you didn't request it.
 *
 */
public class Settings {
    /**
     * Show a persistent notification from the service?
     */
    public static boolean SHOW_PERSISTENT = true;
    /**
     * Allow a background service?
     */
    public static boolean RUN_BACKGROUND = true;
    /**
     * Allow a thread updating the app while it is open?
     */
    public static boolean AUTO_REFRESH_WHILE_OPEN = true;
    /**
     * The domain
     */
    public static String DOMAIN = null;
    /**
     * The directory the scripts and logs are stored in
     */
    public static String DIRECTORY = null;
    /**
     * Script name
     */
    public static String DELETE_SCRIPT = "androiddelete.php";
    /**
     * Script name
     */
    public static String MAIN_SCRIPT = "android.php";
    /**
     * Script name
     */
    public static String LIST_FILES = "files.php";
    /**
     * Authentication password
     */
    public static String PASSWORD = null;
    /**
     * Authentication username
     */
    public static String USERNAME = null;

    public Settings(Activity c){
        boolean doesExist = Saver.loadBoolean("settingsinit", c);
        if(!doesExist) {
            c.startActivity(new Intent(c, Setup.class));
            Splash.CREATING_SETTINGS = true;
        }else {
            load(c);
            Splash.CREATING_SETTINGS = false;
        }
    }

    public Settings(Context c){
        load(c);
    }


    public static void load(Context c){
        SHOW_PERSISTENT = Saver.loadBoolean("SHOW_PERSISTENT", c);
        RUN_BACKGROUND = Saver.loadBoolean("RUN_BACKGROUND" , c);
        AUTO_REFRESH_WHILE_OPEN = Saver.loadBoolean("AUTO_REFRESH"   , c);
        DOMAIN = Saver.loadString("DOMAIN", c);
        DIRECTORY = Saver.loadString("DIRECTORY", c);
        MAIN_SCRIPT = Saver.loadString("MAIN_SCRIPT", c);
        LIST_FILES = Saver.loadString("LIST_FILES", c);
        DELETE_SCRIPT = Saver.loadString("DELETE_SCRIPT", c);
        USERNAME = Saver.loadString("USERNAME", c);
        PASSWORD = Saver.loadString("PASSWORD", c);
    }

    public static void save(Context c){
        Saver.save("SHOW_PERSISTENT", Boolean.toString(SHOW_PERSISTENT), c);
        Saver.save("RUN_BACKGROUND" , Boolean.toString(RUN_BACKGROUND), c);
        Saver.save("AUTO_REFRESH"   , Boolean.toString(AUTO_REFRESH_WHILE_OPEN), c);
        Saver.save("DOMAIN", DOMAIN, c);
        Saver.save("DIRECTORY", DIRECTORY, c);
        Saver.save("MAIN_SCRIPT", MAIN_SCRIPT, c);
        Saver.save("LIST_FILES", LIST_FILES, c);
        Saver.save("DELETE_SCRIPT", DELETE_SCRIPT, c);
        Saver.save("USERNAME", USERNAME, c);
        Saver.save("PASSWORD", PASSWORD, c);

    }
    public static void save(Context c, String domain, String dir, String core, String list, String delete, String username, String password,
                            boolean sync, boolean allowbg, boolean persistent){
        DOMAIN = domain;
        DIRECTORY = dir;
        MAIN_SCRIPT = core;
        LIST_FILES = list;
        DELETE_SCRIPT = delete;
        USERNAME = username;
        PASSWORD = password;
        AUTO_REFRESH_WHILE_OPEN = sync;
        RUN_BACKGROUND = allowbg;
        SHOW_PERSISTENT = persistent;
        Saver.save("settingsinit", "true", c);
        save(c);
    }
}
