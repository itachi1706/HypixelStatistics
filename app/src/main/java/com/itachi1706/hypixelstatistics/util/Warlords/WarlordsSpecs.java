package com.itachi1706.hypixelstatistics.util.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 6:00 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.Warlords
 */
@SuppressWarnings("unused")
public enum WarlordsSpecs {
    WARRIOR("Unknown Warrior Spec", "Warrior"),
    PALADIN("Unknown Paladin Spec", "Paladin"),
    MAGE("Unknown Mage Spec", "Mage"),
    UNKNOWN("Unknown Class", "???"),
    PYROMANCER("Pyromancer", "Mage"),
    CRYOMANCER("Cryomancer", "Mage"),
    AQUAMANCER("Aquamancer", "Mage"),
    BERSERKER("Berserker", "Warrior"),
    DEFENDER("Defender", "Warrior"),
    AVENGER("Avenger", "Paladin"),
    CRUSADER("Crusader", "Paladin"),
    PROTECTOR("Protector", "Paladin");

    private static WarlordsSpecs[] v = values();
    private final String specName;
    private final String className;

    private WarlordsSpecs(String specName, String className){
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
