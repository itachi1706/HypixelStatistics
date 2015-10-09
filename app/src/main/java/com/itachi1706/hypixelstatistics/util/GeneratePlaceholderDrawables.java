package com.itachi1706.hypixelstatistics.util;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.w3c.dom.Text;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util
 */
public class GeneratePlaceholderDrawables {

    private static TextDrawable.IBuilder getBuilder(){
        return TextDrawable.builder()
                .beginConfig().withBorder(4)
                .width(72).height(72)
                .endConfig().rect();
    }

    private static TextDrawable generateFromMcNameInitials(String name){
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor("mcPlayerName");
        TextDrawable.IBuilder builder = getBuilder();
        return builder.build(name, color);
    }

    private static String generateInitialsFromName(String name){
        String[] inits = name.split(" ");
        String initial = "";
        for (String s : inits){
            initial += s.charAt(0);
        }
        return initial;
    }

    public static TextDrawable generateFromMcNameWithInitialsConversion(String name){
        return generateFromMcNameInitials(generateInitialsFromName(name));
    }

}
