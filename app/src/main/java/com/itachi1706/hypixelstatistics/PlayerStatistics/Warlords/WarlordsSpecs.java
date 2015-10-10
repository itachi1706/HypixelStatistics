package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 6:00 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords
 */
@SuppressWarnings("unused")
@Deprecated
public enum WarlordsSpecs {
    WARRIOR("Unknown Warrior Spec", "Warrior"),
    PALADIN("Unknown Paladin Spec", "Paladin"),
    MAGE("Unknown Mage Spec", "Mage"),
    SHAMAN("Unknown Shaman Spec", "Shaman"),
    UNKNOWN("Unknown Class", "???"),
    PYROMANCER("Pyromancer", "Mage"),
    CRYOMANCER("Cryomancer", "Mage"),
    AQUAMANCER("Aquamancer", "Mage"),
    BERSERKER("Berserker", "Warrior"),
    DEFENDER("Defender", "Warrior"),
    AVENGER("Avenger", "Paladin"),
    CRUSADER("Crusader", "Paladin"),
    PROTECTOR("Protector", "Paladin"),
    THUNDERLORD("Thunderlord", "Shaman"),
    EARTHWARDEN("Earthwarden", "Shaman");

    private final String specName, className;

    WarlordsSpecs(String specName, String className){
        this.specName = specName;
        this.className = className;
    }

    public static WarlordsSpecs fromDatabase(int specID, int classID){
        switch (classID){
            case 0: //Mage
                switch (specID){
                    case 0: return PYROMANCER;
                    case 1: return CRYOMANCER;
                    case 2: return AQUAMANCER;
                    default: return MAGE;
                }
            case 1: //Warrior
                switch (specID){
                    case 0: return BERSERKER;
                    case 1: return DEFENDER;
                    default: return WARRIOR;
                }
            case 2: //Paladin
                switch (specID){
                    case 0: return AVENGER;
                    case 1: return CRUSADER;
                    case 2: return PROTECTOR;
                    default: return PALADIN;
                }
            case 3: //Shaman
                switch (specID){
                    case 0: return THUNDERLORD;
                    case 1: return EARTHWARDEN;
                    default: return SHAMAN;
                }
            default: return UNKNOWN;
        }
    }

    public String getSpecName() {
        return specName;
    }

    public String getClassName() {
        return className;
    }
}
