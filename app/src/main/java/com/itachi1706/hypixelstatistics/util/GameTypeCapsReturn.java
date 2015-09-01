package com.itachi1706.hypixelstatistics.util;

import net.hypixel.api.util.GameType;

/**
 * Created by Kenneth on 17/3/2015, 9:23 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class GameTypeCapsReturn {

    /**
     * @param name The key used in the database
     * @return The GameType associated with that key, or null if there isn't one.
     */
    public static GameType fromDatabase(String name) {
        switch (name) {
            case "QUAKE":
                return GameType.QUAKECRAFT;
            case "WALLS":
                return GameType.WALLS;
            case "PAINTBALL":
                return GameType.PAINTBALL;
            case "HUNGERGAMES":
            case "SURVIVAL_GAMES":
                return GameType.SURVIVAL_GAMES;
            case "TNTGAMES":
                return GameType.TNTGAMES;
            case "VAMPIREZ":
                return GameType.VAMPIREZ;
            case "WALLS3":
                return GameType.WALLS3;
            case "ARCADE":
                return GameType.ARCADE;
            case "ARENA":
                return GameType.ARENA;
            case "MCGO":
                return GameType.MCGO;
            case "UHC":
                return GameType.UHC;
            case "BATTLEGROUND":
                return GameType.BATTLEGROUND;
        }
        return null;
    }
}
