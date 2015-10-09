package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class TNTGamesStatistics {

    /**
     * TNT Games (TNT Wizards, Bow Spleef, TNT Run, TNT Tag
     * TNT Wizards - capture, Bow Spleef - bowspleef, TNT Tag - tnttag, TNT Run - tntrun
     * displayed: coins, selected_hat, wins_capture, kills_capture, deaths_capture, deaths_bowspleef, wins_bowspleef, wins_tntag,
     * wins_tntrun
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseTntGames(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>TNT Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("selected_hat"))
            if (obj.get("selected_hat").isJsonNull())
                descArray.add(new ResultDescription("Selected Hat", "null"));
            else
                descArray.add(new ResultDescription("Selected Hat", obj.get("selected_hat").getAsString()));

        ResultDescription tntWizards = TntWizards(obj);
        ResultDescription bowSpleef = BowSpleef(obj);
        ResultDescription tntTagAndRun = TNTTagAndRun(obj);
        if (tntWizards != null)
            descArray.add(tntWizards);
        if (bowSpleef != null)
            descArray.add(bowSpleef);
        if (tntTagAndRun != null)
            descArray.add(tntTagAndRun);
        return descArray;
    }

    private static ResultDescription TntWizards(JsonObject obj){
        ArrayList<ResultDescription> tntWArr = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>TNT Wizards</i>", null, false, true));
        if (obj.has("wins_capture"))
            tntWArr.add(new ResultDescription("Wins", obj.get("wins_capture").getAsString()));
        if (obj.has("kills_capture"))
            tntWArr.add(new ResultDescription("Kills", obj.get("kills_capture").getAsString()));
        if (obj.has("deaths_capture"))
            tntWArr.add(new ResultDescription("Deaths", obj.get("deaths_capture").getAsString()));
        if (tntWArr.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tntWArr) {
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("TNT Wizards", "Click here to view statistics from TNT Wizards", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription BowSpleef(JsonObject obj){
        ArrayList<ResultDescription> tntBSArr = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>TNT Bow Spleef</i>", null, false, true));
        if (obj.has("deaths_bowspleef"))
            tntBSArr.add(new ResultDescription("Deaths", obj.get("deaths_bowspleef").getAsString()));
        if (obj.has("wins_bowspleef"))
            tntBSArr.add(new ResultDescription("Wins", obj.get("wins_bowspleef").getAsString()));
        if (tntBSArr.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tntBSArr) {
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("TNT Bow Spleef", "Click here to view statistics from Bow Spleef", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription TNTTagAndRun(JsonObject obj){
        ArrayList<ResultDescription> tntTRArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>TNT Tag/TNT Run</i>", null, false, true));
        if (obj.has("wins_tntag"))
            tntTRArray.add(new ResultDescription("TNTTag Wins", obj.get("wins_tntag").getAsString()));
        if (obj.has("wins_tntrun"))
            tntTRArray.add(new ResultDescription("TNTRun Wins", obj.get("wins_tntrun").getAsString()));
        if (tntTRArray.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tntTRArray) {
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("TNT Tag/TNT Run", "Click here to view statistics from the 2 games", true, msg.toString());
        }
        return null;
    }
}
