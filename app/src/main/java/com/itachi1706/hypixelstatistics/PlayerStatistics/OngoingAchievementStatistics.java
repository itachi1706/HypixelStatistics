package com.itachi1706.hypixelstatistics.PlayerStatistics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.GeneralPlayerStats.LobbyList;
import com.itachi1706.hypixelstatistics.GeneralPlayerStats.OngoingAchievements;
import com.itachi1706.hypixelstatistics.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
public class OngoingAchievementStatistics {

    /**
     * Parse Ongoing Achievements with Achievement Enums
     * @param reply PlayerReply object
     */
    public static ArrayList<PlayerInfoStatistics> parseOngoingAchievements(PlayerReply reply){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        JsonObject achievements = reply.getPlayer().getAsJsonObject("achievements");
        Map<String, JsonElement> tmpMapping = new HashMap<>();
        Map<String, JsonElement> toRemove = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : achievements.entrySet()){
            //Temp transfer to another map
            tmpMapping.put(e.getKey(), e.getValue());
        }

        //Iterate through the tmpMapping based on lobbies
        for (LobbyList list : LobbyList.values()) {
            ArrayList<PlayerInfoStatistics> perLobbyArray = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : tmpMapping.entrySet()) {
                OngoingAchievements achievement = OngoingAchievements.fromDatabase(entry.getKey());
                if (achievement.getAchievementLobbies() == list){
                    if (achievement == OngoingAchievements.UNKNOWN)
                        perLobbyArray.add(new PlayerInfoStatistics(entry.getKey() + "(" + list.getName() + ")", entry.getValue().toString()));
                    else {
                        ArrayList<PlayerInfoStatistics> tmpArray = splitAchievements(achievement, entry.getValue().getAsInt());
                        for (PlayerInfoStatistics d : tmpArray) {
                            perLobbyArray.add(d);
                        }
                        tmpArray.clear();
                    }
                    toRemove.put(entry.getKey(), entry.getValue());
                }
            }
            if (perLobbyArray.size() > 0){
                descArray.add(new PlayerInfoStatistics("<b>" + list.getName() + "</b>", MinecraftColorCodes.GOLD.getHtmlCode() + perLobbyArray.size() + " achievements" + MinecraftColorCodes.CLEAR.getHtmlCode()));
                for (PlayerInfoStatistics d : perLobbyArray)
                    descArray.add(d);
            }
            perLobbyArray.clear();
        }

        //Remove the already displayed stuff from tmpMapping
        for (Map.Entry<String, JsonElement> entry : toRemove.entrySet()){
            tmpMapping.remove(entry.getKey());
        }

        //Remaining ones in Hashmap just also print
        for (Map.Entry<String, JsonElement> entry : tmpMapping.entrySet()){
            OngoingAchievements achievement = OngoingAchievements.fromDatabase(entry.getKey());
            if (achievement == OngoingAchievements.UNKNOWN)
                descArray.add(new PlayerInfoStatistics(entry.getKey() + "(" + LobbyList.UNKNOWN.getName() + ")", entry.getValue().toString()));
            else {
                ArrayList<PlayerInfoStatistics> tmpArray = splitAchievements(achievement, entry.getValue().getAsInt());
                for (PlayerInfoStatistics d : tmpArray) {
                    descArray.add(d);
                }
            }
        }

        tmpMapping.clear();
        toRemove.clear();
        return descArray;
    }

    private static ArrayList<PlayerInfoStatistics> splitAchievements(OngoingAchievements achievement, int achievedValue){
        ArrayList<PlayerInfoStatistics> endResult = new ArrayList<>();
        for (int i = 1; i <= achievement.getMax_tiers(); i++){
            String title = OngoingAchievements.getTitleByTier(achievement, i);
            String description = OngoingAchievements.getDescriptionByTier(achievement, i);
            int tierValue = OngoingAchievements.getTierByTier(achievement, i);
            String progression;
            if (tierValue <= achievedValue){
                //Completed alr (Show Completed)
                progression = MinecraftColorCodes.GREEN.getHtmlCode() + "Completed" + MinecraftColorCodes.CLEAR.getHtmlCode();
            } else {
                //In progress (Show progress)
                progression = MinecraftColorCodes.LIGHT_PURPLE.getHtmlCode() + achievedValue + MinecraftColorCodes.CLEAR.getHtmlCode() +
                        "/" + MinecraftColorCodes.AQUA.getHtmlCode() + tierValue + MinecraftColorCodes.CLEAR.getHtmlCode();
            }
            String compiledSubString = description + "<br />" + progression;
            endResult.add(new PlayerInfoStatistics(title, compiledSubString));
        }
        return endResult;
    }
}
