package com.sanskruti.volotek.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceManager {

    private static final String PREF_NAME = "festival_pref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    public PreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setBoolean(String PREF_NAME, Boolean val) {
        editor.putBoolean(PREF_NAME, val);
        editor.commit();
    }

    public void setString(String PREF_NAME, String VAL) {
        editor.putString(PREF_NAME, VAL);
        editor.commit();
    }

    public void setInt(String PREF_NAME, int VAL) {
        editor.putInt(PREF_NAME, VAL);
        editor.commit();
    }

    public boolean getBoolean(String PREF_NAME) {
        return pref.getBoolean(PREF_NAME, false);
    }

    public boolean getBoolean(String str, boolean z) {
        return this.pref.getBoolean(str, z);
    }
    public void clearAllDataOnLogout() {

        String apiKey = getString(Constant.api_key);

        editor.clear();
        editor.apply();

        setString(Constant.api_key,apiKey);

    }


    public void remove(String PREF_NAME) {
        if (pref.contains(PREF_NAME)) {
            editor.remove(PREF_NAME);
            editor.commit();
        }
    }

    public String getString(String PREF_NAME) {
        if (pref.contains(PREF_NAME)) {
            return pref.getString(PREF_NAME, null);
        }
        return "";
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);
    }


    public int getInt(String str, int i) {
        return pref.getInt(str, i);
    }
}
