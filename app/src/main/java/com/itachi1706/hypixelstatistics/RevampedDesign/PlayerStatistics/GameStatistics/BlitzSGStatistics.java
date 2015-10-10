package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class BlitzSGStatistics {

    /**
     * BSG (Hunger Games)
     * displayed: aura, chosen_taunt, blood, chosen_victorydance, coins, deaths, kills, wins
     * soon: class levels
     * @param obj Statistics
     */
    public static ArrayList<PlayerInfoStatistics> parseHG(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>Blitz Survival Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new PlayerInfoStatistics("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new PlayerInfoStatistics("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("blood"))
            descArray.add(new PlayerInfoStatistics("Blood Enabled", obj.get("blood").getAsString()));
        if (obj.has("aura"))
            descArray.add(new PlayerInfoStatistics("Chosen Aura", obj.get("aura").getAsString()));
        if (obj.has("chosen_taunt"))
            descArray.add(new PlayerInfoStatistics("Chosen Taunt", obj.get("chosen_taunt").getAsString()));
        if (obj.has("chosen_victorydance"))
            descArray.add(new PlayerInfoStatistics("Chosen Victory Dance", obj.get("chosen_victorydance").getAsString()));
        return descArray;
    }
}
