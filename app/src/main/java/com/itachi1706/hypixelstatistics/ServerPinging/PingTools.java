package com.itachi1706.hypixelstatistics.ServerPinging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kenneth on 7/2/2015, 5:35 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.ServerPinging
 */
public class PingTools {

    @SuppressWarnings("unused")
    public static boolean isIP(String ip){
        boolean isValid = false;

		/*IP: A numeric value will have following format:
		         ^[-+]?: Starts with an optional "+" or "-" sign.
		     [0-9]*: May have one or more digits.
		    \\.? : May contain an optional "." (decimal point) character.
		    [0-9]+$ : ends with numeric digit.
		*/

        //Initialize reg ex for numeric data.
        String expression = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(ip);
        if(matcher.matches()){
            isValid = true;
        }
        return isValid;
    }

    public static String parseFormatting(String in){
        String out = in;
        //out = out.replace("§", "</font><font color=#55FFFF>");
        out = out.replace("§0", "</font><font color=#000000>");
        out = out.replace("§1", "</font><font color=#0000AA>");
        out = out.replace("§2", "</font><font color=#00AA00>");
        out = out.replace("§3", "</font><font color=#00AAAA>");
        out = out.replace("§4", "</font><font color=#AA0000>");
        out = out.replace("§5", "</font><font color=#AA00AA>");
        out = out.replace("§6", "</font><font color=#FFAA00>");
        out = out.replace("§7", "</font><font color=#AAAAAA>");
        out = out.replace("§8", "</font><font color=#555555>");
        out = out.replace("§9", "</font><font color=#5555FF>");
        out = out.replace("§a", "</font><font color=#55FF55>");
        out = out.replace("§b", "</font><font color=#55FFFF>");
        out = out.replace("§c", "</font><font color=#FF5555>");
        out = out.replace("§d", "</font><font color=#FF55FF>");
        out = out.replace("§e", "</font><font color=#FFFF55>");
        out = out.replace("§f", "</font><font color=#FFFFFF>");
        //End of Colors
        // Formatting
        out = out.replace("§k", "");
        out = out.replace("§l", "");
        out = out.replace("§m", "");
        out = out.replace("§n", "");
        out = out.replace("§o", "");
        out = out.replace("§r", "</font><font color=#FFFFFF>");
        return out;
    }

}
