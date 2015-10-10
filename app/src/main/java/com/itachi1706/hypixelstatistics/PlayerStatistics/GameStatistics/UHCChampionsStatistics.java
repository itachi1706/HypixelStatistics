package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class UHCChampionsStatistics {

    /**
     * UHC (UHC Champions)
     * displayed: coins, score, rank, wins, deaths, kills, heads_eaten, equippedKit
     * @param obj Statistics
     * @return parsed List
     */
    public static ArrayList<ResultDescription> parseUHC(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("score")) {
            descArray.add(new ResultDescription("Score", obj.get("score").getAsInt() + ""));
            descArray.add(new ResultDescription("Ranking", getUHCRank(obj.get("score").getAsInt())));
        } else {
            descArray.add(new ResultDescription("Score", "0"));
            descArray.add(new ResultDescription("Ranking", getUHCRank(0)));
        }
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int uhcKills = obj.get("kills").getAsInt();
            int uhcDeaths = obj.get("deaths").getAsInt();
            if (uhcDeaths == 0)
                uhcDeaths = 1;  //Done to prevent Divide by Zero Exception
            double uhcKDA = (double) uhcKills / uhcDeaths;
            uhcKDA = (double) Math.round(uhcKDA * 100) / 100;
            descArray.add(new ResultDescription("K/D Ratio", uhcKDA + ""));
        }
        if (obj.has("heads_eaten"))
            descArray.add(new ResultDescription("Heads Eaten", obj.get("heads_eaten").getAsString()));
        if (obj.has("equippedKit"))
            descArray.add(new ResultDescription("Equipped Kit", obj.get("equippedKit").getAsString()));
        return descArray;
    }

    private static String getUHCRank(int score){
        if (score > 10210)
            return "Champion";
        if (score > 5210)
            return "Warlord";
        if (score > 2710)
            return "Gladiator";
        if (score > 1710)
            return "Centurion";
        if (score > 960)
            return "Captain";
        if (score > 460)
            return "Knight";
        if (score > 210)
            return "Sergeant";
        if (score > 60)
            return "Soldier";
        if (score > 10)
            return "Initiate";
        return "Recruit";
    }
}
