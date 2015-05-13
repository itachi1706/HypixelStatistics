package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats;

/**
 * Created by Kenneth on 25/3/2015, 6:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats
 */
@SuppressWarnings("unused")
public enum OngoingAchievements {
    //Replace %s with the max value of that tier
    GENERAL_COINS("Ultimate Banker ", "Earn a total of %s coins", "general_coins", 5, 1000, 25000, 75000, 150000, 250000, LobbyList.GENERAL),
    GENERAL_WINS("Winning! ", "Win %s minigames", "general_wins", 5, 15, 150, 500, 1500, 2500, LobbyList.GENERAL),

    WALLS3_COINS("Mega Walls Banker ", "Earn %s coins in Mega Walls", "walls3_coins", 5, 5000, 10000, 35000, 55000, 75000, LobbyList.MEGAWALLS),
    WALLS3_KILLS("Mega Walls Slayer ", "Kill %s players", "walls3_kills", 5, 10, 50, 250, 1500, 4500, LobbyList.MEGAWALLS),
    WALLS3_WINS("Mega Walls Champion ", "Win %s games", "walls3_wins", 5, 10, 50, 100, 250, 500, LobbyList.MEGAWALLS),

    BLITZ_COINS("Blitz Banker ", "Earn %s coins in Blitz", "blitz_coins", 5, 5000, 10000, 35000, 55000, 75000, LobbyList.BSG),
    BLITZ_KILLS("Blitz Slayer ", "Kill %s players", "blitz_kills", 5, 10, 100, 1000, 5000, 12000, LobbyList.BSG),
    BLITZ_WINS("One Man Army ", "Win %s games", "blitz_wins", 5, 10, 100, 250, 500, 1200, LobbyList.BSG),

    ARENA_CLIMB_THE_RANKS("Climb the Ranks ", "Reach %s rating", "arena_climb_the_ranks", 3, 1300, 1700, 2000, -1, -1, LobbyList.ARENA),
    ARENA_GOTTA_WEAR_EM_ALL("Gotta Wear 'Em All ", "Collect %s hats", "arena_gotta_wear_em_all", 5, 5, 10, 15, 20, 25, LobbyList.ARENA),
    ARENA_GLADIATOR("Gladiator ", "Win 2v2 %s times", "arena_gladiator", 5, 50, 100, 500, 1000, 5000, LobbyList.ARENA),
    ARENA_BOSSED("Bossed ", "Kill %s players", "arena_bossed", 5, 10, 100, 250, 1500, 5000, LobbyList.ARENA),

    TNTGAMES_BOW_SPLEEF_WINS("Archery ", "Win %s games of Bow Spleef", "tntgames_bow_spleef_wins", 5, 10, 20, 50, 100, 500, LobbyList.TNT),
    TNTGAMES_TNT_RUN_WINS("Marathon ", "Win %s games of TNT Run", "tntgames_tnt_run_wins", 5, 10, 20, 50, 100, 500, LobbyList.TNT),
    TNTGAMES_WIZARDS_WINS("Sorcery ", "Win %s games of Wizards", "tntgames_wizards_wins", 5, 10, 20, 50, 100, 500, LobbyList.TNT),

    QUAKE_KILLS("MLG ", "Kill %s players", "quake_kills", 5, 100, 1000, 10000, 50000, 100000, LobbyList.QUAKE),
    QUAKE_WINS("Winner ", "Win %s games", "quake_wins", 5, 10, 50, 100, 1000, 2000, LobbyList.QUAKE),
    QUAKE_KILLING_SPREES("Killing Spree Madness ", "Get %s killing sprees", "quake_killing_sprees", 5, 25, 50, 100, 150, 250, LobbyList.QUAKE),

    VAMPIREZ_SURVIVOR_WINS("Survivor ", "Win %s games as a Survivor", "vampirez_survivor_wins", 5, 1, 10, 50, 100, 150, LobbyList.VAMPIREZ),
    VAMPIREZ_COINS("VampireZ Banker ", "Earn %s coins in VampireZ", "vampirez_coins", 5, 10000, 20000, 55000, 75000, 150000, LobbyList.VAMPIREZ),
    VAMPIREZ_KILL_VAMPIRES("Vampire Hunter ", "Kill %s Vampires", "vampirez_kill_vampires", 5, 10, 100, 500, 1000, 2500, LobbyList.VAMPIREZ),
    VAMPIREZ_KILL_SURVIVORS("Blood Sucker ", "Kill %s Survivors", "vampirez_kill_survivors", 5, 10, 100, 500, 1000, 2500, LobbyList.VAMPIREZ),

    PAINTBALL_COINS("Paintball Banker ", "Earn %s coins in Paintball", "paintball_coins", 5, 5000, 10000, 35000, 55000, 75000, LobbyList.PAINTBALL),
    PAINTBALL_KILLS("Painter ", "Kill %s players", "paintball_kills", 5, 100, 1000, 10000, 100000, 200000, LobbyList.PAINTBALL),
    PAINTBALL_WINS("Paintball Superstar ", "Win %s games", "paintball_wins", 5, 10, 50, 100, 1000, 2500, LobbyList.PAINTBALL),

    WALLS_COINS("Walls Banker ", "Earn %s coins in The Walls", "walls_coins", 5, 5000, 10000, 35000, 55000, 75000, LobbyList.WALLS),
    WALLS_KILLS("Walls Slayer ", "Kill %s players", "walls_kills", 5, 10, 50, 250, 1000, 2500, LobbyList.WALLS),
    WALLS_WINS("Walls Warrior ", "Win %s games", "walls_wins", 5, 10, 50, 100, 250, 500, LobbyList.WALLS),

    //TODO Verify in-game (Particularly kills and assists achievements)
    WARLORDS_WARRIOR_LEVEL("Anger Management ", "Obtain Lv%s with the Warrior class", "warlords_warrior_level", 5, 20, 40, 60, 80, 90, LobbyList.WARLORDS),
    WARLORDS_MAGE_LEVEL("Icy Hot ", "Obtain Lv%s with the Mage class", "warlords_mage_level", 5, 20, 40, 60, 80, 90, LobbyList.WARLORDS),
    WARLORDS_KILLS("Kills Secured ", "Kill %s players in any game mode", "warlords_kills", 5, 50, 250, 1000, 5000, 25000, LobbyList.WARLORDS),
    WARLORDS_PALADIN_LEVEL("Knight of Justice ", "Obtain Lv%s with the Paladin class", "warlords_paladin_level", 5, 20, 40, 60, 80, 90, LobbyList.WARLORDS),
    WARLORDS_SHAMAN_LEVEL("Master of the Elements ", "Obtain Lv%s with the Shaman class", "warlords_shaman_level", 5, 20, 40, 60, 80, 90, LobbyList.WARLORDS),
    WARLORDS_ASSIST("Kills Secured ", "Earn %s assists in any game mode", "warlords_assist", 5, 20, 100, 500, 2500, 12500, LobbyList.WARLORDS),
    WARLORDS_REPAIR_WEAPONS("RNG God Offerings ", "Get %s Broken Weapons repaired by The Weaponsmith", "warlords_repair_weapons", 5, 10, 50, 100, 250, 500, LobbyList.WARLORDS),
    WARLORDS_COINS("Spoils of War ", "Earn %s coins in Warlords", "warlords_coins", 5, 10000, 50000, 250000, 1250000, 6250000, LobbyList.WARLORDS),

    UNKNOWN("Unknown ", "%s", "unknown", 1, 0, -1, -1, -1, -1, LobbyList.UNKNOWN);

    private String achievementKey, title, description;
    private int tier1, tier2, tier3, tier4, tier5, max_tiers;
    private LobbyList achievementLobbies;

    //Possible Tiers: 3/5
    OngoingAchievements(String title, String description, String achievementKey, int max_tiers, int tier1, int tier2, int tier3, int tier4, int tier5, LobbyList achievementLobbies){
        this.title = title;
        this.description = description;
        this.achievementKey = achievementKey;
        this.max_tiers = max_tiers;
        this.tier1 = tier1;
        this.tier2 = tier2;
        this.tier3 = tier3;
        this.tier4 = tier4;
        this.tier5 = tier5;
        this.achievementLobbies = achievementLobbies;
    }

    public String getAchievementKey() {
        return achievementKey;
    }

    public int getTier1() {
        return tier1;
    }

    public int getTier2() {
        return tier2;
    }

    public int getTier3() {
        return tier3;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTier4() {
        return tier4;
    }

    public int getTier5() {
        return tier5;
    }

    public int getMax_tiers() {
        return max_tiers;
    }

    public LobbyList getAchievementLobbies(){
        return achievementLobbies;
    }

    public static OngoingAchievements fromDatabase(String key){
        for (OngoingAchievements a : OngoingAchievements.values()){
            if (a.getAchievementKey().equals(key))
                return a;
        }
        return UNKNOWN;
    }

    public static int getTierByTier(OngoingAchievements achievement, int tier){
        if (achievement.getMax_tiers() < tier || tier <= 0) return -1;

        switch (tier){
            case 1: return achievement.getTier1();
            case 2: return achievement.getTier2();
            case 3: return achievement.getTier3();
            case 4: return achievement.getTier4();
            case 5: return achievement.getTier5();
        }
        return -1;
    }

    public static String getDescriptionByTier(OngoingAchievements achievement, int tier){
        if (achievement.getMax_tiers() < tier || tier <= 0) return "Invalid Tier";
        return achievement.getDescription().replaceAll("%s", getTierByTier(achievement, tier) + "");
    }

    public static String getTitleByTier(OngoingAchievements achievement, int tier){
        if (achievement.getMax_tiers() < tier || tier <= 0) return achievement.getTitle() + "?";
        switch (tier){
            case 1: return achievement.getTitle() + "I";
            case 2: return achievement.getTitle() + "II";
            case 3: return achievement.getTitle() + "III";
            case 4: return achievement.getTitle() + "IV";
            case 5: return achievement.getTitle() + "V";
        }
        return achievement.getTitle() + "?";
    }
}
