package com.itachi1706.hypixelstatistics.PlayerStatistics;

import com.itachi1706.hypixelstatistics.Objects.PlayerInfoStatistics;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
public class StaffOrYtStatistics {

    /* Staff/YT Only Information
        vanished, stoggle, silence, chatTunnel, nick, prefix
     */
    public static ArrayList<PlayerInfoStatistics> parsePriviledged(PlayerReply reply){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        if (reply.getPlayer().has("vanished"))
            descArray.add(new PlayerInfoStatistics("Vanished", reply.getPlayer().get("vanished").getAsString()));
        if (reply.getPlayer().has("stoggle")) {
            if (reply.getPlayer().get("stoggle").getAsBoolean())
                descArray.add(new PlayerInfoStatistics("Staff Chat" , "Enabled"));
            else
                descArray.add(new PlayerInfoStatistics("Staff Chat", "Disabled"));
        }
        if (reply.getPlayer().has("silence"))
            descArray.add(new PlayerInfoStatistics("Chat Silenced", reply.getPlayer().get("silence").getAsString()));
        if (reply.getPlayer().has("chatTunnel")) {
            if (reply.getPlayer().get("chatTunnel").isJsonNull())
                descArray.add(new PlayerInfoStatistics("Tunneled Into", "None"));
            else
                descArray.add(new PlayerInfoStatistics("Tunneled Into", reply.getPlayer().get("chatTunnel").getAsString()));
        }
        if (reply.getPlayer().has("nick"))
            descArray.add(new PlayerInfoStatistics("Nicked As", reply.getPlayer().get("nick").getAsString()));
        if (reply.getPlayer().has("prefix"))
            descArray.add(new PlayerInfoStatistics("Rank Prefix", reply.getPlayer().get("prefix").getAsString()));
        return descArray;
    }
}
