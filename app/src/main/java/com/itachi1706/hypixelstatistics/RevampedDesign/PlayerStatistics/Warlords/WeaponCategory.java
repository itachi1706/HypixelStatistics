package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:06 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords
 */
@SuppressWarnings("unused")
@Deprecated
public enum WeaponCategory {
    COMMON("Common", "§a"),
    RARE("Rare", "§9"),
    EPIC("Epic", "§5"),
    LEGENDARY("Legendary", "§6"),
    DEFAULT("Error", "§f");

    private final String name, colorCode;

    WeaponCategory(String name, String colorCode){
        this.name = name;
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getName() {
        return name;
    }

    public static WeaponCategory fromDatabase(String categoryName){
        switch (categoryName){
            case "COMMON": return COMMON;
            case "RARE": return RARE;
            case "EPIC": return EPIC;
            case "LEGENDARY": return LEGENDARY;
            default: return DEFAULT;
        }
    }
}
