package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class TNTGamesStatistics {

    /**
     * TNT Games (TNT Wizards, Bow Spleef, TNT Run, TNT Tag
     * TNT Wizards - capture, Bow Spleef - bowspleef, TNT Tag - tnttag, TNT Run - tntrun
     * displayed: coins, selected_hat, wins_capture, kills_capture, deaths_capture, deaths_bowspleef, wins_bowspleef, wins_tntag,
     * wins_tntrun
     * @param obj Statistics
     */
    public static ArrayList<PlayerInfoStatistics> parseTntGames(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>TNT Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("selected_hat"))
            if (obj.get("selected_hat").isJsonNull())
                descArray.add(new PlayerInfoStatistics("Selected Hat", "null"));
            else
                descArray.add(new PlayerInfoStatistics("Selected Hat", obj.get("selected_hat").getAsString()));

        PlayerInfoStatistics tntWizards = TntWizards(obj);
        PlayerInfoStatistics bowSpleef = BowSpleef(obj);
        PlayerInfoStatistics tntTagAndRun = TNTTagAndRun(obj);
        if (tntWizards != null)
            descArray.add(tntWizards);
        if (bowSpleef != null)
            descArray.add(bowSpleef);
        if (tntTagAndRun != null)
            descArray.add(tntTagAndRun);
        return descArray;
    }

    private static PlayerInfoStatistics TntWizards(JsonObject obj){
        ArrayList<PlayerInfoStatistics> tntWArr = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>TNT Wizards</i>", null, false, true));
        if (obj.has("wins_capture"))
            tntWArr.add(new PlayerInfoStatistics("Wins", obj.get("wins_capture").getAsString()));
        if (obj.has("kills_capture"))
            tntWArr.add(new PlayerInfoStatistics("Kills", obj.get("kills_capture").getAsString()));
        if (obj.has("deaths_capture"))
            tntWArr.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_capture").getAsString()));
        if (tntWArr.size() > 0) {
            return new PlayerInfoStatistics("TNT Wizards", "Click here to view statistics from TNT Wizards", StatisticsHelper.generateDialogStatisticsString(tntWArr));
        }
        return null;
    }

    private static PlayerInfoStatistics BowSpleef(JsonObject obj){
        ArrayList<PlayerInfoStatistics> tntBSArr = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>TNT Bow Spleef</i>", null, false, true));
        if (obj.has("deaths_bowspleef"))
            tntBSArr.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_bowspleef").getAsString()));
        if (obj.has("wins_bowspleef"))
            tntBSArr.add(new PlayerInfoStatistics("Wins", obj.get("wins_bowspleef").getAsString()));
        if (tntBSArr.size() > 0) {
            return new PlayerInfoStatistics("TNT Bow Spleef", "Click here to view statistics from Bow Spleef", StatisticsHelper.generateDialogStatisticsString(tntBSArr));
        }
        return null;
    }

    private static PlayerInfoStatistics TNTTagAndRun(JsonObject obj){
        ArrayList<PlayerInfoStatistics> tntTRArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>TNT Tag/TNT Run</i>", null, false, true));
        if (obj.has("wins_tntag"))
            tntTRArray.add(new PlayerInfoStatistics("TNTTag Wins", obj.get("wins_tntag").getAsString()));
        if (obj.has("wins_tntrun"))
            tntTRArray.add(new PlayerInfoStatistics("TNTRun Wins", obj.get("wins_tntrun").getAsString()));
        if (tntTRArray.size() > 0) {
            return new PlayerInfoStatistics("TNT Tag/TNT Run", "Click here to view statistics from the 2 games", StatisticsHelper.generateDialogStatisticsString(tntTRArray));
        }
        return null;
    }
}
