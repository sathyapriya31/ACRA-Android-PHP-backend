package com.gamerscave.acrabackend.content;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gamerscave.acrabackend.ErrorListActivity;
import com.gamerscave.acrabackend.utils.Error;
import com.gamerscave.acrabackend.utils.SQLSaver;
import com.gamerscave.acrabackend.utils.VolleyConnect;

/**
 * The content. THis is where all the errors will be shown
 */
public class Content {

    public static final List<Item> ITEMS = new ArrayList<>();

    public static final Map<String, Item> ITEM_MAP = new HashMap<>();

    public Content(Context c){
        SQLSaver sql = new SQLSaver(c);
        //First, we execute volley
        VolleyConnect vc = new VolleyConnect();
        vc.connect(c);
        //Then we query the errors. This is so we can get any new errors instantly
        List<Error> errors = sql.getAllErrors(c);
        for(Error e : errors){
            //The hash for the error we are testing
            String ehash = e.getHash();
            boolean found = false;
            for(Item i : ITEMS){
                //The hash for the item we are testing
                String ihash = i.error.hash;
                if(ihash.equals(ehash)){
                    //If the hash is equal, we ignore adding it to the list.
                    //A development issue was found where all errors were re-added to the list
                    //and because there is a background service, the app was never fully reset.
                    //That caused the errors to repeat themselves in the list, 2, 3, 4, 5 times
                    //one per hide -> reopen -> destroy sequence
                    found = true;
                }
            }
            if(!found)
                addItem(new Item(e));
        }
        sql.onDestroy();

    }

    public static void addItem(Item item) {
        if(item.error != null) {
            ITEMS.add(item);
            ITEM_MAP.put(Long.toString(item.error.getId()), item);

        }else{
            Log.e("DEBUG", "Error cannot be null");
        }
    }

    public static void recreateItemMap(){
        ITEM_MAP.clear();
        for(Item item : ITEMS){
            ITEM_MAP.put(Long.toString(item.error.getId()), item);
        }
    }

    public static class Item {
        public Error error;
        public Item(Error error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return error.getStacktrace();
        }
    }
}
