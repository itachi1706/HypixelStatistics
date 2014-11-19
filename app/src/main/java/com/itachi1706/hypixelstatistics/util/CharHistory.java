package com.itachi1706.hypixelstatistics.util;

import android.content.SharedPreferences;

import net.hypixel.api.reply.PlayerReply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kenneth on 19/11/2014, 8:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class CharHistory {

    public static void addHistory(PlayerReply result, SharedPreferences pref){
        String playerName = result.getPlayer().get("playername").getAsString();
        String playerMcName = result.getPlayer().get("displayname").getAsString();
        String prefix;
        if (result.getPlayer().has("prefix")){
            prefix = result.getPlayer().get("prefix").getAsString();
        } else {
            prefix = null;
        }
        String rank;
        if (result.getPlayer().has("rank"))
             rank = result.getPlayer().get("rank").getAsString();
        else
            rank = null;
        String Packagerank;
        if (result.getPlayer().has("packageRank"))
            Packagerank = result.getPlayer().get("packageRank").getAsString();
        else
            Packagerank = null;
        String newPackageRank;
        if (result.getPlayer().has("newPackageRank"))
            newPackageRank = result.getPlayer().get("newPackageRank").getAsString();
        else
            newPackageRank = null;

        JSONArray array = getExistingJSONString(pref);
        JSONObject obj = new JSONObject();
        JSONObject main = new JSONObject();
        try {
            if (prefix != null) {
                obj.put("prefix", prefix);
            }
            if (rank != null) {
                obj.put("rank", rank);
            }
            if (Packagerank != null) {
                obj.put("packageRank", Packagerank);
            }
            if (newPackageRank != null) {
                obj.put("newPackageRank", newPackageRank);
            }
            obj.put("displayname", playerMcName);
            obj.put("playername", playerName);
            array.put(obj);
            main.put("history",array);
            pref.edit().putString("history",main.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getExistingJSONString(SharedPreferences pref){
        String json = pref.getString("history", null);
        if (json == null){
            return new JSONArray();
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("history");
            return arr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static String getListOfHistory(SharedPreferences pref){
        return pref.getString("history", null);
    }
}
