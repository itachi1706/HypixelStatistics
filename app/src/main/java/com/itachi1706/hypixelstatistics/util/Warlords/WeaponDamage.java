package com.itachi1706.hypixelstatistics.util.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:49 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.Warlords
 */
@SuppressWarnings("unused")
public enum WeaponDamage {
    ZERO("0 - 0", 0, 0, 0),
    EIGHTY("68 - 92", 68, 92, 80),
    EIGHTY_ONE("68 - 93", 68, 93, 81),
    EIGHTY_TWO("69 - 94", 69, 94, 82),
    EIGHTY_THREE("70 - 95", 70, 95, 83),
    EIGHTY_FOUR("71 - 96", 71, 96, 84),
    EIGHTY_FIVE("72 - 97", 72, 97, 85),
    EIGHTY_SIX("73 - 98", 73, 98, 86),
    EIGHTY_SEVEN("73 - 100", 73, 100, 87),
    EIGHTY_EIGHT("74 - 101", 74, 101, 88),
    EIGHTY_NINE("75 - 102", 75, 102, 89),
    NINETY("76 - 103", 76, 103, 90),
    NINETY_TWO("78 - 105", 78, 105, 92),
    NINETY_FOUR("79 - 108", 79, 108, 94),
    NINETY_SIX("81 - 110", 81, 110, 96),
    NINETY_SEVEN("82 - 111", 82, 111, 97),
    NINETY_EIGHT("83 - 112", 83, 112, 98),
    NINETY_NINE("84 - 113", 84, 113, 99),
    ONE_HUNDERED("85 - 115", 85, 115, 100),
    ONE_HUNDRED_ONE("85 - 116 ", 85, 116, 101),
    ONE_HUNDERED_TEN("93 - 126", 93, 126, 110),
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
            case 80: return EIGHTY;
            case 81: return EIGHTY_ONE;
            case 82: return EIGHTY_TWO;
            case 83: return EIGHTY_THREE;
            case 84: return EIGHTY_FOUR;
            case 85: return EIGHTY_FIVE;
            case 86: return EIGHTY_SIX;
            case 87: return EIGHTY_SEVEN;
            case 88: return EIGHTY_EIGHT;
            case 89: return EIGHTY_NINE;
            case 90: return NINETY;
            case 92: return NINETY_TWO;
            case 94: return NINETY_FOUR;
            case 96: return NINETY_SIX;
            case 97: return NINETY_SEVEN;
            case 98: return NINETY_EIGHT;
            case 99: return NINETY_NINE;
            case 100: return ONE_HUNDERED;
            case 101: return ONE_HUNDRED_ONE;
            case 110: return ONE_HUNDERED_TEN;
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
