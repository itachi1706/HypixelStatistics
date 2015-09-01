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
public class WallsStatistics {

    /**
     * Walls Game
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseWalls(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Walls</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            descArray.add(new ResultDescription("Games Lost", obj.get("losses").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int waKills = obj.get("kills").getAsInt();
            int waDeaths = obj.get("deaths").getAsInt();
            if (waDeaths == 0)
                waDeaths = 1;  //Done to prevent Divide by Zero Exception
            double waKDA = (double) waKills / waDeaths;
            waKDA = (double) Math.round(waKDA * 100) / 100;
            descArray.add(new ResultDescription("K/D Ratio", waKDA + ""));
        }
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
