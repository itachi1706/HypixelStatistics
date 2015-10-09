package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class PaintballStatistics {

    /**
     * Paintball Game
     * displayed: coins, deaths, wins, kills, killstreaks, shots_fired, hat, packages
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parsePaintball(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Paintball</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("shots_fired"))
            descArray.add(new ResultDescription("Total Shots Fired", obj.get("shots_fired").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int pbKills = obj.get("kills").getAsInt();
            int pbDeaths = obj.get("deaths").getAsInt();
            if (pbDeaths == 0)
                pbDeaths = 1;  //Done to prevent Divide by Zero Exception
            double pbKDA = (double) pbKills / pbDeaths;
            pbKDA = (double) Math.round(pbKDA * 100) / 100;
            descArray.add(new ResultDescription("K/D Ratio", pbKDA + ""));
        }
        if (obj.has("killstreaks"))
            descArray.add(new ResultDescription("Longest Killstreak", obj.get("killstreaks").getAsString()));
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
