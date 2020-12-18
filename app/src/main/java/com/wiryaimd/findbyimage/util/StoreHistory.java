package com.wiryaimd.findbyimage.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StoreHistory {

    public static final String ARRIMGKEY = "KEY_ARRIMGTWO";

    public static SharedPreferences getSharedPrefences(Activity activity){
        return activity.getPreferences(Context.MODE_PRIVATE);
    }

    public static void saveLink(Activity activity, String link){
        try {
            if (getSavedlink(activity) != null){
                ArrayList<String> arrlink = getSavedlink(activity);
                arrlink.add(link);
                Gson gson = new Gson();
                String strjson = gson.toJson(arrlink);
                SharedPreferences.Editor editor = getSharedPrefences(activity).edit();
                editor.putString(ARRIMGKEY, strjson);
                editor.apply();
                System.out.println("save availab");
            }else{
                ArrayList<String> arrlink = new ArrayList<>();
                arrlink.add(link);
                Gson gson = new Gson();
                String strjson = gson.toJson(arrlink);
                SharedPreferences.Editor editor = getSharedPrefences(activity).edit();
                editor.putString(ARRIMGKEY, strjson);
                editor.apply();
                System.out.println("save first");
            }
        }catch (Exception e){
            System.out.println("failed");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getSavedlink(Activity activity){
        String jsonstr = getSharedPrefences(activity).getString(ARRIMGKEY, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        return gson.fromJson(jsonstr, type);
    }

}
