package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class BlitzSGStatistics {

    /**
     * BSG (Hunger Games)
     * displayed: aura, chosen_taunt, blood, chosen_victorydance, coins, deaths, kills, wins
     * soon: class levels
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseHG(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Blitz Survival Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("blood"))
            descArray.add(new ResultDescription("Blood Enabled", obj.get("blood").getAsString()));
        if (obj.has("aura"))
            descArray.add(new ResultDescription("Chosen Aura", obj.get("aura").getAsString()));
        if (obj.has("chosen_taunt"))
            descArray.add(new ResultDescription("Chosen Taunt", obj.get("chosen_taunt").getAsString()));
        if (obj.has("chosen_victorydance"))
            descArray.add(new ResultDescription("Chosen Victory Dance", obj.get("chosen_victorydance").getAsString()));
        return descArray;
    }
}
