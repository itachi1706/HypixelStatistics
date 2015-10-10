package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.util.ArrayList;

/**
 * Created by Kenneth on 10/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics
 */
public class StatisticsHelper {

    /**
     * Generates the Dialog Statistics String from a List of PlayerInfoStatistics Objects
     * Each Statistics String will be generated with each line being
     * "{title}: {message}<br/>"
     * @param arrayList The List of objects to generate dialog string from
     * @return The generated dialog string in HTML format
     * (Not that you should call {@see #Html.fromHtml(String) fromHtml} to print this out)
     */
    public static String generateDialogStatisticsString(ArrayList<PlayerInfoStatistics> arrayList){
        StringBuilder msg = new StringBuilder();
        for (PlayerInfoStatistics t : arrayList){
            msg.append(t.getTitle()).append(": ").append(t.getMessage()).append("<br />");
        }
        return msg.toString();
    }

    /**
     * This object generates a list of items from a JsonArray object
     * @param obj the JsonObject to retrive the array from
     * @param jsonKey the key in which the array is keyed to
     * @return A string for the items concat together
     */
    public static String generatePackagesStatistics(JsonObject obj, String jsonKey){
        StringBuilder packageBuilder = new StringBuilder();
        JsonArray packages = obj.get(jsonKey).getAsJsonArray();
        boolean firstPack = true;
        for (JsonElement e : packages){
            if (firstPack){
                firstPack = false;
                packageBuilder.append(e.getAsString());
            }
            else {
                packageBuilder.append(",").append(e.getAsString());
            }
        }

        return packageBuilder.toString();
    }

    /**
     * Parses and colors the items in each of the player statistics
     * @param message The message to color
     * @return Formatted Message
     */
    public static String parseColorInPlayerStats(String message){
        if (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("enabled")) {
            return MinecraftColorCodes.parseColors("§a" + message + "§r");
        }
        if (message.equalsIgnoreCase("false") || message.equalsIgnoreCase("disabled")) {
            return MinecraftColorCodes.parseColors("§c" + message + "§r");
        }
        if((message.equalsIgnoreCase("null"))){
            return MinecraftColorCodes.parseColors("§c" + "NONE" + "§r");
        }
        return message;
    }
}
