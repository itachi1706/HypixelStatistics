package com.itachi1706.hypixelstatistics.util.HistoryHandling;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.HistoryObject;

import net.hypixel.api.reply.PlayerReply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kenneth on 19/11/2014, 8:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class CharHistory {

    public static void addHistory(PlayerReply result, SharedPreferences pref) {
        // NEW UUID
        String uid = result.getPlayer().get("uuid").getAsString();

        String playerName = result.getPlayer().get("playername").getAsString();
        String playerMcName = playerName;
        if (MinecraftColorCodes.checkDisplayName(result)) {
             playerMcName = result.getPlayer().get("displayname").getAsString();
        }
        String prefix;
        if (result.getPlayer().has("prefix")) {
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
        long date = System.currentTimeMillis();

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
            obj.put("uuid", uid);
            obj.put("displayname", playerMcName);
            obj.put("playername", playerName);
            obj.put("dateObtained", date);
            array.put(obj);
            main.put("history", array);
            pref.edit().putString("history", main.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getExistingJSONString(SharedPreferences pref) {
        String json = pref.getString("history", null);
        if (json == null) {
            return new JSONArray();
        }
        try {
            JSONObject obj = new JSONObject(json);
            return obj.getJSONArray("history");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void updateJSONString(SharedPreferences pref, JsonArray array){
        String newJsonStr = "{\"history\":" + array.toString() + "}";
        pref.edit().putString("history", newJsonStr).apply();
        Log.d("HISTORY SAVED", pref.getString("history", "null"));
    }

    public static boolean checkHistoryExpired(com.google.gson.JsonObject obj){
        //Check if user is null
        if (!obj.has("uuid"))
            return true;
        // 30 Days in millis 2592000000L
        final long expiryDay = 2592000000L;
        if (!obj.has("dateObtained")){
            //Prev Gen Hist, reobtain
            return true;
        }
        long dateObt = obj.get("dateObtained").getAsLong();
        long currentDate = System.currentTimeMillis();
        //Check if Pass 10 days
        return currentDate - dateObt > expiryDay;
    }

    public static boolean checkLegacyStrings(JsonObject obj){
        return !obj.has("uuid");
    }

    public static void verifyNoLegacy(SharedPreferences pref){
        String tmp = getListOfHistory(pref);
        if (tmp == null)
            return;
        Gson gson = new Gson();
        HistoryObject obj = gson.fromJson(tmp, HistoryObject.class);
        JsonArray histCheck = obj.getHistory();
        for (JsonElement el : histCheck){
            JsonObject histCheckName = el.getAsJsonObject();
            if (checkLegacyStrings(histCheckName)){
                histCheck.remove(histCheckName);
                updateJSONString(pref, histCheck);
                Log.d("HISTORY", "History Expired");
            }
        }
    }

    public static String getListOfHistory(SharedPreferences pref){
        return pref.getString("history", null);
    }
}
