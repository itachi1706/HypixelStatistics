package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics;

import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
public class DonatorStatistics {

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    public static ArrayList<PlayerInfoStatistics> parseDonor(PlayerReply reply){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        if (reply.getPlayer().has("fly"))
            descArray.add(new PlayerInfoStatistics("Fly Mode", reply.getPlayer().get("fly").getAsString()));
        if (reply.getPlayer().has("petActive"))
            descArray.add(new PlayerInfoStatistics("Active Pet", reply.getPlayer().get("petActive").getAsString()));
        else
            descArray.add(new PlayerInfoStatistics("Active Pet", "false"));
        if (reply.getPlayer().has("pp"))
            descArray.add(new PlayerInfoStatistics("Particle Pack", reply.getPlayer().get("pp").getAsString()));
        if (reply.getPlayer().has("testpass"))
            descArray.add(new PlayerInfoStatistics("Test Server Access", reply.getPlayer().get("testpass").getAsString()));
        if (reply.getPlayer().has("wardrobe"))
            descArray.add(new PlayerInfoStatistics("Wardrobe (H,C,L,B)", reply.getPlayer().get("wardrobe").getAsString()));
        if (reply.getPlayer().has("auto_spawn_pet"))
            descArray.add(new PlayerInfoStatistics("Auto-Spawn Pet", reply.getPlayer().get("auto_spawn_pet").getAsString()));
        if (reply.getPlayer().has("legacyGolem"))
            descArray.add(new PlayerInfoStatistics(MinecraftColorCodes.parseColors("Golem Supporter §6(Pre-EULA)§r"), reply.getPlayer().get("legacyGolem").getAsString()));
        return descArray;
    }
}
