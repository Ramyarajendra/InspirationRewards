package com.ramya.inspirationrewards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class RewardsPref {
    private static final String TAG = "RewardsPref";
    private SharedPreferences prefs;
    private Editor edit;

    public RewardsPref(Activity activity) {
        super();
        Log.d(TAG, "RewardsPref: Constructor");
        prefs = activity.getSharedPreferences(activity.getString(R.string.prefFile), Context.MODE_PRIVATE);
        edit = prefs.edit();
    }

    public void save(String key, String txt) {
        Log.d(TAG, "save: " + key + ":" + txt);
        edit.putString(key, txt);
        edit.apply(); // commit T/F
    }
    public void saveBool(String key, Boolean text) {
        Log.d(TAG, "save: " + key + ":" + text);
        edit.putBoolean(key, text);
        edit.apply(); // commit T/F
    }
    public String getValue(String key) {
        String text = prefs.getString(key, "");
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }
    public Boolean getBoolValue(String key) {
        Boolean text = prefs.getBoolean(key, false);
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }
}
