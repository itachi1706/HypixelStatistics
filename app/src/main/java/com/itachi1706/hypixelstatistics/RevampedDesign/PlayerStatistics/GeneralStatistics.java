package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.util.GameTypeCapsReturn;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.util.GameType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
public class GeneralStatistics {

    //Parsing General Information
    /*
    rank, displayname, uuid, packageRank, disguise, eulaCoins, gadget, karma, firstLogin, lastLogin, timePlaying, networkExp,
    networkLevel, mostRecentlyThanked, mostRecentlyTipped, thanksSent, tipsSent, channel, chat, tournamentTokens,
    vanityTokens, mostRecentGameType, seeRequest, tipsReceived, thanksReceived, achievementsOneTime
     */
    public static ArrayList<PlayerInfoStatistics> parseGeneral(PlayerReply reply, String localPlayerName){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        if (reply.getPlayer().has("rank"))
            descArray.add(new PlayerInfoStatistics("Rank", reply.getPlayer().get("rank").getAsString()));
        else
            descArray.add(new PlayerInfoStatistics("Rank", "NORMAL"));
        descArray.add(new PlayerInfoStatistics("Name", localPlayerName));
        descArray.add(new PlayerInfoStatistics("UUID",reply.getPlayer().get("uuid").getAsString()));
        //Donor Rank (packageRank and newPackageRank)
        if (reply.getPlayer().has("newPackageRank")){
            //Post-EULA Donator
            descArray.add(new PlayerInfoStatistics("Donor Rank", reply.getPlayer().get("newPackageRank").getAsString()));
            if (reply.getPlayer().has("packageRank"))
                //Pre-EULA donator that upgraded rank post-EULA
                descArray.add(new PlayerInfoStatistics(MinecraftColorCodes.parseColors("Legacy Donor Rank §6(Pre-EULA)§r"), reply.getPlayer().get("packageRank").getAsString()));
        } else {
            //Pre-EULA Donator and Non-Donator
            if (reply.getPlayer().has("packageRank"))
                descArray.add(new PlayerInfoStatistics("Donor Rank", reply.getPlayer().get("packageRank").getAsString()));
        }
        if (reply.getPlayer().has("disguise"))
            descArray.add(new PlayerInfoStatistics("Disguise",reply.getPlayer().get("disguise").getAsString()));
        if (reply.getPlayer().has("eulaCoins"))
            descArray.add(new PlayerInfoStatistics(MinecraftColorCodes.parseColors("Veteran Donor §6(Pre-EULA)§r"), "true"));
        if (reply.getPlayer().has("gadget"))
            descArray.add(new PlayerInfoStatistics("Lobby Gadget",reply.getPlayer().get("gadget").getAsString()));
        if (reply.getPlayer().has("karma"))
            descArray.add(new PlayerInfoStatistics("Karma",reply.getPlayer().get("karma").getAsString()));
        if (reply.getPlayer().has("firstLogin"))
            descArray.add(new PlayerInfoStatistics("First Login",new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz", Locale.US).format(new Date(reply.getPlayer().get("firstLogin").getAsLong()))));
        if (reply.getPlayer().has("lastLogin"))
            descArray.add(new PlayerInfoStatistics("Last Login",new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz", Locale.US).format(new Date(reply.getPlayer().get("lastLogin").getAsLong()))));
        if (reply.getPlayer().has("timePlaying"))
            descArray.add(new PlayerInfoStatistics("Time Played (From 16 May 2014) ",MinecraftColorCodes.parseColors(parseTimeOnline(reply.getPlayer().get("timePlaying").getAsLong()))));
        if (reply.getPlayer().has("networkExp"))
            descArray.add(new PlayerInfoStatistics("Network XP",reply.getPlayer().get("networkExp").getAsString()));
        if (reply.getPlayer().has("networkLevel"))
            descArray.add(new PlayerInfoStatistics("Network Level",reply.getPlayer().get("networkLevel").getAsString()));
        else
            descArray.add(new PlayerInfoStatistics("Network Level" , "1"));
        if (reply.getPlayer().has("mostRecentlyThanked"))
            descArray.add(new PlayerInfoStatistics("Last Thanked (Legacy)",reply.getPlayer().get("mostRecentlyThanked").getAsString()));
        if (reply.getPlayer().has("mostRecentlyTipped"))
            descArray.add(new PlayerInfoStatistics("Last Tipped (Legacy)",reply.getPlayer().get("mostRecentlyTipped").getAsString()));
        if (reply.getPlayer().has("mostRecentlyThankedUuid"))
            descArray.add(new PlayerInfoStatistics("Last Thanked",reply.getPlayer().get("mostRecentlyThankedUuid").getAsString(), "=+=senduuid=+= " + reply.getPlayer().get("mostRecentlyThankedUuid").getAsString()));
        if (reply.getPlayer().has("mostRecentlyTippedUuid"))
            descArray.add(new PlayerInfoStatistics("Last Tipped",reply.getPlayer().get("mostRecentlyTippedUuid").getAsString(), "=+=senduuid=+= " + reply.getPlayer().get("mostRecentlyTippedUuid").getAsString()));
        if (reply.getPlayer().has("thanksSent"))
            descArray.add(new PlayerInfoStatistics("No of Thanks sent",reply.getPlayer().get("thanksSent").getAsString()));
        if (reply.getPlayer().has("tipsSent"))
            descArray.add(new PlayerInfoStatistics("No of Tips sent",reply.getPlayer().get("tipsSent").getAsString()));
        if (reply.getPlayer().has("thanksReceived"))
            descArray.add(new PlayerInfoStatistics("No of Thanks received",reply.getPlayer().get("thanksReceived").getAsString()));
        if (reply.getPlayer().has("tipsReceived"))
            descArray.add(new PlayerInfoStatistics("No of Tips sent received",reply.getPlayer().get("tipsReceived").getAsString()));
        if (reply.getPlayer().has("channel"))
            descArray.add(new PlayerInfoStatistics("Current Chat Channel",reply.getPlayer().get("channel").getAsString()));
        else
            descArray.add(new PlayerInfoStatistics("Current Chat Channel", "ALL"));
        if (reply.getPlayer().has("chat")) {
            if (reply.getPlayer().get("chat").getAsBoolean())
                descArray.add(new PlayerInfoStatistics("Chat Enabled", "Enabled"));
            else
                descArray.add(new PlayerInfoStatistics("Chat Enabled", "Disabled"));
        } else
            descArray.add(new PlayerInfoStatistics("Chat Enabled", "Enabled"));
        if (reply.getPlayer().has("tournamentTokens"))
            descArray.add(new PlayerInfoStatistics("Tournament Tokens",reply.getPlayer().get("tournamentTokens").getAsString()));
        else
            descArray.add(new PlayerInfoStatistics("Tournament Tokens", "0"));
        if (reply.getPlayer().has("vanityTokens"))
            descArray.add(new PlayerInfoStatistics("Vanity Tokens",reply.getPlayer().get("vanityTokens").getAsString()));
        else
            descArray.add(new PlayerInfoStatistics("Vanity Tokens", "0 "));
        if (reply.getPlayer().has("mostRecentGameType")) {
            GameType recentGameType = GameTypeCapsReturn.fromDatabase(reply.getPlayer().get("mostRecentGameType").getAsString());
            if (recentGameType == null) {
                descArray.add(new PlayerInfoStatistics("Last Game Played", reply.getPlayer().get("mostRecentGameType").getAsString()));
            } else {
                descArray.add(new PlayerInfoStatistics("Last Game Played", recentGameType.getName()));
            }
        }
        if (reply.getPlayer().has("seeRequests")) {
            if (reply.getPlayer().get("seeRequests").getAsBoolean())
                descArray.add(new PlayerInfoStatistics("Friend Requests", "Enabled"));
            else
                descArray.add(new PlayerInfoStatistics("Friend Requests", "Disabled"));
        } else
            descArray.add(new PlayerInfoStatistics("Friend Requests", "Enabled"));
        if (reply.getPlayer().has("achievementsOneTime"))
            descArray.add(new PlayerInfoStatistics("No of 1-time Achievements Done", reply.getPlayer().getAsJsonArray("achievementsOneTime").size() + ""));
        if (reply.getPlayer().has("knownAliases")){
            JsonArray arr = reply.getPlayer().getAsJsonArray("knownAliases");
            StringBuilder listOfAliases = new StringBuilder();
            for (JsonElement e : arr){
                listOfAliases.append(e.getAsString()).append("\n");
            }
            descArray.add(new PlayerInfoStatistics("Known Aliases", listOfAliases.toString().replace("\n", " ")));
        }
        return descArray;
    }

    private static String parseTimeOnline(long time){
        //Get Days if there is
        if (time > 44640){
            //Theres Months (M, D, H, M)
            return String.format("%d Months, %d Days, %d Hours, %d Minutes", TimeUnit.MINUTES.toDays(time) / 31, TimeUnit.MINUTES.toDays(time) % 31 , TimeUnit.MINUTES.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MINUTES.toDays(time)), time - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(time)));
        }
        if (time > 1440){
            //Theres Days (D, H, M)
            return String.format("%d Days, %d Hours, %d Minutes", TimeUnit.MINUTES.toDays(time), TimeUnit.MINUTES.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MINUTES.toDays(time)), time - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(time)));
        }
        if (time > 60){
            //Theres Hours (H, M)
            return String.format("%d Hours, %d Minutes", TimeUnit.MINUTES.toHours(time) ,time - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(time)));
        }
        //Minutes only (M)
        return time + " Minutes";
    }
}
