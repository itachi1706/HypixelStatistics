package com.itachi1706.hypixelstatistics.util.HistoryHandling;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 19/11/2014, 8:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class CharHistory {

    public static void addHistory(PlayerReply result, SharedPreferences pref) {
        String uid = result.getPlayer().get("uuid").getAsString();
        String playerName = result.getPlayer().get("playername").getAsString();
        String playerMcName = MinecraftColorCodes.checkDisplayName(result) ? result.getPlayer().get("displayname").getAsString() : playerName;
        String prefix = result.getPlayer().has("prefix") ? result.getPlayer().get("prefix").getAsString() : null;
        String rank = result.getPlayer().has("rank") ? result.getPlayer().get("rank").getAsString() : null;
        String packagerank = result.getPlayer().has("packageRank") ? result.getPlayer().get("packageRank").getAsString() : null;
        String newPackageRank = result.getPlayer().has("newPackageRank") ? result.getPlayer().get("newPackageRank").getAsString() : null;
        long date = System.currentTimeMillis();

        HistoryArrayObject toAdd = new HistoryArrayObject(rank, packagerank, uid, playerMcName, playerName, newPackageRank, date, prefix);

        HistoryObject existingHistoryObject = getExistingHistory(pref);
        List<HistoryArrayObject> historyItem = existingHistoryObject.hasHistory() ? convertHistoryArrayToList(existingHistoryObject.getHistory()) : new ArrayList<HistoryArrayObject>();
        
        historyItem.add(toAdd);
        existingHistoryObject.setHistory(convertHistoryListToArray(historyItem));
        Gson gson = new Gson();
        String jsonString = gson.toJson(existingHistoryObject);
        pref.edit().putString("history", jsonString).apply();
    }

    private static HistoryObject getExistingHistory(SharedPreferences pref) {
        String json = pref.getString("history", null);
        if (json == null) {
            return new HistoryObject();
        }
        Gson gson = new Gson();
        return gson.fromJson(json, HistoryObject.class);
    }

    public static void updateJSONString(SharedPreferences pref, List<HistoryArrayObject> array){
        HistoryObject obj = new HistoryObject();
        obj.setHistory(convertHistoryListToArray(array));

        Gson gson = new Gson();
        String newJsonStr = gson.toJson(obj);

        pref.edit().putString("history", newJsonStr).apply();
        Log.d("HISTORY SAVED", pref.getString("history", "null"));
    }

    public static boolean checkHistoryExpired(HistoryArrayObject obj){
        //Check if user is null
        if (obj.hasUuid()) return true;
        // 30 Days in millis 2592000000L
        final long expiryDay = 2592000000L;
        if (obj.hasDateObtained()) return true; //Prev Gen Hist, reobtain

        long dateObt = obj.getDateObtained();
        long currentDate = System.currentTimeMillis();
        //Check if Pass 10 days
        return currentDate - dateObt > expiryDay;
    }

    public static boolean checkLegacyStrings(HistoryArrayObject obj){
        return !obj.hasUuid();
    }

    public static void verifyNoLegacy(SharedPreferences pref){
        String tmp = getListOfHistory(pref);
        if (tmp == null)
            return;
        Gson gson = new Gson();
        HistoryObject obj = gson.fromJson(tmp, HistoryObject.class);
        List<HistoryArrayObject> histCheck = convertHistoryArrayToList(obj.getHistory());
        for (HistoryArrayObject el : histCheck){
            if (checkLegacyStrings(el)){
                histCheck.remove(el);
                updateJSONString(pref, histCheck);
                Log.d("HISTORY", "History Expired");
            }
        }
    }

    public static String getListOfHistory(SharedPreferences pref){
        return pref.getString("history", null);
    }

    public static List<HistoryArrayObject> convertHistoryArrayToList(HistoryArrayObject[] history){
        List<HistoryArrayObject> result = new ArrayList<>();
        Collections.addAll(result, history);
        return result;
    }

    public static HistoryArrayObject[] convertHistoryListToArray(List<HistoryArrayObject> historyArrayObjects){
        return historyArrayObjects.toArray(new HistoryArrayObject[historyArrayObjects.size()]);
    }
}
