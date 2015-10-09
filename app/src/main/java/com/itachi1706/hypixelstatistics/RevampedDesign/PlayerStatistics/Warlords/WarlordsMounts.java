package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.Warlords;

/**
 * Created by Kenneth on 2/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords
 */
@Deprecated
public enum WarlordsMounts {
    NOBLE_STEED("noble_steed", "§7Noble Steed§r"),
    UNDYING_MARE("undying_mare", "§aUndying Mare§r"),
    CORPSE_MARE("corpse_mare", "§aCorpse Mare§r"),
    WAR_HORSE("war_horse", "§9War Horse§r"),
    BATTLE_BEAST("battle_beast", "§9Battle Beast§r"),
    RAGING_STALLION("raging_stallion", "§9Raging Stallion§r"),
    UNKNOWN("unknown", "Unknown");

    private final String id, name;

    WarlordsMounts(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static WarlordsMounts fromDatabase(String id){
        for (WarlordsMounts m : WarlordsMounts.values()){
            if (m.getId().equals(id))
                return m;
        }
        return UNKNOWN;
    }
}
