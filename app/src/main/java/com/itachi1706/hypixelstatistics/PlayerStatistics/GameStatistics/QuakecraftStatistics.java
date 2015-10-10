package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class QuakecraftStatistics {

    /**
     * Quakecraft Game
     * displayed: coins, deaths, kills, killstreaks, wins
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseQuake(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>QuakeCraft</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("killstreaks"))
            descArray.add(new ResultDescription("Longest Killstreak", obj.get("killstreaks").getAsString()));
        return descArray;
    }
}
