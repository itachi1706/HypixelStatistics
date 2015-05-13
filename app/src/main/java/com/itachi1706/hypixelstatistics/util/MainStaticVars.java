package com.itachi1706.hypixelstatistics.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.FriendsListAdapter;
import com.itachi1706.hypixelstatistics.util.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.Objects.FriendsObject;
import com.itachi1706.hypixelstatistics.util.Objects.GuildMemberDesc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
@SuppressWarnings("ConstantConditions")
public class MainStaticVars {
    public static final String API_BASE_URL = "https://api.hypixel.net/";

    //Booster
    //boosterUpdated - Finished Updating Booster
    //inProg - Still getting player booster names
    //parseRes - Parsing Results
    //isBriefBooster - Brief Booster
    //isUsingDetailedActiveBooster - Whether Detailed Active Boosters are being used or not
    public static ArrayList<BoosterDescription> boosterList = new ArrayList<>();
    public static ArrayList<BoosterDescription> unfilteredBoosterList = new ArrayList<>();
    public static boolean boosterUpdated = false, inProg = false, parseRes = false;
    public static String boosterJsonString;
    public static boolean isBriefBooster = false, isUsingDetailedActiveBooster = false;
    public static int numOfBoosters = 0, tmpBooster = 0, boosterProcessCounter = 0, boosterMaxProcessCounter = 0;
    public static BoosterDescListAdapter boosterListAdapter;

    //Settings
    public static String apikey = null;
    public static boolean isStaff = false, isCreator = false;

    //Player Stats
    public static String knownAliases = "";

    //Server Info
    public static int playerCount = 0, maxPlayerCount = 0;
    public static String serverMOTD = "Retrieving MOTD...";

    public static final ArrayList<GuildMemberDesc> guildList = new ArrayList<>();

    public static final ArrayList<FriendsObject> friendsList = new ArrayList<>();
    public static int friendsListSize = 0;
    public static String friendOwner = "";
    public static FriendsListAdapter friendsListAdapter;

    //Parkour Numbers
    public static final int SERVER_PARKOUR_COUNT = 13;

    public static int HTTP_QUERY_TIMEOUT = 60000; //60 seconds timeout
    //Basically how long to try and query
    //Will throw ConnectTimeoutException (setConnectionTimeout) and SocketTimeoutException (setSoTimeout)

    public static HashMap<String, String> guild_member_session_data = new HashMap<>();

    public static HashMap<String, String> friends_session_data = new HashMap<>();

    public static HashMap<String, String> friends_last_online_data = new HashMap<>();

    public static HashMap<String, String> guild_last_online_data = new HashMap<>();

    public static void updateAPIKey(Context context){
        String defaultkey = context.getResources().getString(R.string.hypixel_api_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        apikey = prefs.getString("api-key",defaultkey);
        isStaff = !prefs.getString("rank", "LEL").equals("LEL");
        isCreator = prefs.getString("api-key", "topkek").equals(defaultkey);
        //Log.d("API KEY", "New API Key: " + apikey);
}

    public static void resetKnownAliases(){
        knownAliases = "";
    }

    public static void updateBriefBoosterPref(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        isUsingDetailedActiveBooster = prefs.getBoolean("detailed_active_boosters", false);
    }

    public static String getChangelogStringFromArrayList(ArrayList<String> changelog){
         /* Legend of Stuff
        1st Line - Current Version Number
        2nd Line - Link to New Version
        # - Changelog Version Number (Bold this)
        * - Points
        @ - Break Line
         */
        StringBuilder changelogBuilder = new StringBuilder();
        changelogBuilder.append("New Version: ").append(changelog.get(0)).append("<br/><br/>");
        for (String line : changelog){
            if (line.startsWith("#"))
                changelogBuilder.append("<b>").append(line.replace('#', ' ')).append("</b><br />");
            else if (line.startsWith("*"))
                changelogBuilder.append(" - ").append(line.replace('*', ' ')).append("<br />");
            else if (line.startsWith("@"))
                changelogBuilder.append("<br />");
        }
        return changelogBuilder.toString();
    }

    public static boolean checkIfYouGotJsonString(String jsonString){
        return !jsonString.startsWith("<!DOCTYPE html>");
    }

    public static int getTheme(Activity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String themeSel = prefs.getString("appThemePref", "Default");
        switch (themeSel){
            case "Default": return R.style.AppTheme;
            case "AppTheme": return R.style.AppTheme;
            case "NewTheme": return R.style.NewTheme;
        }
        return R.style.AppTheme;
    }

    public static int getStatusBarColor(Activity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String themeSel = prefs.getString("appThemePref", "Default");
        switch (themeSel){
            case "Default": return R.color.blue_700;
            case "AppTheme": return R.color.blue_700;
            case "NewTheme": return R.color.yellow_900;
        }
        return R.color.blue_700;
    }

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
}
