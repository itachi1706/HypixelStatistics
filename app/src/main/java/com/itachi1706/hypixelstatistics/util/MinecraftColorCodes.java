package com.itachi1706.hypixelstatistics.util;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;

import net.hypixel.api.reply.PlayerReply;

/**
 * Created by Kenneth on 11/11/2014, 2:11 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public enum MinecraftColorCodes {
    BLACK("§0", "<font color='#000000'>", "Black", "&0"),
    DARK_BLUE("§1", "<font color='#0000AA'>", "Dark Blue", "&1"),
    DARK_GREEN("§2", "<font color='#00AA00'>", "Dark Green", "&2"),
    DARK_AQUA("§3", "<font color='#00AAAA'>", "Dark Aqua", "&3"),
    DARK_RED("§4", "<font color='#AA0000'>", "Dark Red", "&4"),
    DARK_PURPLE("§5", "<font color='#AA00AA'>", "Dark Purple", "&5"),
    GOLD("§6", "<font color='#FFAA00'>", "Gold", "&6"),
    GRAY("§7", "<font color='#AAAAAA'>", "Gray", "&7"),
    DARK_GRAY("§8", "<font color='#555555'>", "Dark Gray", "&8"),
    BLUE("§9", "<font color='#5555FF'>", "Blue", "&9"),
    GREEN("§a", "<font color='#55FF55'>", "Green", "&a"),
    AQUA("§b", "<font color='#55FFFF'>", "Aqua", "&b"),
    RED("§c", "<font color='#FF5555'>", "Red", "&c"),
    LIGHT_PURPLE("§d", "<font color='#FF55FF'>", "Light Purple", "&d"),
    YELLOW("§e", "<font color='#FFFF55'>", "Yellow", "&e"),
    WHITE("§f", "<font color='#FFFFFF'>", "White", "&f"),
    CLEAR("§r", "</font>", "Clear", "&r"); //Clear Formatting
    
    private final String colorCode, htmlCode, colorName, bukkitColorCode;
    
    MinecraftColorCodes(String colorCode, String htmlCode, String colorName, String bukkitColorCode){
        this.colorCode = colorCode;
        this.htmlCode = htmlCode;
        this.colorName = colorName;
        this.bukkitColorCode = bukkitColorCode;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    @SuppressWarnings("unused")
    public String getColorName() {
        return colorName;
    }

    @SuppressWarnings("unused")
    public String getBukkitColorCode() {
        return bukkitColorCode;
    }

    /**
     * Parse a string to HTML formatted String with colour through enums
     * (Note: If you have colour, you need to reset it with §r)
     * @param in the string to be formatted
     * @return the formatted HTMML string
     */
    public static String parseColors(String in){
        String out = in;
        out = out.replace(BLACK.getColorCode(),BLACK.getHtmlCode());                //Black
        out = out.replace(DARK_BLUE.getColorCode(),DARK_BLUE.getHtmlCode());        //Dark Blue
        out = out.replace(DARK_GREEN.getColorCode(),DARK_GREEN.getHtmlCode());      //Dark Green
        out = out.replace(DARK_AQUA.getColorCode(),DARK_AQUA.getHtmlCode());        //Dark Aqua
        out = out.replace(DARK_RED.getColorCode(),DARK_RED.getHtmlCode());          //Dark Red
        out = out.replace(DARK_PURPLE.getColorCode(),DARK_PURPLE.getHtmlCode());    //Dark Purple
        out = out.replace(GOLD.getColorCode(),GOLD.getHtmlCode());                  //Gold
        out = out.replace(GRAY.getColorCode(),GRAY.getHtmlCode());                  //Gray
        out = out.replace(DARK_GRAY.getColorCode(),DARK_GRAY.getHtmlCode());        //Dark Gray
        out = out.replace(BLUE.getColorCode(),BLUE.getHtmlCode());                  //Blue
        out = out.replace(GREEN.getColorCode(),GREEN.getHtmlCode());                //Green
        out = out.replace(AQUA.getColorCode(),AQUA.getHtmlCode());                  //Aqua
        out = out.replace(RED.getColorCode(),RED.getHtmlCode());                    //Red
        out = out.replace(LIGHT_PURPLE.getColorCode(),LIGHT_PURPLE.getHtmlCode());  //Light Purple
        out = out.replace(YELLOW.getColorCode(),YELLOW.getHtmlCode());              //Yellow
        out = out.replace(WHITE.getColorCode(),WHITE.getHtmlCode());                //White
        out = out.replace(CLEAR.getColorCode(),CLEAR.getHtmlCode());                //Close
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
    public static String parseHistoryHypixelRanks(HistoryArrayObject name){
        String special = null;
        if (name.hasPrefix()){
            special = newSpecialRanks(name.getDisplayname(), name.getPrefix());
        }
        if (special != null) return special;

        if (name.hasRank()){
            String rank = rank(name.getDisplayname(), name.getRank());
            if (rank != null) return rank;
        }

        if (name.hasNewPackageRank()){
            return newPackageRank(name.getDisplayname(), name.getNewPackageRank());
        } else if (name.hasPackageRank()){
            return packageRank(name.getDisplayname(), name.getPackageRank());
        } else {
            //Normal
            return parseColors("§7" + name.getDisplayname() + "§r");
        }
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
