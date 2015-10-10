package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class ArcadeStatistics {

    /**
     * The Arcade Games
     * displayed: blood, coins
     * (Creeper Attack) max_wave
     * (Ender Spleef) wins_ender
     * (Party Games) wins_party, wins_party_2
     * (Bounty Hunter) bounty_kills_oneinthequiver, bounty_head, deaths_oneinthequiver, kills_oneinthequiver, wins_oneinthequiver
     * (Farm Hunt) wins_farm_hunt, poop_collected
     * (The Blocking Dead) headshots_dayone, kills_dayone, wins_dayone
     * (Throw Out) wins_throw_out, kills_throw_out, deaths_throw_out
     * (Dragon Wars) kills_dragonwars2, wins_dragonwars2
     * (Galaxy Wars) sw_kills, sw_shot_fired, sw_rebel_kills, sw_deaths, sw_empire_kills
     * (Build Battle) wins_buildbattle, wins_buildbattle_teams
     * @param obj Statistics
     */
    public static ArrayList<PlayerInfoStatistics> parseArcade(JsonObject obj){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<b>The Arcade Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("blood"))
            descArray.add(new PlayerInfoStatistics("Blood Enabled", obj.get("blood").getAsString()));
        if (obj.has("max_wave"))
            descArray.add(new PlayerInfoStatistics("Creeper Attack Max Wave", obj.get("max_wave").getAsString()));
        if (obj.has("wins_ender"))
            descArray.add(new PlayerInfoStatistics("Ender Spleef Wins", obj.get("wins_ender").getAsString()));
        if (obj.has("wins_party"))
            descArray.add(new PlayerInfoStatistics("Party Games Wins", obj.get("wins_party").getAsString()));
        if (obj.has("wins_party_2"))
            descArray.add(new PlayerInfoStatistics("Party Games 2 Wins", obj.get("wins_party_2").getAsString()));

        PlayerInfoStatistics bountyHunter = BountyHunter(obj);
        PlayerInfoStatistics farmHunt = FarmHunt(obj);
        PlayerInfoStatistics blockingDead = BlockingDead(obj);
        PlayerInfoStatistics throwout = Throwout(obj);
        PlayerInfoStatistics dragonWars = DragonWars(obj);
        PlayerInfoStatistics galaxyWars = GalaxyWars(obj);
        PlayerInfoStatistics buildBattle = BuildBattle(obj);

        if (bountyHunter != null)
            descArray.add(bountyHunter);
        if (farmHunt != null)
            descArray.add(farmHunt);
        if (blockingDead != null)
            descArray.add(blockingDead);
        if (throwout != null)
            descArray.add(throwout);
        if (dragonWars != null)
            descArray.add(dragonWars);
        if (galaxyWars != null)
            descArray.add(galaxyWars);
        if (buildBattle != null)
            descArray.add(buildBattle);

        return descArray;
    }

    private static PlayerInfoStatistics BountyHunter(JsonObject obj){
        //Bounty Hunter
        ArrayList<PlayerInfoStatistics> bhArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Bounty Hunter</i>", null, false, true));
        if (obj.has("bounty_head"))
            bhArray.add(new PlayerInfoStatistics("Bounty Head", obj.get("bounty_head").getAsString()));
        if (obj.has("bounty_kills_oneinthequiver"))
            bhArray.add(new PlayerInfoStatistics("Total Bounty Kills", obj.get("bounty_kills_oneinthequiver").getAsString()));
        if (obj.has("deaths_oneinthequiver"))
            bhArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_oneinthequiver").getAsString()));
        if (obj.has("kills_oneinthequiver"))
            bhArray.add(new PlayerInfoStatistics("Kills", obj.get("kills_oneinthequiver").getAsString()));
        if (obj.has("wins_oneinthequiver"))
            bhArray.add(new PlayerInfoStatistics("Wins", obj.get("wins_oneinthequiver").getAsString()));
        if (bhArray.size() > 0){
            return new PlayerInfoStatistics("Bounty Hunter", "Click here to view statistics from Bounty Hunter", StatisticsHelper.generateDialogStatisticsString(bhArray));
        }
        return null;
    }

    private static PlayerInfoStatistics FarmHunt(JsonObject obj){
        //Farm Hunt
        ArrayList<PlayerInfoStatistics> fhArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Farm Hunt</i>", null, false, true));
        if (obj.has("wins_farm_hunt"))
            fhArray.add(new PlayerInfoStatistics("Wins", obj.get("wins_farm_hunt").getAsString()));
        if (obj.has("poop_collected"))
            fhArray.add(new PlayerInfoStatistics("Poop Collected", obj.get("poop_collected").getAsString()));
        if (fhArray.size() > 0){
            return new PlayerInfoStatistics("Farm Hunt", "Click here to view statistics from Farm Hunt", StatisticsHelper.generateDialogStatisticsString(fhArray));
        }
        return null;
    }

    private static PlayerInfoStatistics BlockingDead(JsonObject obj){
        //Blocking Dead
        ArrayList<PlayerInfoStatistics> tbdArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>The Blocking Dead</i>", null, false, true));
        if (obj.has("headshots_dayone"))
            tbdArray.add(new PlayerInfoStatistics("Headshots", obj.get("headshots_dayone").getAsString()));
        if (obj.has("kills_dayone"))
            tbdArray.add(new PlayerInfoStatistics("Kills", obj.get("kills_dayone").getAsString()));
        if (obj.has("wins_dayone"))
            tbdArray.add(new PlayerInfoStatistics("Wins", obj.get("wins_dayone").getAsString()));
        if (tbdArray.size() > 0){
            return new PlayerInfoStatistics("The Blocking Dead", "Click here to view statistics from The Blocking Dead", StatisticsHelper.generateDialogStatisticsString(tbdArray));
        }
        return null;
    }

    private static PlayerInfoStatistics Throwout(JsonObject obj){
        //Throwout
        ArrayList<PlayerInfoStatistics> toArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Throwout</i>", null, false, true));
        if (obj.has("wins_throw_out"))
            toArray.add(new PlayerInfoStatistics("Wins", obj.get("wins_throw_out").getAsString()));
        if (obj.has("kills_throw_out"))
            toArray.add(new PlayerInfoStatistics("Kills", obj.get("kills_throw_out").getAsString()));
        if (obj.has("deaths_throw_out"))
            toArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths_throw_out").getAsString()));
        if (toArray.size() > 0){
            return new PlayerInfoStatistics("Throwout", "Click here to view statistics from Throwout", StatisticsHelper.generateDialogStatisticsString(toArray));
        }
        return null;
    }

    private static PlayerInfoStatistics DragonWars(JsonObject obj){
        //Dragon Wars
        ArrayList<PlayerInfoStatistics> dwArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Dragon Wars</i>", null, false, true));
        if (obj.has("kills_dragonwars2"))
            dwArray.add(new PlayerInfoStatistics("Kills", obj.get("kills_dragonwars2").getAsString()));
        if (obj.has("wins_dragonwars2"))
            dwArray.add(new PlayerInfoStatistics("Wins", obj.get("wins_dragonwars2").getAsString()));
        if (dwArray.size() > 0){
            return new PlayerInfoStatistics("Dragon Wars", "Click here to view statistics from Dragon Wars", StatisticsHelper.generateDialogStatisticsString(dwArray));
        }
        return null;
    }

    private static PlayerInfoStatistics GalaxyWars(JsonObject obj){
        //Galaxy Wars
        ArrayList<PlayerInfoStatistics> gwArray = new ArrayList<>();
        //descArray.add(new PlayerInfoStatistics("<i>Dragon Wars</i>", null, false, true));
        if (obj.has("sw_kills"))
            gwArray.add(new PlayerInfoStatistics("Kills", obj.get("sw_kills").getAsString()));
        if (obj.has("sw_shots_fired"))
            gwArray.add(new PlayerInfoStatistics("Shots Fired", obj.get("sw_shots_fired").getAsString()));
        if (obj.has("sw_rebel_kills"))
            gwArray.add(new PlayerInfoStatistics("Rebel Kills", obj.get("sw_rebel_kills").getAsString()));
        if (obj.has("sw_deaths"))
            gwArray.add(new PlayerInfoStatistics("Deaths", obj.get("sw_deaths").getAsString()));
        if (obj.has("sw_empire_kills"))
            gwArray.add(new PlayerInfoStatistics("Empire Kills", obj.get("sw_empire_kills").getAsString()));
        if (gwArray.size() > 0){
            return new PlayerInfoStatistics("Galaxy Wars", "Click here to view statistics from Galaxy Wars", StatisticsHelper.generateDialogStatisticsString(gwArray));
        }
        return null;
    }

    private static PlayerInfoStatistics BuildBattle(JsonObject obj){
        //Build Battle
        ArrayList<PlayerInfoStatistics> bbArray = new ArrayList<>();
        if (obj.has("wins_buildbattle"))
            bbArray.add(new PlayerInfoStatistics("Wins (Solo)", obj.get("wins_buildbattle").getAsString()));
        if (obj.has("wins_buildbattle_teams"))
            bbArray.add(new PlayerInfoStatistics("Wins (Teams)", obj.get("wins_buildbattle_teams").getAsString()));
        if (bbArray.size() > 0){
            return new PlayerInfoStatistics("Build Battle", "Click here to view statistics from Build Battle", StatisticsHelper.generateDialogStatisticsString(bbArray));
        }
        return null;
    }
}
