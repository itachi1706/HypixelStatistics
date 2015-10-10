package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class PaintballStatistics {

    /**
     * Paintball Game
     * displayed: coins, deaths, wins, kills, killstreaks, shots_fired, hat, packages
     * @param obj Statistics
     */
    public static ArrayList<PlayerInfoStatistics> parsePaintball(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>Paintball</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new PlayerInfoStatistics("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("shots_fired"))
            descArray.add(new PlayerInfoStatistics("Total Shots Fired", obj.get("shots_fired").getAsString()));
        if (obj.has("kills"))
            descArray.add(new PlayerInfoStatistics("Total Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int pbKills = obj.get("kills").getAsInt();
            int pbDeaths = obj.get("deaths").getAsInt();
            if (pbDeaths == 0)
                pbDeaths = 1;  //Done to prevent Divide by Zero Exception
            double pbKDA = (double) pbKills / pbDeaths;
            pbKDA = (double) Math.round(pbKDA * 100) / 100;
            descArray.add(new PlayerInfoStatistics("K/D Ratio", pbKDA + ""));
        }
        if (obj.has("killstreaks"))
            descArray.add(new PlayerInfoStatistics("Longest Killstreak", obj.get("killstreaks").getAsString()));
        if (obj.has("packages")){
            descArray.add(new PlayerInfoStatistics("Packages", StatisticsHelper.generatePackagesStatistics(obj, "packages")));
        }
        return descArray;
    }
}
