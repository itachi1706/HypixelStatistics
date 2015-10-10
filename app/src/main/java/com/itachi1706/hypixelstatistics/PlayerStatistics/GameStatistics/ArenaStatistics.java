package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
public class ArenaStatistics {

    /**
     * Arena Game
     * displayed: active_rune, chest_opens, coins, coins_spent, keys, magical_chest, rating,
     * support, ultimate, utility, offensive,
     * damage_2v2, deaths_2v2, games_2v2, healed_2v2, kills_2v2, losses_2v2, wins_2v2, win_streaks_2v2
     * damage_4v4, deaths_4v4, games_4v4, healed_4v4, kills_4v4, losses_4v4, wins_4v4, win_streaks_4v4
     * damage_ffa, deaths_ffa, games_ffa, healed_ffa, kills_ffa, losses_ffa, wins_ffa, win_streaks_ffa
     * @param obj Statistics
     */
    public static ArrayList<ResultDescription> parseArena(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Arena Brawl</b>", null, false, true));
        if (obj.has("rating"))
            descArray.add(new ResultDescription("Arena Rating", obj.get("rating").getAsString()));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("coins_spent"))
            descArray.add(new ResultDescription("Total Coins Spent", obj.get("coins_spent").getAsString()));
        if (obj.has("active_rune"))
            descArray.add(new ResultDescription("Active Rune", obj.get("active_rune").getAsString()));
        if (obj.has("chest_opens"))
            descArray.add(new ResultDescription("Total Chest Opened", obj.get("chest_opens").getAsString()));
        if (obj.has("keys"))
            descArray.add(new ResultDescription("Keys", obj.get("keys").getAsString()));
        if (obj.has("magical_chest"))
            descArray.add(new ResultDescription("Total No of times Magical Chest Opened", obj.get("magical_chest").getAsString()));

        ResultDescription equip = equipmentStatistics(obj);
        ResultDescription twoVtwo = twoVsTwoStatistics(obj);
        ResultDescription fourVfour = fourVsFourStatistics(obj);
        ResultDescription ffaStat = FreeForAllStatistics(obj);
        if (equip != null)
            descArray.add(equip);
        if (twoVtwo != null)
            descArray.add(twoVtwo);
        if (fourVfour != null)
            descArray.add(fourVfour);
        if (ffaStat != null)
            descArray.add(ffaStat);

        return descArray;
    }

    private static ResultDescription equipmentStatistics(JsonObject obj){
        ArrayList<ResultDescription> eqArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Equips</i>", null, false, true));
        if (obj.has("offensive"))
            eqArray.add(new ResultDescription("Offensive", obj.get("offensive").getAsString()));
        if (obj.has("support"))
            eqArray.add(new ResultDescription("Support", obj.get("support").getAsString()));
        if (obj.has("utility"))
            eqArray.add(new ResultDescription("Utility", obj.get("utility").getAsString()));
        if (obj.has("ultimate"))
            eqArray.add(new ResultDescription("Ultimate", obj.get("ultimate").getAsString()));
        if (eqArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : eqArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Equips", "Click here to view Arena Equips", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription twoVsTwoStatistics(JsonObject obj){
        ArrayList<ResultDescription> twoArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>2v2</i>", null, false, true));
        if (obj.has("damage_2v2"))
            twoArray.add(new ResultDescription("Total Damage Dealt", obj.get("damage_2v2").getAsString()));
        if (obj.has("deaths_2v2"))
            twoArray.add(new ResultDescription("Deaths", obj.get("deaths_2v2").getAsString()));
        if (obj.has("games_2v2"))
            twoArray.add(new ResultDescription("Games Played", obj.get("games_2v2").getAsString()));
        if (obj.has("healed_2v2"))
            twoArray.add(new ResultDescription("Total Health Healed", obj.get("healed_2v2").getAsString()));
        if (obj.has("kills_2v2"))
            twoArray.add(new ResultDescription("Kills", obj.get("kills_2v2").getAsString()));
        if (obj.has("losses_2v2"))
            twoArray.add(new ResultDescription("Games Lost", obj.get("losses_2v2").getAsString()));
        if (obj.has("wins_2v2"))
            twoArray.add(new ResultDescription("Games Won", obj.get("wins_2v2").getAsString()));
        if (obj.has("win_streaks_2v2"))
            twoArray.add(new ResultDescription("Longest Win Streak", obj.get("win_streaks_2v2").getAsString()));
        if (twoArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : twoArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("2v2", "Click here to view statistics from 2v2 Arena", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription fourVsFourStatistics(JsonObject obj){
        ArrayList<ResultDescription> fourArr = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>4v4</i>", null, false, true));
        if (obj.has("damage_4v4"))
            fourArr.add(new ResultDescription("Total Damage Dealt", obj.get("damage_4v4").getAsString()));
        if (obj.has("deaths_4v4"))
            fourArr.add(new ResultDescription("Deaths", obj.get("deaths_4v4").getAsString()));
        if (obj.has("games_4v4"))
            fourArr.add(new ResultDescription("Games Played", obj.get("games_4v4").getAsString()));
        if (obj.has("healed_4v4"))
            fourArr.add(new ResultDescription("Total Health Healed", obj.get("healed_4v4").getAsString()));
        if (obj.has("kills_4v4"))
            fourArr.add(new ResultDescription("Kills", obj.get("kills_4v4").getAsString()));
        if (obj.has("losses_4v4"))
            fourArr.add(new ResultDescription("Games Lost", obj.get("losses_4v4").getAsString()));
        if (obj.has("wins_4v4"))
            fourArr.add(new ResultDescription("Games Won", obj.get("wins_4v4").getAsString()));
        if (obj.has("win_streaks_4v4"))
            fourArr.add(new ResultDescription("Longest Win Streak", obj.get("win_streaks_4v4").getAsString()));
        if (fourArr.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : fourArr){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("4v4", "Click here to view statistics from 4v4 Arena", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription FreeForAllStatistics(JsonObject obj){
        ArrayList<ResultDescription> ffaArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Free For All</i>", null, false, true));
        if (obj.has("damage_ffa"))
            ffaArray.add(new ResultDescription("Total Damage Dealt", obj.get("damage_ffa").getAsString()));
        if (obj.has("deaths_ffa"))
            ffaArray.add(new ResultDescription("Deaths", obj.get("deaths_ffa").getAsString()));
        if (obj.has("games_ffa"))
            ffaArray.add(new ResultDescription("Games Played", obj.get("games_ffa").getAsString()));
        if (obj.has("healed_ffa"))
            ffaArray.add(new ResultDescription("Total Health Healed", obj.get("healed_ffa").getAsString()));
        if (obj.has("kills_ffa"))
            ffaArray.add(new ResultDescription("Kills", obj.get("kills_ffa").getAsString()));
        if (obj.has("losses_ffa"))
            ffaArray.add(new ResultDescription("Games Lost", obj.get("losses_ffa").getAsString()));
        if (obj.has("wins_ffa"))
            ffaArray.add(new ResultDescription("Games Won", obj.get("wins_ffa").getAsString()));
        if (obj.has("win_streaks_ffa"))
            ffaArray.add(new ResultDescription("Longest Win Streak", obj.get("win_streaks_ffa").getAsString()));
        if (ffaArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : ffaArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Free For All (FFA)", "Click here to view statistics from FFA Arena", true, msg.toString());
        }
        return null;
    }
}
