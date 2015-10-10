package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class VampireZStatistics {

    /**
     * VampireZ Statistics
     * coins, human_deaths, human_wins, human_kills, vampire_deaths, vampire_wins, vampire_kills
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseVampZ(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>VampireZ</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("human_deaths"))
            descArray.add(new ResultDescription("Total Deaths (Human)", obj.get("human_deaths").getAsString()));
        if (obj.has("human_wins"))
            descArray.add(new ResultDescription("Total Wins (Human)", obj.get("human_wins").getAsString()));
        if (obj.has("human_kills"))
            descArray.add(new ResultDescription("Total Kills (Human)", obj.get("human_kills").getAsString()));
        if (obj.has("vampire_deaths"))
            descArray.add(new ResultDescription("Total Deaths (Vampire)", obj.get("vampire_deaths").getAsString()));
        if (obj.has("vampire_wins"))
            descArray.add(new ResultDescription("Total Wins (Vampire)", obj.get("vampire_wins").getAsString()));
        if (obj.has("vampire_kills"))
            descArray.add(new ResultDescription("Total Kills (Vampire)", obj.get("vampire_kills").getAsString()));
        return descArray;
    }
}
