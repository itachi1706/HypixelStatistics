package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:49 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords
 */
@SuppressWarnings("unused")
public enum WeaponAbility {
    PYROSKILLONE("Fireball"),
    PYROSKILLTWO("Flame Burst"),
    PYROSKILLTHREE("Time Warp"),
    PYROSKILLFOUR("Arcane Shield"),
    PYROSKILLULTIMATE("Inferno"),
    CRYOSKILLONE("Frostbolt"),
    CRYOSKILLTWO("Freezing Breath"),
    CRYOSKILLTHREE("Time Warp"),
    CRYOSKILLFOUR("Arcane Shield"),
    CRYOSKILLULTIMATE("Ice Barrier"),
    AQUASKILLONE("Water Bolt"),
    AQUASKILLTWO("Water Breath"),
    AQUASKILLTHREE("Time Warp"),
    AQUASKILLFOUR("Arcane Shield"),
    AQUASKILLULTIMATE("Healing Rain"),
    BERSERKERSKILLONE("Wounding Strike"),
    BERSERKERSKILLTWO("Seismic Wave"),
    BERSERKERSKILLTHREE("Ground Slam"),
    BERSERKERSKILLFOUR("Blood Lust"),
    BERSERKERSKILLULTIMATE("Berserk"),
    DEFENDERSKILLONE("Wounding Strike"),
    DEFENDERSKILLTWO("Seismic Wave"),
    DEFENDERSKILLTHREE("Ground Slam"),
    DEFENDERSKILLFOUR("Intervene"),
    DEFENDERSKILLULTIMATE("Last Stand"),
    AVENGERSKILLONE("Avenger's Strike"),
    AVENGERSKILLTWO("Consecrate"),
    AVENGERSKILLTHREE("Light Infusion"),
    AVENGERSKILLFOUR("Holy Radiance"),
    AVENGERSKILLULTIMATE("Avenger's Wrath"),
    CRUSADERSKILLONE("Crusader's Strike"),
    CRUSADERSKILLTWO("Consecrate"),
    CRUSADERSKILLTHREE("Light Infusion"),
    CRUSADERSKILLFOUR("Holy Radiance"),
    CRUSADERSKILLULTIMATE("Inspiring Presence"),
    PROTECTORSKILLONE("Protector's Strike"),
    PROTECTORSKILLTWO("Consecrate"),
    PROTECTORSKILLTHREE("Light Infusion"),
    PROTECTORSKILLFOUR("Holy Radiance"),
    PROTECTORSKILLULTIMATE("Hammer of Light"),
    THUNDERLORDSKILLONE("Lightning Bolt"),
    THUNDERLORDSKILLTWO("Chain Lightning"),
    THUNDERLORDSKILLTHREE("Windfury Weapon"),
    THUNDERLORDSKILLFOUR("Lightning Rod"),
    THUNDERLORDSKILLULTIMATE("Capacitor Totem"),
    EARTHWARDENSKILLONE("Earthen Spike"),
    EARTHWARDENSKILLTWO("Boulder"),
    EARTHWARDENSKILLTHREE("Earthliving Weapon"),
    EARTHWARDENSKILLFOUR("Chain Heal"),
    EARTHWARDENSKILLULTIMATE("Healing Totem"),
    UNKNOWNSKILL("Unknown Skill - Contact Dev"),
    UNKNOWNCLASS("Unknown Class - Contact Dev");

    private static WeaponAbility[] v = values();
    private final String abilityName;

    WeaponAbility(String abilityName){
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
            case THUNDERLORD:
                switch (abilityID){
                    case 0: return THUNDERLORDSKILLONE;
                    case 1: return THUNDERLORDSKILLTWO;
                    case 2: return THUNDERLORDSKILLTHREE;
                    case 3: return THUNDERLORDSKILLFOUR;
                    case 4: return THUNDERLORDSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            case EARTHWARDEN:
                switch (abilityID){
                    case 0: return EARTHWARDENSKILLONE;
                    case 1: return EARTHWARDENSKILLTWO;
                    case 2: return EARTHWARDENSKILLTHREE;
                    case 3: return EARTHWARDENSKILLFOUR;
                    case 4: return EARTHWARDENSKILLULTIMATE;
                    default: return UNKNOWNSKILL;
                }
            default: return UNKNOWNCLASS;
        }
    }
}
