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
        String special = specialRanks(name);
        if (special != null){
            return special;
        }
        if (name.getPlayer().has("rank")){
            switch (name.getPlayer().get("rank").getAsString()){
                case "YOUTUBER": return parseColors("§6[YT] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "ADMIN": return parseColors("§c[ADMIN] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "HELPER": return parseColors("§9[HELPER] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MODERATOR": return parseColors("§2[MOD] " + name.getPlayer().get("displayname").getAsString() + "§r");
            }
        }
        if (name.getPlayer().has("packageRank")){
            switch (name.getPlayer().get("packageRank").getAsString()){
                case "NONE": return parseColors("§7" + name.getPlayer().get("displayname").getAsString() + "§r");
                case "VIP": return parseColors("§a[VIP] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "VIP_PLUS": return parseColors("§a[VIP§r§6+§r§a] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MVP": return parseColors("§b[MVP] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MVP_PLUS": return parseColors("§b[MVP§r§c+§r§b] " + name.getPlayer().get("displayname").getAsString() + "§r");
            }
        } else if (name.getPlayer().has("newPackageRank")){
            switch (name.getPlayer().get("newPackageRank").getAsString()) {
                case "NONE":
                    return parseColors("§7" + name.getPlayer().get("displayname").getAsString() + "§r");
                case "VIP":
                    return parseColors("§a[VIP] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "VIP_PLUS":
                    return parseColors("§a[VIP§r§6+§r§a] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MVP":
                    return parseColors("§b[MVP] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MVP_PLUS":
                    return parseColors("§b[MVP§r§c+§r§b] " + name.getPlayer().get("displayname").getAsString() + "§r");
            }
        } else {
            //Normal
            return parseColors("§7" + name.getPlayer().get("displayname").getAsString() + "§r");
        }
        return "Error parsing String";
    }

    /**
     * Formats Name and Rank of a Hypixel Player from History
     * @param name Name and Rank of player on Hypixel
     * @return formatted Name/Rank
     */
    public static String parseHistoryHypixelRanks(JsonObject name){
        //Check for special rank
        String special = specialRanksHistory(name);
        if (special != null){
            return special;
        }
        if (name.has("rank")){
            switch (name.get("rank").getAsString()){
                case "YOUTUBER": return parseColors("§6[YT] " + name.get("displayname").getAsString() + "§r");
                case "ADMIN": return parseColors("§c[ADMIN] " + name.get("displayname").getAsString() + "§r");
                case "HELPER": return parseColors("§9[HELPER] " + name.get("displayname").getAsString() + "§r");
                case "MODERATOR": return parseColors("§2[MOD] " + name.get("displayname").getAsString() + "§r");
            }
        }
        if (name.has("packageRank")){
            switch (name.get("packageRank").getAsString()){
                case "NONE": return parseColors("§7" + name.get("displayname").getAsString() + "§r");
                case "VIP": return parseColors("§a[VIP] " + name.get("displayname").getAsString() + "§r");
                case "VIP_PLUS": return parseColors("§a[VIP§r§6+§r§a] " + name.get("displayname").getAsString() + "§r");
                case "MVP": return parseColors("§b[MVP] " + name.get("displayname").getAsString() + "§r");
                case "MVP_PLUS": return parseColors("§b[MVP§r§c+§r§b] " + name.get("displayname").getAsString() + "§r");
            }
        } else if (name.has("newPackageRank")){
            switch (name.get("newPackageRank").getAsString()) {
                case "NONE":
                    return parseColors("§7" + name.get("displayname").getAsString() + "§r");
                case "VIP":
                    return parseColors("§a[VIP] " + name.get("displayname").getAsString() + "§r");
                case "VIP_PLUS":
                    return parseColors("§a[VIP§r§6+§r§a] " + name.get("displayname").getAsString() + "§r");
                case "MVP":
                    return parseColors("§b[MVP] " + name.get("displayname").getAsString() + "§r");
                case "MVP_PLUS":
                    return parseColors("§b[MVP§r§c+§r§b] " + name.get("displayname").getAsString() + "§r");
            }
        } else {
            //Normal
            return parseColors("§7" + name.get("displayname").getAsString() + "§r");
        }
        return "Error parsing String";
    }

    /**
     * Check for special prefixes from History
     * @param name PlayerReply object
     * @return parsed string if valid, null otherwise
     */
    private static String specialRanksHistory(JsonObject name){
        if (name.has("prefix")) {
            switch (name.get("prefix").getAsString()) {
                case "§c[OWNER]":
                    return parseColors("§c[OWNER] " + name.get("displayname").getAsString() + "§r");
                case "§c[SLOTH]":
                    return parseColors("§c[SLOTH] " + name.get("displayname").getAsString() + "§r");
                case "§c[RETIRED]":
                    return parseColors("§c[RETIRED] " + name.get("displayname").getAsString() + "§r");
                case "§c[§aMC§fProHosting§c]":
                    return parseColors("§c[§r§aMC§r§fProHosting§r§c] " + name.get("displayname").getAsString() + "§r");
                case "§6[MOJANG]":
                    return parseColors("§6[MOJANG] " + name.get("displayname").getAsString() + "§r");
                case "§3[BUILD TEAM]":
                    return parseColors("§3[BUILD TEAM] " + name.get("displayname").getAsString() + "§r");
                case "§3[BUILD TEAM§c+§3]":
                    return parseColors("§3[BUILD TEAM§r§c+§r§3] " + name.get("displayname").getAsString() + "§r");
                case "§6[APPLE]":
                    return parseColors("§6[APPLE] " + name.get("displayname").getAsString() + "§r");
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Check for special prefixes
     * @param name PlayerReply object
     * @return parsed string if valid, null otherwise
     */
    private static String specialRanks(PlayerReply name){
        if (name.getPlayer().has("prefix")){
            switch (name.getPlayer().get("prefix").getAsString()){
                case "§c[OWNER]": return parseColors("§c[OWNER] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§c[SLOTH]": return parseColors("§c[SLOTH] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§c[RETIRED]": return parseColors("§c[RETIRED] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§c[§aMC§fProHosting§c]": return parseColors("§c[§r§aMC§r§fProHosting§r§c] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§6[MOJANG]": return parseColors("§6[MOJANG] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§3[BUILD TEAM]": return parseColors("§3[BUILD TEAM] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§3[BUILD TEAM§c+§3]": return parseColors("§3[BUILD TEAM§r§c+§r§3] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "§6[APPLE]": return parseColors("§6[APPLE] " + name.getPlayer().get("displayname").getAsString() + "§r");
                default: return null;
            }
        }
        return null;
    }


}
