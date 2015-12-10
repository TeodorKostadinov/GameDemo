package com.example.fos.gamedemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fos on 8.12.2015 г..
 */
public class PrefsUtils {
    private static final String SP_USER = "com.example.fos.gamedemo.utils.sp_user";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static SharedPreferences getUserPrefs(Context context) {
        return context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
    }
}
