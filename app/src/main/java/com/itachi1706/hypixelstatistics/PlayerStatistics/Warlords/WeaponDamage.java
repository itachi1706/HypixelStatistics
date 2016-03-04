package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:49 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords
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
    NINETY_ONE("77 - 104", 77, 104, 91),
    NINETY_TWO("78 - 105", 78, 105, 92),
    NINETY_THREE("79 - 106", 79, 106, 93),
    NINETY_FOUR("79 - 108", 79, 108, 94),
    NINETY_FIVE("80 - 109", 80, 109, 95),
    NINETY_SIX("81 - 110", 81, 110, 96),
    NINETY_SEVEN("82 - 111", 82, 111, 97),
    NINETY_EIGHT("83 - 112", 83, 112, 98),
    NINETY_NINE("84 - 113", 84, 113, 99),
    ONE_HUNDERED("85 - 115", 85, 115, 100),
    ONE_HUNDRED_ONE("85 - 116", 85, 116, 101),
    ONE_HUNDRED_SIX("90 - 121", 90, 121, 106),
    ONE_HUNDERED_SEVEN("90 - 123", 90, 123, 107),
    ONE_HUNDERED_NINE("92 - 125", 92, 125, 109),
    ONE_HUNDERED_TEN("93 - 126", 93, 126, 110),
    WIP("Work in Progress (WIP)", 0, 0, 0);

    private final String damageRangeString;
    private final int minDamage, maxDamage, id;

    WeaponDamage(String damageRange, int minDamage, int maxDamage, int id){
        this.damageRangeString = damageRange;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.id = id;
    }

    public String getDamageRangeString() {
        return damageRangeString;
    }

    public static WeaponDamage fromDatabase(int damageID){
        for (WeaponDamage d : WeaponDamage.values()){
            if (d.getWeaponDamageID() == damageID)
                return d;
        }
        return WIP;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getWeaponDamageID() { return id; }
}
