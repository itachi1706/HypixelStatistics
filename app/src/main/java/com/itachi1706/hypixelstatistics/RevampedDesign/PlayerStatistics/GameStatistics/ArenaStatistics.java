package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
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
    public static ArrayList<PlayerInfoStatistics> parseArena(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>Arena Brawl</b>", null, false, true));
        if (obj.has("rating"))
            descArray.add(new PlayerInfoStatistics("Arena Rating", obj.get("rating").getAsString()));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("coins_spent"))
            descArray.add(new PlayerInfoStatistics("Total Coins Spent", obj.get("coins_spent").getAsString()));
        if (obj.has("active_rune"))
            descArray.add(new PlayerInfoStatistics("Active Rune", obj.get("active_rune").getAsString()));
        if (obj.has("chest_opens"))
            descArray.add(new PlayerInfoStatistics("Total Chest Opened", obj.get("chest_opens").getAsString()));
        if (obj.has("keys"))
            descArray.add(new PlayerInfoStatistics("Keys", obj.get("keys").getAsString()));
        if (obj.has("magical_chest"))
            descArray.add(new PlayerInfoStatistics("Total No of times Magical Chest Opened", obj.get("magical_chest").getAsString()));

        PlayerInfoStatistics equip = equipmentStatistics(obj);
        PlayerInfoStatistics twoVtwo = twoVsTwoStatistics(obj);
        PlayerInfoStatistics fourVfour = fourVsFourStatistics(obj);
        PlayerInfoStatistics ffaStat = FreeForAllStatistics(obj);
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

    private static PlayerInfoStatistics equipmentStatistics(JsonObject obj){
        ArrayList<PlayerInfoStatistics> eqArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Equips</i>", null, false, true));
        if (obj.has("offensive"))
            eqArray.add(new PlayerInfoStatistics("Offensive", obj.get("offensive").getAsString()));
        if (obj.has("support"))
            eqArray.add(new PlayerInfoStatistics("Support", obj.get("support").getAsString()));
        if (obj.has("utility"))
            eqArray.add(new PlayerInfoStatistics("Utility", obj.get("utility").getAsString()));
        if (obj.has("ultimate"))
            eqArray.add(new PlayerInfoStatistics("Ultimate", obj.get("ultimate").getAsString()));
        if (eqArray.size() > 0){
            return new PlayerInfoStatistics("Equips", "Click here to view Arena Equips", StatisticsHelper.generateDialogStatisticsString(eqArray));
        }
        return null;
    }

    private static PlayerInfoStatistics twoVsTwoStatistics(JsonObject obj){
        ArrayList<PlayerInfoStatistics> twoArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>2v2</i>", null, false, true));
        if (obj.has("damage_2v2"))
            twoArray.add(new PlayerInfoStatistics("Total Damage Dealt", obj.get("damage_2v2").getAsString()));
        if (obj.has("deaths_2v2"))
            twoArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_2v2").getAsString()));
        if (obj.has("games_2v2"))
            twoArray.add(new PlayerInfoStatistics("Games Played", obj.get("games_2v2").getAsString()));
        if (obj.has("healed_2v2"))
            twoArray.add(new PlayerInfoStatistics("Total Health Healed", obj.get("healed_2v2").getAsString()));
        if (obj.has("kills_2v2"))
            twoArray.add(new PlayerInfoStatistics("Kills", obj.get("kills_2v2").getAsString()));
        if (obj.has("losses_2v2"))
            twoArray.add(new PlayerInfoStatistics("Games Lost", obj.get("losses_2v2").getAsString()));
        if (obj.has("wins_2v2"))
            twoArray.add(new PlayerInfoStatistics("Games Won", obj.get("wins_2v2").getAsString()));
        if (obj.has("win_streaks_2v2"))
            twoArray.add(new PlayerInfoStatistics("Longest Win Streak", obj.get("win_streaks_2v2").getAsString()));
        if (twoArray.size() > 0){
            return new PlayerInfoStatistics("2v2", "Click here to view statistics from 2v2 Arena", StatisticsHelper.generateDialogStatisticsString(twoArray));
        }
        return null;
    }

    private static PlayerInfoStatistics fourVsFourStatistics(JsonObject obj){
        ArrayList<PlayerInfoStatistics> fourArr = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>4v4</i>", null, false, true));
        if (obj.has("damage_4v4"))
            fourArr.add(new PlayerInfoStatistics("Total Damage Dealt", obj.get("damage_4v4").getAsString()));
        if (obj.has("deaths_4v4"))
            fourArr.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_4v4").getAsString()));
        if (obj.has("games_4v4"))
            fourArr.add(new PlayerInfoStatistics("Games Played", obj.get("games_4v4").getAsString()));
        if (obj.has("healed_4v4"))
            fourArr.add(new PlayerInfoStatistics("Total Health Healed", obj.get("healed_4v4").getAsString()));
        if (obj.has("kills_4v4"))
            fourArr.add(new PlayerInfoStatistics("Kills", obj.get("kills_4v4").getAsString()));
        if (obj.has("losses_4v4"))
            fourArr.add(new PlayerInfoStatistics("Games Lost", obj.get("losses_4v4").getAsString()));
        if (obj.has("wins_4v4"))
            fourArr.add(new PlayerInfoStatistics("Games Won", obj.get("wins_4v4").getAsString()));
        if (obj.has("win_streaks_4v4"))
            fourArr.add(new PlayerInfoStatistics("Longest Win Streak", obj.get("win_streaks_4v4").getAsString()));
        if (fourArr.size() > 0){
            return new PlayerInfoStatistics("4v4", "Click here to view statistics from 4v4 Arena", StatisticsHelper.generateDialogStatisticsString(fourArr));
        }
        return null;
    }

    private static PlayerInfoStatistics FreeForAllStatistics(JsonObject obj){
        ArrayList<PlayerInfoStatistics> ffaArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Free For All</i>", null, false, true));
        if (obj.has("damage_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Total Damage Dealt", obj.get("damage_ffa").getAsString()));
        if (obj.has("deaths_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_ffa").getAsString()));
        if (obj.has("games_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Games Played", obj.get("games_ffa").getAsString()));
        if (obj.has("healed_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Total Health Healed", obj.get("healed_ffa").getAsString()));
        if (obj.has("kills_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Kills", obj.get("kills_ffa").getAsString()));
        if (obj.has("losses_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Games Lost", obj.get("losses_ffa").getAsString()));
        if (obj.has("wins_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Games Won", obj.get("wins_ffa").getAsString()));
        if (obj.has("win_streaks_ffa"))
            ffaArray.add(new PlayerInfoStatistics("Longest Win Streak", obj.get("win_streaks_ffa").getAsString()));
        if (ffaArray.size() > 0){
            return new PlayerInfoStatistics("Free For All (FFA)", "Click here to view statistics from FFA Arena", StatisticsHelper.generateDialogStatisticsString(ffaArray));
        }
        return null;
    }
}
