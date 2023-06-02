package com.example.infinitetalk.utilties;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefrenceManager {
    public final SharedPreferences sharedPreferences;

    public PrefrenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCENAME, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putSrting(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getSrting(String key) {
      return sharedPreferences.getString(key , null);
    }

    public void  clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
