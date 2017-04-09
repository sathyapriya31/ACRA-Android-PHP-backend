package com.gamerscave.acrabackend.content;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gamerscave.acrabackend.ErrorListActivity;
import com.gamerscave.acrabackend.utils.Error;
import com.gamerscave.acrabackend.utils.SQLSaver;
import com.gamerscave.acrabackend.utils.VolleyConnect;

/**
 * The content. THis is where all the errors will be shown
 */
public class Content {

    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Map<String, Item> ITEM_MAP = new HashMap<>();

    public Content(Context c){
        SQLSaver sql = new SQLSaver(c);
        //First, we execute volley
        VolleyConnect vc = new VolleyConnect();
        vc.connect(c);
        //Then we query the errors. This is so we can get any new errors instantly
        List<Error> errors = sql.getAllErrors(c);
        for(Error e : errors){
            addItem(new Item(e));
        }

    }

    public static void addItem(Item item) {
        if(item.error != null) {
            ITEMS.add(item);
            ITEM_MAP.put(Long.toString(item.error.getId()), item);
        }else{
            Log.e("DEBUG", "Error cannot be null");
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
