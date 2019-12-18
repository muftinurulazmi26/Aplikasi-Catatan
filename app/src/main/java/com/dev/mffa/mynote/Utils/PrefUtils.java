package com.dev.mffa.mynote.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("   APP_PREF",Context.MODE_PRIVATE);
    }

    public static void storeApiKey(Context context, String apikey){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("API_KEY",apikey);
        editor.apply();
    }

    public static String getApiKey(Context context){
        return getSharedPreferences(context).getString("API_KEY",null);
    }
}
