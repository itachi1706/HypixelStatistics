package com.itachi1706.hypixelstatistics.PlayerStatistics;

import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
@Deprecated
public class DonatorStatistics {

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    public static ArrayList<ResultDescription> parseDonor(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (reply.getPlayer().has("fly"))
            descArray.add(new ResultDescription("Fly Mode", reply.getPlayer().get("fly").getAsString()));
        if (reply.getPlayer().has("petActive"))
            descArray.add(new ResultDescription("Active Pet", reply.getPlayer().get("petActive").getAsString()));
        else
            descArray.add(new ResultDescription("Active Pet", "false"));
        if (reply.getPlayer().has("pp"))
            descArray.add(new ResultDescription("Particle Pack", reply.getPlayer().get("pp").getAsString()));
        if (reply.getPlayer().has("testpass"))
            descArray.add(new ResultDescription("Test Server Access", reply.getPlayer().get("testpass").getAsString()));
        if (reply.getPlayer().has("wardrobe"))
            descArray.add(new ResultDescription("Wardrobe (H,C,L,B)", reply.getPlayer().get("wardrobe").getAsString()));
        if (reply.getPlayer().has("auto_spawn_pet"))
            descArray.add(new ResultDescription("Auto-Spawn Pet", reply.getPlayer().get("auto_spawn_pet").getAsString()));
        if (reply.getPlayer().has("legacyGolem"))
            descArray.add(new ResultDescription(MinecraftColorCodes.parseColors("Golem Supporter §6(Pre-EULA)§r"), reply.getPlayer().get("legacyGolem").getAsString()));
        return descArray;
    }
}
