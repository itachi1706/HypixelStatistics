package com.itachi1706.hypixelstatistics.RevampedDesign.Interfaces;

import net.hypixel.api.reply.PlayerReply;

/**
 * Created by Kenneth on 8/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.Interfaces
 */
public interface FragmentInterface {
    void processPlayerJson(String json);
    void processPlayerObject(PlayerReply obj, String json);
}
