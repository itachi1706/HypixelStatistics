package com.itachi1706.hypixelstatistics.util;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util
 */
public class GeneratePlaceholderDrawables {

    /**
     * Creates and return the builder for TextDrawable
     * @return The TextDrawable builder
     */
    private static TextDrawable.IBuilder getBuilder(){
        return TextDrawable.builder()
                .beginConfig().withBorder(4)
                .width(72).height(72)
                .endConfig().rect();
    }

    /**
     * Generate the TextDrawable from the Initials
     * @param name Initials to generate drawable from
     * @return The TextDrawable object
     */
    private static TextDrawable generateFromMcNameInitials(String name){
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor("mcPlayerName");
        TextDrawable.IBuilder builder = getBuilder();
        return builder.build(name, color);
    }

    /**
     * Generates a up to 2-letter initial from a string
     * @param name The string to generate
     * @return up to 2-letters initials
     */
    private static String generateInitialsFromName(String name){
        String val = "";
        boolean space = true;
        for (int i = 0; i < name.length(); i++){
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) val += c;
            else if (!Character.isLetter(c) && !Character.isDigit(c)) val += c;
            else if (Character.isDigit(c) && space) { val += c; }
            else if (Character.isSpaceChar(c)) { space = true; continue; }
            else if (space) val += c;
            space = false;
        }

        //Only take first and last char
        return val.length() > 1 ? val.substring(0, 1) + val.substring(val.length() - 1, val.length()) : val.substring(0,1);
    }


    /**
     * Generate a up to 2-letter TextDrawable from name, including the extraction of initials
     * @param name a string to extract initials and generate the drawable from
     * @return Generated drawable
     */
    public static TextDrawable generateFromMcNameWithInitialsConversion(String name){
        return generateFromMcNameInitials(generateInitialsFromName(name));
    }

}
