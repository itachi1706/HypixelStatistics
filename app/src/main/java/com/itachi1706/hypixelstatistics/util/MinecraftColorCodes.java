package com.itachi1706.hypixelstatistics.util;

import com.google.gson.JsonObject;

import net.hypixel.api.reply.PlayerReply;

/**
 * Created by Kenneth on 11/11/2014, 2:11 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class MinecraftColorCodes {

    /**
     * Parse a string to HTML formatted String with colour
     * (Note: If you have colour, you need to reset it with §r)
     * @param in the string to be formatted
     * @return the formatted HTMML string
     */
    public static String parseColors(String in){
        String out = in;
        out = out.replace("§0","<font color='#000000'>");   //Black
        out = out.replace("§1","<font color='#0000AA'>");   //Dark Blue
        out = out.replace("§2","<font color='#00AA00'>");   //Dark Green
        out = out.replace("§3","<font color='#00AAAA'>");   //Dark Aqua
        out = out.replace("§4","<font color='#AA0000'>");   //Dark Red
        out = out.replace("§5","<font color='#AA00AA'>");   //Dark Purple
        out = out.replace("§6","<font color='#FFAA00'>");   //Gold
        out = out.replace("§7","<font color='#AAAAAA'>");   //Gray
        out = out.replace("§8","<font color='#555555'>");   //Dark Gray
        out = out.replace("§9","<font color='#5555FF'>");   //Blue
        out = out.replace("§a","<font color='#55FF55'>");   //Green
        out = out.replace("§b","<font color='#55FFFF'>");   //Aqua
        out = out.replace("§c","<font color='#FF5555'>");   //Red
        out = out.replace("§d","<font color='#FF55FF'>");   //Light Purple
        out = out.replace("§e","<font color='#FFFF55'>");   //Yellow
        out = out.replace("§f","<font color='#FFFFFF'>");   //White
        out = out.replace("§r","</font>");                  //Close
        return out;
    }

    /**
     * Formats Name and Rank of a Hypixel Player
     * @param name Name and Rank of player on Hypixel
     * @return formatted Name/Rank
     */
    public static String parseHypixelRanks(PlayerReply name){
        //Check for special rank
        if (!checkDisplayName(name)){
            return parseColors("§f" + name.getPlayer().get("playername").getAsString() + "§r");
        }
        String special = null;
        if (name.getPlayer().has("prefix")){
            special = newSpecialRanks(name.getPlayer().get("displayname").getAsString(), name.getPlayer().get("prefix").getAsString());
        }
        if (special != null){
            return special;
        }
        if (name.getPlayer().has("rank")){
            String rank = rank(name.getPlayer().get("displayname").getAsString(), name.getPlayer().get("rank").getAsString());
            if (rank != null){
                return rank;
            }
        }
        if (name.getPlayer().has("newPackageRank")){
            return newPackageRank(name.getPlayer().get("displayname").getAsString(), name.getPlayer().get("newPackageRank").getAsString());
        } else if (name.getPlayer().has("packageRank")){
            return packageRank(name.getPlayer().get("displayname").getAsString(), name.getPlayer().get("packageRank").getAsString());
        } else {
            //Normal
            return parseColors("§7" + name.getPlayer().get("displayname").getAsString() + "§r");
        }
        //return "Error parsing String";
    }

    /**
     * Formats Name and Rank of a Hypixel Player from History
     * @param name Name and Rank of player on Hypixel
     * @return formatted Name/Rank
     */
    public static String parseHistoryHypixelRanks(JsonObject name){
        //Check for special rank
        String special = null;
        if (name.has("prefix")){
            special = newSpecialRanks(name.get("displayname").getAsString(), name.get("prefix").getAsString());
        }
        if (special != null){
            return special;
        }
        if (name.has("rank")){
            String rank = rank(name.get("displayname").getAsString(), name.get("rank").getAsString());
            if (rank != null){
                return rank;
            }
        }
        if (name.has("newPackageRank")){
            return newPackageRank(name.get("displayname").getAsString(), name.get("newPackageRank").getAsString());
        } else if (name.has("packageRank")){
            return packageRank(name.get("displayname").getAsString(), name.get("packageRank").getAsString());
        } else {
            //Normal
            return parseColors("§7" + name.get("displayname").getAsString() + "§r");
        }
        //return "Error parsing String";
    }

    /**
     * Parse New Package Ranks
     * @param playername Player's Display Name
     * @param newPackageRank Player's New Package Rank
     * @return Formatted String
     */
    private static String newPackageRank(String playername, String newPackageRank){
        switch (newPackageRank) {
            case "NONE":
                return parseColors("§7" + playername + "§r");
            case "VIP":
                return parseColors("§a[VIP] " + playername + "§r");
            case "VIP_PLUS":
                return parseColors("§a[VIP§r§6+§r§a] " + playername + "§r");
            case "MVP":
                return parseColors("§b[MVP] " + playername + "§r");
            case "MVP_PLUS":
                return parseColors("§b[MVP§r§c+§r§b] " + playername + "§r");
        }
        return "An error occured (Invalid NewPackageRank)";
    }

    /**
     * Parse Old Package Rank
     * @param playername Player's Display Name
     * @param packageRank Player's Pre EULA Rank
     * @return Formatted String
     */
    private static String packageRank(String playername, String packageRank){
        switch (packageRank) {
            case "NONE":
                return parseColors("§7" + playername + "§r");
            case "VIP":
                return parseColors("§a[VIP] " + playername + "§r");
            case "VIP_PLUS":
                return parseColors("§a[VIP§r§6+§r§a] " + playername + "§r");
            case "MVP":
                return parseColors("§b[MVP] " + playername + "§r");
            case "MVP_PLUS":
                return parseColors("§b[MVP§r§c+§r§b] " + playername + "§r");
        }
        return "An error occured (Invalid packageRank)";
    }

    /**
     * Parse Priviledged Rank
     * @param playername Player's Display Name
     * @param rank Player's Rank
     * @return Formatted String
     */
    private static String rank(String playername, String rank){
        switch (rank){
            case "YOUTUBER": return parseColors("§6[YT] " + playername + "§r");
            case "ADMIN": return parseColors("§c[ADMIN] " + playername + "§r");
            case "HELPER": return parseColors("§9[HELPER] " + playername + "§r");
            case "MODERATOR": return parseColors("§2[MOD] " + playername + "§r");
            case "JR_HELPER": return parseColors("§9[JR HELPER] " + playername + "§r");
        }
        return null;
    }

    /**
     * Parse Special Prefixed Ranks
     * @param displayName Player's Display Name
     * @param prefix Player's Personalized Prefix
     * @return Formatted String
     */
    private static String newSpecialRanks(String displayName, String prefix){
        switch (prefix){
            case "§c[OWNER]": return parseColors("§c[OWNER] " + displayName + "§r");
            case "§c[SLOTH]": return parseColors("§c[SLOTH] " + displayName + "§r");
            case "§c[RETIRED]": return parseColors("§c[RETIRED] " + displayName + "§r");
            case "§c[§aMC§fProHosting§c]": return parseColors("§c[§r§aMC§r§fProHosting§r§c] " + displayName + "§r");
            case "§6[MOJANG]": return parseColors("§6[MOJANG] " + displayName + "§r");
            case "§3[BUILD TEAM]": return parseColors("§3[BUILD TEAM] " + displayName + "§r");
            case "§3[BUILD TEAM§c+§3]": return parseColors("§3[BUILD TEAM§r§c+§r§3] " + displayName + "§r");
            case "§6[APPLE]": return parseColors("§6[APPLE] " + displayName + "§r");
            default: return null;
        }
    }

    /**
     * Check if Player is a staff member
     * @param name PlayerReply Object
     * @return true if player is a staff member
     */
    public static boolean isStaff(PlayerReply name){
        if (name.getPlayer().has("rank")){
            String rank = name.getPlayer().get("rank").getAsString();
            if (rank.equals("ADMIN") || rank.equals("HELPER") || rank.equals("MODERATOR") || rank.equals("JR_HELPER"))
                return true;
        }
        return false;
    }

    /**
     * Check if player object has a display name
     * @param name PlayerReply Object
     * @return true if player has a display name
     */
    public static boolean checkDisplayName(PlayerReply name){
        return name.getPlayer().has("displayname");
    }


}
