package com.itachi1706.hypixelstatistics.util.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util.PlayerStatistics.GameStatistics
 */
public class MegaWallsStatistics {

    /**
     * Walls 3 Game
     * chosen_class, coins, deaths, kills, finalDeaths, finalKills, wins, losses
     * individual/weekly statistics soon
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseWalls3(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Walls 3</b>", null, false, true));
        if (obj.has("chosen_class"))
            descArray.add(new ResultDescription("Class Selected", obj.get("chosen_class").getAsString()));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));

        //Overall
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Total Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        // Overall Kill Death Ratio (Kills/Deaths)
        if (obj.has("kills") && obj.has("deaths")){
            int w3Kills = obj.get("kills").getAsInt();
            int w3Deaths = obj.get("deaths").getAsInt();
            if (w3Deaths == 0)
                w3Deaths = 1;  //Done to prevent Divide by Zero Exception
            double w3KDA = (double) w3Kills / w3Deaths;
            w3KDA = (double) Math.round(w3KDA * 100.00) / 100.00;
            descArray.add(new ResultDescription("K/D Ratio", w3KDA + ""));
        }
        if (obj.has("finalDeaths"))
            descArray.add(new ResultDescription("Total Final Deaths", obj.get("finalDeaths").getAsString()));
        if (obj.has("finalKills"))
            descArray.add(new ResultDescription("Total Final Kills", obj.get("finalKills").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Total Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            descArray.add(new ResultDescription("Total Games Lost", obj.get("losses").getAsString()));

        //Herobrine
        descArray = parseIndividualMW(obj, "Herobrine", descArray);
        //Skeleton
        descArray = parseIndividualMW(obj, "Skeleton", descArray);
        //Zombie
        descArray = parseIndividualMW(obj, "Zombie", descArray);
        //Creeper
        descArray = parseIndividualMW(obj, "Creeper", descArray);
        //Enderman
        descArray = parseIndividualMW(obj, "Enderman", descArray);
        //Spider
        descArray = parseIndividualMW(obj, "Spider", descArray);
        //Dreadlord
        descArray = parseIndividualMW(obj, "Dreadlord", descArray);
        //Shaman
        descArray = parseIndividualMW(obj, "Shaman", descArray);
        //Arcanist
        descArray = parseIndividualMW(obj, "Arcanist", descArray);
        //Golem
        descArray = parseIndividualMW(obj, "Golem", descArray);
        //Blaze
        descArray = parseIndividualMW(obj, "Blaze", descArray);
        //Pigman
        descArray = parseIndividualMW(obj, "Pigman", descArray);
        return descArray;
    }

    private static ArrayList<ResultDescription> parseIndividualMW(JsonObject obj, String className, ArrayList<ResultDescription> descArray){
        ArrayList<ResultDescription> classArray = new ArrayList<>();
        if (obj.has("deaths_" + className))
            classArray.add(new ResultDescription("Deaths", obj.get("deaths_" + className).getAsString()));
        if (obj.has("kills_" + className))
            classArray.add(new ResultDescription("Kills", obj.get("kills_" + className).getAsString()));
        if (obj.has("finalDeaths_" + className))
            classArray.add(new ResultDescription("Final Deaths", obj.get("finalDeaths_" + className).getAsString()));
        if (obj.has("finalKills_" + className))
            classArray.add(new ResultDescription("Final Kills", obj.get("finalKills_" + className).getAsString()));
        if (obj.has("wins_" + className))
            classArray.add(new ResultDescription("Games Won", obj.get("wins_" + className).getAsString()));
        if (obj.has("losses_" + className))
            classArray.add(new ResultDescription("Games Lost", obj.get("losses_" + className).getAsString()));
        if (classArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : classArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription(className + " Statistics", "Click here to view " + className + " Statistics", true, msg.toString()));
            //} else {
            //    descArray.add(new ResultDescription(className + " Statistics", "Click here to view " + className + " Statistics", true, "This player does not have any statistics for this class yet!"));
        }
        return descArray;
    }
}
