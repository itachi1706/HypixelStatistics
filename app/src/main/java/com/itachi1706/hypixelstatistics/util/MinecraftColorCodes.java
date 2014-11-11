package com.itachi1706.hypixelstatistics.util;

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
                case "NORMAL": return name.getPlayer().get("displayname").getAsString();
                case "VIP": return parseColors("§a[VIP] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "VIP_PLUS": return parseColors("§a[VIP§r§6+§r§a] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MVP": return parseColors("§b[VIP] " + name.getPlayer().get("displayname").getAsString() + "§r");
                case "MVP_PLUS": return parseColors("§b[VIP§r§c+§r§b] " + name.getPlayer().get("displayname").getAsString() + "§r");
            }
        } else {
            //Normal
            return name.getPlayer().get("displayname").getAsString();
        }
        return "Error parsing String";
    }
}
