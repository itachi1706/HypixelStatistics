package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
@Deprecated
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
    public static ArrayList<ResultDescription> parseArcade(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>The Arcade Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("blood"))
            descArray.add(new ResultDescription("Blood Enabled", obj.get("blood").getAsString()));
        if (obj.has("max_wave"))
            descArray.add(new ResultDescription("Creeper Attack Max Wave", obj.get("max_wave").getAsString()));
        if (obj.has("wins_ender"))
            descArray.add(new ResultDescription("Ender Spleef Wins", obj.get("wins_ender").getAsString()));
        if (obj.has("wins_party"))
            descArray.add(new ResultDescription("Party Games Wins", obj.get("wins_party").getAsString()));
        if (obj.has("wins_party_2"))
            descArray.add(new ResultDescription("Party Games 2 Wins", obj.get("wins_party_2").getAsString()));

        ResultDescription bountyHunter = BountyHunter(obj);
        ResultDescription farmHunt = FarmHunt(obj);
        ResultDescription blockingDead = BlockingDead(obj);
        ResultDescription throwout = Throwout(obj);
        ResultDescription dragonWars = DragonWars(obj);
        ResultDescription galaxyWars = GalaxyWars(obj);
        ResultDescription buildBattle = BuildBattle(obj);

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

    private static ResultDescription BountyHunter(JsonObject obj){
        //Bounty Hunter
        ArrayList<ResultDescription> bhArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Bounty Hunter</i>", null, false, true));
        if (obj.has("bounty_head"))
            bhArray.add(new ResultDescription("Bounty Head", obj.get("bounty_head").getAsString()));
        if (obj.has("bounty_kills_oneinthequiver"))
            bhArray.add(new ResultDescription("Total Bounty Kills", obj.get("bounty_kills_oneinthequiver").getAsString()));
        if (obj.has("deaths_oneinthequiver"))
            bhArray.add(new ResultDescription("Deaths", obj.get("deaths_oneinthequiver").getAsString()));
        if (obj.has("kills_oneinthequiver"))
            bhArray.add(new ResultDescription("Kills", obj.get("kills_oneinthequiver").getAsString()));
        if (obj.has("wins_oneinthequiver"))
            bhArray.add(new ResultDescription("Wins", obj.get("wins_oneinthequiver").getAsString()));
        if (bhArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : bhArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Bounty Hunter", "Click here to view statistics from Bounty Hunter", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription FarmHunt(JsonObject obj){
        //Farm Hunt
        ArrayList<ResultDescription> fhArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Farm Hunt</i>", null, false, true));
        if (obj.has("wins_farm_hunt"))
            fhArray.add(new ResultDescription("Wins", obj.get("wins_farm_hunt").getAsString()));
        if (obj.has("poop_collected"))
            fhArray.add(new ResultDescription("Poop Collected", obj.get("poop_collected").getAsString()));
        if (fhArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : fhArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Farm Hunt", "Click here to view statistics from Farm Hunt", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription BlockingDead(JsonObject obj){
        //Blocking Dead
        ArrayList<ResultDescription> tbdArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>The Blocking Dead</i>", null, false, true));
        if (obj.has("headshots_dayone"))
            tbdArray.add(new ResultDescription("Headshots", obj.get("headshots_dayone").getAsString()));
        if (obj.has("kills_dayone"))
            tbdArray.add(new ResultDescription("Kills", obj.get("kills_dayone").getAsString()));
        if (obj.has("wins_dayone"))
            tbdArray.add(new ResultDescription("Wins", obj.get("wins_dayone").getAsString()));
        if (tbdArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tbdArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("The Blocking Dead", "Click here to view statistics from The Blocking Dead", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription Throwout(JsonObject obj){
        //Throwout
        ArrayList<ResultDescription> toArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Throwout</i>", null, false, true));
        if (obj.has("wins_throw_out"))
            toArray.add(new ResultDescription("Wins", obj.get("wins_throw_out").getAsString()));
        if (obj.has("kills_throw_out"))
            toArray.add(new ResultDescription("Kills", obj.get("kills_throw_out").getAsString()));
        if (obj.has("deaths_throw_out"))
            toArray.add(new ResultDescription("Deaths", obj.get("deaths_throw_out").getAsString()));
        if (toArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : toArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Throwout", "Click here to view statistics from Throwout", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription DragonWars(JsonObject obj){
        //Dragon Wars
        ArrayList<ResultDescription> dwArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Dragon Wars</i>", null, false, true));
        if (obj.has("kills_dragonwars2"))
            dwArray.add(new ResultDescription("Kills", obj.get("kills_dragonwars2").getAsString()));
        if (obj.has("wins_dragonwars2"))
            dwArray.add(new ResultDescription("Wins", obj.get("wins_dragonwars2").getAsString()));
        if (dwArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : dwArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Dragon Wars", "Click here to view statistics from Dragon Wars", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription GalaxyWars(JsonObject obj){
        //Galaxy Wars
        ArrayList<ResultDescription> gwArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Dragon Wars</i>", null, false, true));
        if (obj.has("sw_kills"))
            gwArray.add(new ResultDescription("Kills", obj.get("sw_kills").getAsString()));
        if (obj.has("sw_shots_fired"))
            gwArray.add(new ResultDescription("Shots Fired", obj.get("sw_shots_fired").getAsString()));
        if (obj.has("sw_rebel_kills"))
            gwArray.add(new ResultDescription("Rebel Kills", obj.get("sw_rebel_kills").getAsString()));
        if (obj.has("sw_deaths"))
            gwArray.add(new ResultDescription("Deaths", obj.get("sw_deaths").getAsString()));
        if (obj.has("sw_empire_kills"))
            gwArray.add(new ResultDescription("Empire Kills", obj.get("sw_empire_kills").getAsString()));
        if (gwArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : gwArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Galaxy Wars", "Click here to view statistics from Galaxy Wars", true, msg.toString());
        }
        return null;
    }

    private static ResultDescription BuildBattle(JsonObject obj){
        //Build Battle
        ArrayList<ResultDescription> bbArray = new ArrayList<>();
        if (obj.has("wins_buildbattle"))
            bbArray.add(new ResultDescription("Wins (Solo)", obj.get("wins_buildbattle").getAsString()));
        if (obj.has("wins_buildbattle_teams"))
            bbArray.add(new ResultDescription("Wins (Teams)", obj.get("wins_buildbattle_teams").getAsString()));
        if (bbArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : bbArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            return new ResultDescription("Build Battle", "Click here to view statistics from Build Battle", true, msg.toString());
        }
        return null;
    }
}
