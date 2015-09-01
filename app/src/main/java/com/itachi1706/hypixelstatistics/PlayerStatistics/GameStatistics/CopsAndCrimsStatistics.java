package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class CopsAndCrimsStatistics {

    /**
     * MC GO (Cops and Crims)
     * displayed: bombs_defused, bombs_planted, coins, deaths, game_wins, headshot_kills, kills, round_wins,
     * shots_fired, cop_kills, criminal_kills, packages
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseMcGo(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Cops and Crims</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("game_wins"))
            descArray.add(new ResultDescription("Game Wins", obj.get("game_wins").getAsString()));
        if (obj.has("round_wins"))
            descArray.add(new ResultDescription("Round Wins", obj.get("round_wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int mcgoKills = obj.get("kills").getAsInt();
            int mcgoDeaths = obj.get("deaths").getAsInt();
            if (mcgoDeaths == 0)
                mcgoDeaths = 1;  //Done to prevent Divide by Zero Exception
            double mcgoKDA = (double) mcgoKills / mcgoDeaths;
            mcgoKDA = (double) Math.round(mcgoKDA * 100) / 100;
            descArray.add(new ResultDescription("K/D Ratio", mcgoKDA + ""));
        }
        if (obj.has("bombs_defused"))
            descArray.add(new ResultDescription("Bombs Defused", obj.get("bombs_defused").getAsString()));
        if (obj.has("bombs_planted"))
            descArray.add(new ResultDescription("Bombs Planted", obj.get("bombs_planted").getAsString()));
        if (obj.has("shots_fired"))
            descArray.add(new ResultDescription("Total Shots Fired", obj.get("shots_fired").getAsString()));
        if (obj.has("headshot_kills"))
            descArray.add(new ResultDescription("Total Headshot Kills", obj.get("headshot_kills").getAsString()));
        if (obj.has("cop_kills"))
            descArray.add(new ResultDescription("Total Cops Kills", obj.get("cop_kills").getAsString()));
        if (obj.has("criminal_kills"))
            descArray.add(new ResultDescription("Total Criminals Kills", obj.get("criminal_kills").getAsString()));
        if (obj.has("packages")){
            StringBuilder packageBuilder = new StringBuilder();
            JsonArray packages = obj.get("packages").getAsJsonArray();
            boolean firstPack = true;
            for (JsonElement e : packages){
                if (firstPack){
                    firstPack = false;
                    packageBuilder.append(e.getAsString());
                }
                else {
                    packageBuilder.append(",").append(e.getAsString());
                }
            }
            descArray.add(new ResultDescription("Packages", packageBuilder.toString()));
        }
        return descArray;
    }
}
