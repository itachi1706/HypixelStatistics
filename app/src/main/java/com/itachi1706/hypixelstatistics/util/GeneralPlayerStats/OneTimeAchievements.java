package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats;

/**
 * Created by Kenneth on 23/3/2015, 6:24 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.GeneralPlayerStats
 */
@SuppressWarnings("unused")
public enum OneTimeAchievements {
    GENERAL_FIRST_JOIN("Achievement Get! Hypixel Server!", "Join the Hypixel Network for the first time", "general_first_join", LobbyList.GENERAL),
    GENERAL_VIP("Very Important Person", "Become a VIP", "general_vip", LobbyList.GENERAL),
    GENERAL_VIP_PLUS("Very Very Important Person", "Become a VIP+", "general_vip_plus", LobbyList.GENERAL),
    GENERAL_FIRST_CHAT("Let the world hear your voice!", "Use chat for the first time", "general_first_chat", LobbyList.GENERAL),
    GENERAL_FIRST_FRIEND("Not forever alone...", "Make a friend using the friend system", "general_first_friend", LobbyList.GENERAL),
    GENERAL_FIRST_PARTY("Strength in numbers", "Party up for the first time", "general_first_party", LobbyList.GENERAL),
    GENERAL_USE_PORTAL("Thinking with Portals", "Use a portal to teleport to a game", "general_use_portal", LobbyList.GENERAL),
    GENERAL_FIRST_GAME("Baby Steps", "Play a minigame", "general_first_game", LobbyList.GENERAL),
    GENERAL_CREEPERBOOK("Creeperbook", "Link your minecraft account with your forum account", "general_creeperbook", LobbyList.GENERAL),
    GENERAL_FRIENDS_25("Popular", "Get 25+ friends", "general_friends_25", LobbyList.GENERAL),
    GENERAL_YOUTUBER("EEEEKK!", "Be in the same lobby as a YouTuber", "general_youtuber", LobbyList.GENERAL),
    GENERAL_USE_PET("Man's best friend", "Purchase a pet and use it in-game", "general_use_pet", LobbyList.GENERAL),
    GENERAL_JOIN_VIP_LOBBY("Red Carpet Treatment", "Join the VIP lobby", "general_join_vip_lobby", LobbyList.GENERAL),

    WALLS3_ATTACK_WITHER("Strategic Mind", "Attack an enemy's Wither", "walls3_attack_wither", LobbyList.MEGAWALLS),
    WALLS3_FIND_CHEST("Chest Finder", "Find a chest while mining", "walls3_find_chest", LobbyList.MEGAWALLS),
    WALLS3_FIRST_SKILL_UPGRADE("Going to the Gym", "Unlock your first Skill Upgrade", "walls3_first_skill_upgrade", LobbyList.MEGAWALLS),
    WALLS3_WIN_WITH_LIVING_WITHER("The Blood of My Enemies", "Win with your wither still alive", "walls3_win_with_living_wither", LobbyList.MEGAWALLS),
    WALLS3_WIN_BEFORE_DEATHMATCH("Easy Mode", "Win before the Deathmatch", "walls3_win_before_deathmatch", LobbyList.MEGAWALLS),

    BLITZ_FIRST_GAME("It all begins here", "Play Blitz for the first time", "blitz_first_game", LobbyList.BSG),
    BLITZ_YOUTUBER("Play with the YouTuber", "Play a game with a YouTuber", "blitz_youtuber", LobbyList.BSG),
    BLITZ_SPAWN_HORSE("HORSEEEYYY", "Spawn a horse", "blitz_spawn_horse", LobbyList.BSG),
    BLITZ_KILL_WITHOUT_KIT("Speed Kills", "Kill two people before you get your kit", "blitz_kill_without_kit", LobbyList.BSG),
    BLITZ_USE_WOLF_TAMER("Raised by Wolves", "Use the WolfTamer kit", "blitz_use_wolf_tamer", LobbyList.BSG),
    BLITZ_CRAFT_BREAD("Peeta Mellark", "Craft bread", "blitz_craft_bread", LobbyList.BSG),
    BLITZ_FIRST_BLOOD("First Blood", "Get the first kill in a game", "blitz_first_blood", LobbyList.BSG),
    BLITZ_FULL_INVENTORY("Hoarder", "Fill up your inventory", "blitz_full_inventory", LobbyList.BSG),
    BLITZ_ENCHANTED_ARMOR("Shieldwall", "Wear a fully enchanted armor set", "blitz_enchanted_armor", LobbyList.BSG),
    BLITZ_GET_DIAMOND_SWORD("My Precious", "Obtain a Diamond Sword", "blitz_get_diamond_sword", LobbyList.BSG),
    BLITZ_ENCHANT_SWORD("Ooh...Magic!", "Enchant a sword", "blitz_enchant_sword", LobbyList.BSG),

    ARCADE_BOUNTY_HUNTER_TARGET_KILLER("Fight like a Man", "Kill your target in BountyHunter with your sword", "arcade_bounty_hunter_target_killer", LobbyList.ARCADE),
    ARCADE_TRAMPOLINIO_RED_WOOL("Bouncy Bounce", "Collect a red wool in Trampolinio", "arcade_trampolinio_red_wool", LobbyList.ARCADE),
    ARCADE_PIG_FISHING_SUPER_BACON("BACON!!!", "Capture a Super Bacon in Pig Fishing", "arcade_pig_fishing_super_bacon", LobbyList.ARCADE),

    ARENA_RUNIC("Runic Enchantments", "Unlock a Rune in the shop", "arena_runic", LobbyList.ARENA),
    ARENA_MAGICAL("Magical Shenanigans", "Open the Magical Chest for the first time", "arena_magical", LobbyList.ARENA),

    TNTGAMES_BOW_SPLEEF_FIRST_DOUBLE_JUMP("Youngling", "Get your first Bow Spleef Double Jump upgrade", "tntgames_bow_spleef_first_double_jump", LobbyList.TNT),
    TNTGAMES_TNT_RUN_NO_SPRINTING("TNT Walk", "Win TNT Run without sprinting", "tntgames_tnt_run_no_sprinting", LobbyList.TNT),
    TNTGAMES_TNT_RUN_FIRST_WIN("Unbroken sprinting", "Win a game of TNT Run", "tntgames_tnt_run_first_win", LobbyList.TNT),
    TNTGAMES_TNT_RUN_PURCHASE_POTION("Quick Feet", "Purchase a Speed Potion", "tntgames_tnt_run_purchase_potion", LobbyList.TNT),
    TNTGAMES_WIZARDS_FIRST_WIN("DiamondZ", "Win a game of TNT Wizards", "tntgames_wizards_first_win", LobbyList.TNT),

    QUAKE_GOOD_GUY_GAMER("Good Guy Gamer", "Say GG at the end of a game", "quake_good_guy_gamer", LobbyList.QUAKE),
    QUAKE_BOGOF("B.O.G.O.F", "Get a double kill", "quake_bogof", LobbyList.QUAKE),
    QUAKE_WHAT_HAVE_I_DONE("What have I done...", "Gib a staff member", "quake_what_have_i_done", LobbyList.QUAKE),
    QUAKE_FIRST_KILL("First Kill", "Be the first person to kill someone", "quake_first_kill", LobbyList.QUAKE),
    QUAKE_MY_WAY("I did it my way", "Customize your Railgun", "quake_my_way", LobbyList.QUAKE),
    QUAKE_HUMILIATION("Humiliation", "Kill the same person 4 times in a row", "quake_humiliation", LobbyList.QUAKE),
    QUAKE_LOOKING_FANCY("Looking Fancy", "Unlock your first hat", "quake_looking_fancy", LobbyList.QUAKE),

    VAMPIREZ_PURCHASE_GOLD("Coins4Gold", "Purchase Gold from the shop", "vampirez_purchase_gold", LobbyList.VAMPIREZ),
    VAMPIREZ_PURCHASE_ARMOR("Fits Like a Glove", "Purchase armor from the Gold Shop", "vampirez_purchase_armor", LobbyList.VAMPIREZ),
    VAMPIREZ_PURCHASE_SWORD("Dangerous to Go Alone", "Purchase a sword from the Gold Shop", "vampirez_purchase_sword", LobbyList.VAMPIREZ),
    VAMPIREZ_PURCHASE_FOOD("Not the Survival Games", "Purchase food from the Gold Shop", "vampirez_purchase_food", LobbyList.VAMPIREZ),
    VAMPIREZ_VAMPIRE_SHOP("Vampire Mutant", "Purchase something from the Blood Shop", "vampirez_vampire_shop", LobbyList.VAMPIREZ),
    VAMPIREZ_SOLE_SURVIVOR("Last Man Standing", "Be the last remaining Survivor", "vampirez_sole_survivor", LobbyList.VAMPIREZ),
    VAMPIREZ_VAMPIRE_FANG_KILL("The Original", "Kill a Survivor with The Original Fang", "vampirez_vampire_fang_kill", LobbyList.VAMPIREZ),
    VAMPIREZ_PURCHASE_BLOOD("Cash4Blood", "Purchase Blood from the shop", "vampirez_purchase_blood", LobbyList.VAMPIREZ),
    VAMPIREZ_VAMPIRE_KILLS_ONE_ROUND("Vampire Slayer", "Kill 15 Vampires in one game", "vampirez_vampire_kills_one_round", LobbyList.VAMPIREZ),
    VAMPIREZ_BLOOD("Bloodthirsty", "Have 150 Blood at once", "vampirez_blood", LobbyList.VAMPIREZ),
    VAMPIREZ_HEROBRINE_UPGRADE("Herobrine Rises!", "Upgrade the Herobrine perk in the Lobby Shop", "vampirez_herobrine_upgrade", LobbyList.VAMPIREZ),

    PAINTBALL_UNLOCK_KILLSTREAKS("Specialist", "Unlock ten killstreaks", "paintball_unlock_killstreaks", LobbyList.PAINTBALL),
    PAINTBALL_NO_KILLSTREAKS("Bare Bones", "Win a game without using any killstreaks", "paintball_no_killstreaks", LobbyList.PAINTBALL),
    PAINTBALL_FIRST_KILL("The Starter", "Be the first person to kill someone", "paintball_first_kill", LobbyList.PAINTBALL),
    PAINTBALL_COMBO("Combo", "Activate TripleShot and Strongarm perks at the same time", "paintball_combo", LobbyList.PAINTBALL),
    PAINTBALL_ACTIVATE_LEEROY_JENKINS("LEEEEROOYYY", "Activate Leeroy Jenkins", "paintball_leeroy_jenkins", LobbyList.PAINTBALL),
    PAINTBALL_ACTIVATE_KILLSTREAKS("Unstoppable", "Activate ten killstreaks", "paintball_combo", LobbyList.PAINTBALL),
    PAINTBALL_UNLOCK_HAT("Mad Hatter", "Unlock a hat from the store", "paintball_unlock_hat", LobbyList.PAINTBALL),
    PAINTBALL_LAST_KILL("The Finished", "Be the last person to kill someone", "paintball_last_kill", LobbyList.PAINTBALL),

    WALLS_REVENGE("Avenged!", "Kill a player that has killed one of your teammates", "walls_revenge", LobbyList.WALLS),

    WARLORDS_FIRST_OF_MANY("The First Of Many", "Get a Broken Weapon repaired by The Weaponsmith", "warlords_first_of_many", LobbyList.WARLORDS),
    WARLORDS_MEDIUM_RARE("Medium Rare", "Receive a Rare weapon from The Weaponsmith", "warlords_medium_rare", LobbyList.WARLORDS),
    WARLORDS_MAKIN_SOME_ROOM("Makin' Some Room", "Salvage a Weapon", "warlords_makin_some_room", LobbyList.WARLORDS),
    WARLORDS_GIDDY_UP("Giddy Up!", "Unlock a mount variation", "warlords_giddy_up", LobbyList.WARLORDS),
    warlords_beep_beep("Beep Beep!", "Capture a flag while under the effect of the speed powerup", "warlords_beep_beep", LobbyList.WARLORDS),
    WARLORDS_I_MUST_RESIST("I...Must...Resist", "Completely fill up your Broken Weapons inventory", "warlords_i_must_resist", LobbyList.WARLORDS),
    WARLORDS_JUICED_UP("Juiced Up!", "Get a killing blow on an enemy player while under the effect of the damage powerup", "warlords_juiced_up", LobbyList.WARLORDS),
    
    UNKNOWN("Unknown", "Unknown achievement", "unknown", LobbyList.UNKNOWN);


    private String title, description, oneTimeAchievementKey;
    private LobbyList gametype;

    OneTimeAchievements(String title, String description, String oneTimeAchievementKey, LobbyList gametype){
        this.title = title;
        this.description = description;
        this.oneTimeAchievementKey = oneTimeAchievementKey;
        this.gametype = gametype;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOneTimeAchievementKey() {
        return oneTimeAchievementKey;
    }

    public LobbyList getGametype() {
        return gametype;
    }

    public static OneTimeAchievements fromDatabase(String key){
        for (OneTimeAchievements ach : OneTimeAchievements.values()){
            if (ach.getOneTimeAchievementKey().equals(key)){
                return ach;
            }
        }
        return UNKNOWN;
    }
}
