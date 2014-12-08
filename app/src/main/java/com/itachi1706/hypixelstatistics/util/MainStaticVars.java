package com.itachi1706.hypixelstatistics.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itachi1706.hypixelstatistics.R;

import java.util.ArrayList;

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class MainStaticVars {
    public static final String API_BASE_URL = "https://api.hypixel.net/";
    public static ArrayList<BoosterDescription> boosterList = new ArrayList<>();
    public static boolean boosterUpdated = false, inProg = false, parseRes = false;
    public static int numOfBoosters = 0, tmpBooster = 0;
    public static String apikey = null;
    public static boolean isStaff = false;

    public static void updateAPIKey(Context context){
        String defaultkey = context.getResources().getString(R.string.hypixel_api_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        apikey = prefs.getString("api-key",defaultkey);
        isStaff = !prefs.getString("rank", "LEL").equals("LEL");
        //Log.d("API KEY", "New API Key: " + apikey);
    }
}
