package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;

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
    public static ArrayList<PlayerInfoStatistics> parseWalls(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>Walls</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new PlayerInfoStatistics("Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            descArray.add(new PlayerInfoStatistics("Games Lost", obj.get("losses").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new PlayerInfoStatistics("Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int waKills = obj.get("kills").getAsInt();
            int waDeaths = obj.get("deaths").getAsInt();
            if (waDeaths == 0)
                waDeaths = 1;  //Done to prevent Divide by Zero Exception
            double waKDA = (double) waKills / waDeaths;
            waKDA = (double) Math.round(waKDA * 100) / 100;
            descArray.add(new PlayerInfoStatistics("K/D Ratio", waKDA + ""));
        }
        if (obj.has("packages")){
            descArray.add(new PlayerInfoStatistics("Packages", StatisticsHelper.generatePackagesStatistics(obj, "packages")));
        }
        return descArray;
    }
}
