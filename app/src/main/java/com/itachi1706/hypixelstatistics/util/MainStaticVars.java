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

    //Booster
    public static ArrayList<BoosterDescription> boosterList = new ArrayList<>();
    public static boolean boosterUpdated = false, inProg = false, parseRes = false;
    public static int numOfBoosters = 0, tmpBooster = 0, boosterProcessCounter = 0, boosterMaxProcessCounter = 0;

    //Settings
    public static String apikey = null;
    public static boolean isStaff = false, isOwner = false;

    //Player Stats
    public static String knownAliases = "";

    //Server Info
    public static int playerCount = 0, maxPlayerCount = 0;
    public static String serverMOTD = "Retrieving MOTD...";

    public static final ArrayList<GuildMemberDesc> guildList = new ArrayList<>();

    public static void updateAPIKey(Context context){
        String defaultkey = context.getResources().getString(R.string.hypixel_api_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        apikey = prefs.getString("api-key",defaultkey);
        isStaff = !prefs.getString("rank", "LEL").equals("LEL");
        isOwner = prefs.getString("api-key", "topkek").equals(defaultkey);
        //Log.d("API KEY", "New API Key: " + apikey);
}

    public static void resetKnownAliases(){
        knownAliases = "";
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
                changelogBuilder.append("<b>").append(line.replace('#',' ')).append("</b><br />");
            else if (line.startsWith("*"))
                changelogBuilder.append(" - ").append(line.replace('*', ' ')).append("<br />");
            else if (line.startsWith("@"))
                changelogBuilder.append("<br />");
        }
        return changelogBuilder.toString();
    }
}
