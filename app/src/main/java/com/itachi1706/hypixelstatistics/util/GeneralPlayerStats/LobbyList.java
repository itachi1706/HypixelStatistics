package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats;

/**
 * Created by Kenneth on 23/3/2015, 7:15 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats
 */
@SuppressWarnings("unused")
public enum LobbyList {
    GENERAL("General", "general"),
    MEGAWALLS("Mega Walls", "walls3"),
    BSG("Blitz Survival Games", "blitz"),
    ARCADE("Arcade", "arcade"),
    ARENA("Arena Brawl", "arena"),
    TNT("The TNT Games", "tntgames"),
    QUAKE("Quakecraft", "quake"),
    VAMPIREZ("VampireZ", "vampirez"),
    PAINTBALL("Paintball Warfare", "paintball"),
    WALLS("The Walls", "walls"),
    UNKNOWN("Unknown", "unknown");

    private String name, key;

    private LobbyList(String name, String key){
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public LobbyList fromKey(String key){
        for (LobbyList l : LobbyList.values()){
            if (l.getKey().equals(key))
                return l;
        }
        return UNKNOWN;
    }
}
