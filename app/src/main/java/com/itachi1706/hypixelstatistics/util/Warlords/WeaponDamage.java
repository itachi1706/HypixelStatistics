package com.itachi1706.hypixelstatistics.util.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:49 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.Warlords
 */
@SuppressWarnings("unused")
public enum WeaponDamage {
    ZERO("0 - 0", 0, 0),
    WIP("Work in Progress (WIP)", 0, 0);

    private final String damageRangeString;
    private final int minDamage, maxDamage;

    private WeaponDamage(String damageRange, int minDamage, int maxDamage){
        this.damageRangeString = damageRange;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public String getDamageRangeString() {
        return damageRangeString;
    }

    public static WeaponDamage fromDatabase(int damageID){
        switch (damageID){
            default: return WIP;
        }
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getMinDamage() {
        return minDamage;
    }
}
