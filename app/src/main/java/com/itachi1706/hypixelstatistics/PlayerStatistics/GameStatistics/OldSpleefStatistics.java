package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.PlayerInfoStatistics;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class OldSpleefStatistics {

    /**
     * Legacy Spleef Game?
     * @param obj Statistics
     */
    public static ArrayList<PlayerInfoStatistics> parseSpleef(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>Legacy Spleef</b>", null, false, true));
        if (obj.has("wins"))
            descArray.add(new PlayerInfoStatistics("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths").getAsString()));
        return descArray;
    }
}
