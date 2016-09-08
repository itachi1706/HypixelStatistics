package com.itachi1706.hypixelstatistics.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck.GetIfDeveloperInfo;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.Objects.GuildMemberDesc;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RecyclerViewAdapters.BoosterRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
@SuppressWarnings("ConstantConditions")
public class MainStaticVars {
    public static final String API_BASE_URL = "http://api.itachi1706.com/api/hypixel.php";

    /**
     * Booster Variables
     * boosterUpdated - Finished Updating Booster
     * inProg - Still getting Player Booster Names
     * parseRes - Parsing Booster Results
     * isBriefBooster - Brief Booster
     * isUsingDetailedActiveBooster - Whether Detailed Active Boosters are being used or not
     */
    public static ArrayList<BoosterDescription> boosterList = new ArrayList<>();
    public static HashMap<String, BoosterDescription> boosterHashMap = new HashMap<>();
    public static ArrayList<BoosterDescription> unfilteredBoosterList = new ArrayList<>();
    public static boolean boosterUpdated = false, inProg = false, parseRes = false;
    public static String boosterJsonString;
    public static boolean isBriefBooster = false;
    public static int numOfBoosters = 0, tmpBooster = 0, boosterProcessCounter = 0, boosterMaxProcessCounter = 0;
    public static BoosterRecyclerAdapter boosterRecyclerAdapter;

    //Settings
    public static String apikey = null;
    public static boolean isStaff = false, isCreator = false;
    public static final String DATE_FORMAT = "dd-MMM-yyyy hh:mm a zz";

    //Server Info
    public static int playerCount = 0, maxPlayerCount = 0;
    public static String serverMOTD = "Retrieving MOTD...";

    public static final ArrayList<GuildMemberDesc> guildList = new ArrayList<>();

    //Parkour Numbers
    public static final int SERVER_PARKOUR_COUNT = 13;

    public static int HTTP_QUERY_TIMEOUT = 60000; //60 seconds timeout
    //Basically how long to try and query
    //Will throw ConnectTimeoutException (setConnectionTimeout) and SocketTimeoutException (setSoTimeout)

    public static HashMap<String, String> guild_member_session_data = new HashMap<>();

    public static HashMap<String, String> friends_session_data = new HashMap<>();

    public static HashMap<String, String> friends_last_online_data = new HashMap<>();

    public static HashMap<String, String> guild_last_online_data = new HashMap<>();

    public static final String BASE_SERVER_URL = "http://api.itachi1706.com/api/appupdatechecker.php?action=androidretrievedata&packagename=";

    public static void updateAPIKey(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        apikey = prefs.getString("api-key",null);
        isStaff = !prefs.getString("rank", "LEL").equals("LEL");
        new GetIfDeveloperInfo().execute(prefs.getString("api-key", "lel"));
        //Log.d("API KEY", "New API Key: " + apikey);
    }

    public static String updateURLWithApiKeyIfExists(String url){
        String newurl = url;
        if (apikey != null){
            newurl += "&key=" + apikey;
        }

        return newurl;
    }

    public static boolean checkIfYouGotJsonString(String jsonString){
        return !jsonString.startsWith("<!DOCTYPE html>");
    }

    public static int getTheme(Activity activity, boolean hasActionBar){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String themeSel = prefs.getString("appThemePref", "Default");
        switch (themeSel){
            case "Default": return hasActionBar ? R.style.AppTheme : R.style.AppTheme_NoActionBar;
            case "AppTheme": return hasActionBar ? R.style.AppTheme : R.style.AppTheme_NoActionBar;
            case "NewTheme": return hasActionBar ? R.style.NewTheme : R.style.NewTheme_NoActionBar;
        }
        return R.style.AppTheme;
    }

    public static int getStatusAndNavBarColor(Activity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String themeSel = prefs.getString("appThemePref", "Default");
        switch (themeSel){
            case "Default": return R.color.blue_700;
            case "AppTheme": return R.color.blue_700;
            case "NewTheme": return R.color.yellow_900;
        }
        return R.color.blue_700;
    }

    public static int getActionBarColor(Activity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String themeSel = prefs.getString("appThemePref", "Default");
        switch (themeSel){
            case "Default": return R.color.blue_500;
            case "AppTheme": return R.color.blue_500;
            case "NewTheme": return R.color.yellow_700;
        }
        return R.color.blue_500;
    }

    @SuppressWarnings("unused")
    public static int getToneColor(Activity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String themeSel = prefs.getString("appThemePref", "Default");
        switch (themeSel){
            case "Default": return R.color.red_a200;
            case "AppTheme": return R.color.red_a200;
            case "NewTheme": return R.color.deep_orange_a200;
        }
        return R.color.red_a200;
    }

    public static void setLayoutAccordingToPrefs(Activity activity){
        setLayoutAccordingToPrefs(activity, true);
    }

    public static void setLayoutAccordingToPrefs(Activity activity, boolean hasActionBar){
        activity.setTheme(getTheme(activity, hasActionBar));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, getStatusAndNavBarColor(activity)));
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, getStatusAndNavBarColor(activity)));
        }
    }

    public static void updateTimeout(Activity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        try {
            HTTP_QUERY_TIMEOUT = Integer.parseInt(prefs.getString("httpTimeout", "60000"));
        } catch (ClassCastException e){
            HTTP_QUERY_TIMEOUT = 60000;
        }

    }

    public static void backupBooster(){
        unfilteredBoosterList = new ArrayList<>();
        for (BoosterDescription desc : boosterList){
            unfilteredBoosterList.add(desc.clone());
        }
    }

    public static void restoreBooster(){
        boosterList = new ArrayList<>();
        for (BoosterDescription desc : unfilteredBoosterList){
            boosterList.add(desc.clone());
        }
    }

    public static boolean addBoosterObject(BoosterDescription object){
        if (!boosterHashMap.containsKey(object.get_purchaseruuid())) {
            boosterHashMap.put(object.get_purchaseruuid(), object);
            boosterRecyclerAdapter.addBooster(object);
            return true;
        }
        return false;
    }

    public static void updateBoosterList() {
        boosterList = new ArrayList<>(boosterHashMap.values());
    }
}
