package com.itachi1706.hypixelstatistics.util.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:49 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.Warlords
 */
@SuppressWarnings("unused")
public enum WeaponDamage {
    ZERO("0 - 0", 0, 0, 0),
    NINETY("76 - 103", 76, 103, 90),
    NINETY_TWO("78 - 105", 78, 105, 92),
    NINETY_FOUR("79 - 108", 79, 108, 94),
    NINETY_SIX("81 - 110", 81, 110, 96),
    NINETY_SEVEN("82 - 111", 82, 111, 97),
    NINETY_EIGHT("83 - 112", 83, 112, 98),
    NINETY_NINE("84 - 113", 84, 113, 99),
    ONE_HUNDERED("85 - 115", 85, 115, 100),
    ONE_HUNDRED_ONE("85 - 116 ", 85, 116, 101),
    WIP("Work in Progress (WIP)", 0, 0, 0);

    private final String damageRangeString;
    private final int minDamage, maxDamage, id;

    private WeaponDamage(String damageRange, int minDamage, int maxDamage, int id){
        this.damageRangeString = damageRange;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.id = id;
    }

    public String getDamageRangeString() {
        return damageRangeString;
    }

    public static WeaponDamage fromDatabase(int damageID){
        switch (damageID){
            case 90: return NINETY;
            case 92: return NINETY_TWO;
            case 94: return NINETY_FOUR;
            case 96: return NINETY_SIX;
            case 97: return NINETY_SEVEN;
            case 98: return NINETY_EIGHT;
            case 99: return NINETY_NINE;
            case 100: return ONE_HUNDERED;
            case 101: return ONE_HUNDRED_ONE;
            default: return WIP;
        }
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getWeaponDamageID() { return id; }
}
