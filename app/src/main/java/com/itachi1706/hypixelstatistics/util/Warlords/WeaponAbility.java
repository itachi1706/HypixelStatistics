package com.itachi1706.hypixelstatistics.util.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:49 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util.Warlords
 */
@SuppressWarnings("unused")
public enum WeaponAbility {
    PYROSKILLONE("Fireball"),
    PYROSKILLTWO("Flame Burst"),
    PYROSKILLTHREE("Time Warp"),
    PYROSKILLFOUR("Arcane Shield"),
    PYROSKILLULTIMATE("Inferno"),
    CRYOSKILLONE(""),
    CRYOSKILLTWO(""),
    CRYOSKILLTHREE(""),
    CRYOSKILLFOUR(""),
    CRYOSKILLULTIMATE(""),
    AQUASKILLONE(""),
    AQUASKILLTWO(""),
    AQUASKILLTHREE(""),
    AQUASKILLFOUR(""),
    AQUASKILLULTIMATE(""),
    BERSERKERSKILLONE(""),
    BERSERKERSKILLTWO(""),
    BERSERKERSKILLTHREE(""),
    BERSERKERSKILLFOUR(""),
    BERSERKERSKILLULTIMATE(""),
    DEFENDERSKILLONE(""),
    DEFENDERSKILLTWO(""),
    DEFENDERSKILLTHREE(""),
    DEFENDERSKILLFOUR(""),
    DEFENDERSKILLULTIMATE(""),
    AVENGERSKILLONE(""),
    AVENGERSKILLTWO(""),
    AVENGERSKILLTHREE(""),
    AVENGERSKILLFOUR(""),
    AVENGERSKILLULTIMATE(""),
    CRUSADERSKILLONE(""),
    CRUSADERSKILLTWO(""),
    CRUSADERSKILLTHREE(""),
    CRUSADERSKILLFOUR(""),
    CRUSADERSKILLULTIMATE(""),
    PROTECTORSKILLONE(""),
    PROTECTORSKILLTWO(""),
    PROTECTORSKILLTHREE(""),
    PROTECTORSKILLFOUR(""),
    PROTECTORSKILLULTIMATE(""),
    UNKNOWNSKILL("Unknown Skill - Contact Dev"),
    UNKNOWNCLASS("Unknown Class - Contact Dev");

    private static WeaponAbility[] v = values();
    private final String abilityName;

    private WeaponAbility(String abilityName){
        this.abilityName = abilityName;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public static WeaponAbility fromDatabase(WarlordsSpecs specName, int abilityID){
        switch (specName){
            case PYROMANCER:
                switch (abilityID){
                    case 0: return PYROSKILLONE;
                    case 1: return PYROSKILLTWO;
                    case 2: return PYROSKILLTHREE;
                    case 3: return PYROSKILLFOUR;
                    case 4: return PYROSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case AQUAMANCER:
                switch (abilityID){
                    case 0: return AQUASKILLONE;
                    case 1: return AQUASKILLTWO;
                    case 2: return AQUASKILLTHREE;
                    case 3: return AQUASKILLFOUR;
                    case 4: return AQUASKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case CRYOMANCER:
                switch (abilityID){
                    case 0: return CRYOSKILLONE;
                    case 1: return CRYOSKILLTWO;
                    case 2: return CRYOSKILLTHREE;
                    case 3: return CRYOSKILLFOUR;
                    case 4: return CRYOSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case BERSERKER:
                switch (abilityID){
                    case 0: return BERSERKERSKILLONE;
                    case 1: return BERSERKERSKILLTWO;
                    case 2: return BERSERKERSKILLTHREE;
                    case 3: return BERSERKERSKILLFOUR;
                    case 4: return BERSERKERSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case DEFENDER:
                switch (abilityID){
                    case 0: return DEFENDERSKILLONE;
                    case 1: return DEFENDERSKILLTWO;
                    case 2: return DEFENDERSKILLTHREE;
                    case 3: return DEFENDERSKILLFOUR;
                    case 4: return DEFENDERSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case AVENGER:
                switch (abilityID){
                    case 0: return AVENGERSKILLONE;
                    case 1: return AVENGERSKILLTWO;
                    case 2: return AVENGERSKILLTHREE;
                    case 3: return AVENGERSKILLFOUR;
                    case 4: return AVENGERSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case CRUSADER:
                switch (abilityID){
                    case 0: return CRUSADERSKILLONE;
                    case 1: return CRUSADERSKILLTWO;
                    case 2: return CRUSADERSKILLTHREE;
                    case 3: return CRUSADERSKILLFOUR;
                    case 4: return CRUSADERSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case PROTECTOR:
                switch (abilityID){
                    case 0: return PROTECTORSKILLONE;
                    case 1: return PROTECTORSKILLTWO;
                    case 2: return PROTECTORSKILLTHREE;
                    case 3: return PROTECTORSKILLFOUR;
                    case 4: return PROTECTORSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            default: return UNKNOWNCLASS;
        }
    }
}
