package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class QuakecraftStatistics {

    /**
     * Quakecraft Game
     * displayed: coins, deaths, kills, killstreaks, wins
     * @param obj Statistics
     */
    public static ArrayList<PlayerInfoStatistics> parseQuake(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>QuakeCraft</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new PlayerInfoStatistics("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new PlayerInfoStatistics("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("killstreaks"))
            descArray.add(new PlayerInfoStatistics("Longest Killstreak", obj.get("killstreaks").getAsString()));
        return descArray;
    }
}
