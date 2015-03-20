package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats;

/**
 * Created by Kenneth on 20/3/2015, 9:04 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats
 */
public enum QuestName {
    BLITZERK("Blitzerk", "blitzerk"),
    EXPLOSIVE_GAMES("Explosive Games", "explosive_games"),
    GLADIATOR("Gladiator", "gladiator"),
    HALLOWEEN2014("Halloween 2014 Event", "halloween2014"),
    MEGAWALLER("Mega Waller", "megawaller"),
    NUGGET_WARRIORS("Nugget Warriors", "nugget_warriors"),
    PAINTBALL_EXPERT("Paintball Expert", "paintball_expert"),
    SERIAL_KILLER("Serial Killer", "serial_killer"),
    SPACE_MISSION("Space Mission", "space_mission"),
    TNT_ADDICT("TNT Addict", "tnt_addict"),
    WALLER("Waller", "waller"),
    WARLORDS_CTF("Daily Quest: Capture The Flag", "warlords_ctf"),
    WARLORDS_DEDICATION("Weekly Quest: Dedication", "warlords_dedication"),
    WARLORDS_DOMINATION("Daily Quest: Domination", "warlords_domination"),
    WARLORDS_WIN("Daily Quest: Warlords", "warlords_win"),
    WARRIORS_JOURNEY("Warrior's Journey", "warriors_journey"),
    WELCOME_TO_HELL("Welcome to Hell", "welcome_to_hell"),
    UNKNOWN("Unknown", "unknown");

    private String questTitle, questKey;

    private QuestName(String questTitle, String questKey){
        this.questTitle = questTitle;
        this.questKey = questKey;
    }

    public String getQuestTitle() {
        return questTitle;
    }

    public String getQuestKey() {
        return questKey;
    }

    public static QuestName fromDB(String key){
        for (QuestName qn : QuestName.values()){
            if (qn.getQuestKey().equals(key))
                return qn;
        }
        return UNKNOWN;
    }
}
