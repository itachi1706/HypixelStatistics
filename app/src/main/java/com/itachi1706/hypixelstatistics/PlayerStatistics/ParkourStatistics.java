package com.itachi1706.hypixelstatistics.PlayerStatistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.util.GameType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
@Deprecated
public class ParkourStatistics {

    /**
     * Parse Lobby Parkour Staistics
     * @param reply PlayerReply object
     */
    public static ArrayList<ResultDescription> parseParkourCounts(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        JsonObject parkourMain = reply.getPlayer().getAsJsonObject("parkourCompletions");
        int legacyCount = 0, newLobbyParkourCount = 0;
        for (Map.Entry<String, JsonElement> entry : parkourMain.entrySet()){
            ArrayList<ResultDescription> parkArray = new ArrayList<>();
            //Get the count of times its completed
            JsonArray completionArray = entry.getValue().getAsJsonArray();
            int i = 1;
            for (JsonElement e : completionArray){
                JsonObject timings = e.getAsJsonObject();
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz", Locale.US).format(new Date(timings.get("timeStart").getAsLong()));
                int timeDurationWork = timings.get("timeTook").getAsInt();
                String timeDuration = String.format("%d min, %d sec %d millis", TimeUnit.MILLISECONDS.toMinutes(timeDurationWork), TimeUnit.MILLISECONDS.toSeconds(timeDurationWork) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDurationWork)), timeDurationWork - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeDurationWork)));
                parkArray.add(new ResultDescription("Attempt #" + i + ": " + timeDuration + "", "On: " + timeStamp));
                i++;
            }
            if (parkArray.size() > 0){
                StringBuilder msg = new StringBuilder();
                msg.append("Amount of Times Completed: ").append(entry.getValue().getAsJsonArray().size()).append("<br />");
                for (ResultDescription t : parkArray){
                    msg.append(t.get_title()).append("<br />").append(t.get_result()).append("<br />");
                }
                //Check for which parkour it is (if legacy or new)

                //Currently there is 12 parkour (with Warlords coming soon)
                switch(entry.getKey()){
                    case "ArcadeGames":
                        descArray.add(new ResultDescription(GameType.ARCADE.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "Arena":
                        descArray.add(new ResultDescription(GameType.ARENA.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "BlitzLobby":
                        descArray.add(new ResultDescription(GameType.SURVIVAL_GAMES.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "CopsnCrims":
                        descArray.add(new ResultDescription(GameType.MCGO.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "MainLobby":
                        descArray.add(new ResultDescription("Main Lobby Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "MegaWalls":
                        descArray.add(new ResultDescription(GameType.WALLS3.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "Paintball":
                        descArray.add(new ResultDescription(GameType.PAINTBALL.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "QuakeCraft":
                        descArray.add(new ResultDescription(GameType.QUAKECRAFT.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "TNT":
                        descArray.add(new ResultDescription(GameType.TNTGAMES.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "TheWallsLobby":
                        descArray.add(new ResultDescription(GameType.WALLS.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "uhc":
                        descArray.add(new ResultDescription(GameType.UHC.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "vampirez":
                        descArray.add(new ResultDescription(GameType.VAMPIREZ.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "Warlords":
                        descArray.add(new ResultDescription(GameType.BATTLEGROUND.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    default:
                        descArray.add(new ResultDescription(entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + " (Legacy)", "Click here to view parkour statistics", true, msg.toString()));
                        legacyCount++;
                        break;
                }
            }
        }
        //Add to front the count of parkour completions etc
        descArray.add(0, new ResultDescription("Legacy Parkour Completed", legacyCount + ""));
        descArray.add(0, new ResultDescription("Parkour Completed", newLobbyParkourCount + "/" + MainStaticVars.SERVER_PARKOUR_COUNT));
        return descArray;
    }
}
