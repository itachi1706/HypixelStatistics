package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats;

/**
 * Created by Kenneth on 20/3/2015, 8:46 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats
 */
public enum QuestObjectives {
    KILL("Killed Players", "kill", -1),
    WIN("Won Games", "win", -1),
    PAINTBALL("Kill 55 Players in Paintball Warfare", "paintball", 55),
    QUAKE("Kill 55 Players in QuakeCraft", "quake", 55),
    WARLORDS_WEEKLY_DEDI("Complete 30 matches with 10 kills/assists", "warlords_weekly_dedi", 30),
    ARENAWIN2("Win 2 games of Arena Brawl", "arenawin2", 2),
    MEGAWALLSWIN("Win 1 game of Mega Walls", "megawallswin", 1),
    PAINTBALLWIN("Win 2 games of Paintball", "paintballwin", 2),
    QUAKE25KILL("Kill 25 Players in QuakeCraft", "quake25kill", 25),
    TNTWIN("Win 3 games of the TNT Games", "tntwin", 3),
    VAMPIREZKILLHUMAN("Kill 1 human in VampireZ", "vampirezkillhuman", 1),
    VAMPIREZKILLVAMPS("Kill 3 vampires in VampireZ", "vampirezkillvamps", 3),
    BLITZKILL("Kill 5 players in Blitz Survival Games", "blitzkill", 5),
    BLITZ("Win 20 games of Blitz Survival Games", "blitz", 20),
    MEGAWALLS("Win 10 games of Mega Walls", "megawalls", 10),
    KILLBLITZ10("Kill 10 players in Blitz Survival Games", "killblitz10", 10),
    WINBLITZ("Win 1 game of Blitz Survival Games", "winblitz", 1),
    UNKNOWN("Unknown", "unknown", -1);

    private String humanReadableDesc, keyName;
    private int maxLimit;

    private QuestObjectives(String humanReadableDesc, String keyName, int maxLimit){
        this.humanReadableDesc = humanReadableDesc;
        this.keyName = keyName;
        this.maxLimit = maxLimit;
    }

    public String getHumanReadableDesc() {
        return humanReadableDesc;
    }

    public String getKeyName() {
        return keyName;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public static QuestObjectives fromDB(String key){
        for (QuestObjectives qb : QuestObjectives.values()){
            if (qb.getKeyName().equals(key))
                return qb;
        }
        return UNKNOWN;
    }


}
