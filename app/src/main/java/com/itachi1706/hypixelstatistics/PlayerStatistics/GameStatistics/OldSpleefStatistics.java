package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class OldSpleefStatistics {

    /**
     * Legacy Spleef Game?
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseSpleef(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Legacy Spleef</b>", null, false, true));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        return descArray;
    }
}
