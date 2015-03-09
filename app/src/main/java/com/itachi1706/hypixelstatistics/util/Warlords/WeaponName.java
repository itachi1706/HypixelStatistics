package com.itachi1706.hypixelstatistics.util.Warlords;

/**
 * Created by Kenneth on 8/3/2015, 5:03 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
@SuppressWarnings("unused")
public enum WeaponName {
    WOODEN_AXE("Steel Sword", WeaponCategory.COMMON),
    STONE_AXE("Training Sword", WeaponCategory.COMMON),
    GOLDEN_HOE("Hatchet", WeaponCategory.COMMON),
    IRON_SHOVEL("Hammer", WeaponCategory.COMMON),
    STONE_PICKAXE("Walking Stick", WeaponCategory.COMMON),
    SALMON("Scimitar", WeaponCategory.COMMON),
    ROTTEN_FLESH("Pike", WeaponCategory.COMMON),
    MUTTON("Claws", WeaponCategory.COMMON),
    PUMPKIN_PIE("Orc Axe", WeaponCategory.COMMON),
    RABBIT_STEW("Bludgeon", WeaponCategory.COMMON),
    IRON_AXE("Demonblade", WeaponCategory.RARE),
    GOLD_AXE("Venomstrike", WeaponCategory.RARE),
    DIAMOND_HOE("Gem Axe", WeaponCategory.RARE),
    GOLDEN_SHOVEL("Stone Mallet", WeaponCategory.RARE),
    IRON_PICKAXE("World Tree Branch", WeaponCategory.RARE),
    PUFFERFISH("Golden Gladius", WeaponCategory.RARE),
    POTATO("Halberd", WeaponCategory.RARE),
    PORKCHOP("Mandibles", WeaponCategory.RARE),
    COOKED_COD("Doubleaxe", WeaponCategory.RARE),
    COOKED_RABBIT("Cudgel", WeaponCategory.RARE),
    DIAMOND_AXE("Diamondspark", WeaponCategory.EPIC),
    WOODEN_HOE("Zweireaper", WeaponCategory.EPIC),
    STONE_HOE("Runeblade", WeaponCategory.EPIC),
    IRON_HOE("Elven Greatsword", WeaponCategory.EPIC),
    WOODEN_SHOVEL("Nomegusta", WeaponCategory.EPIC),
    DIAMOND_SHOVEL("Gemcrusher", WeaponCategory.EPIC),
    GOLDEN_PICKAXE("Flameweaver", WeaponCategory.EPIC),
    CLOWNFISH("Magmasword", WeaponCategory.EPIC),
    MELON("Divine Reach", WeaponCategory.EPIC),
    STRING("Hammer of Light", WeaponCategory.EPIC),
    CHICKEN("Nethersteel Katana", WeaponCategory.EPIC),
    BEEF("Katar", WeaponCategory.EPIC),
    BREAD("Runic Axe", WeaponCategory.EPIC),
    MUSHROOM_STEW("Lunar Relic", WeaponCategory.EPIC),
    COOKED_CHICKEN("Tenderizer", WeaponCategory.EPIC),
    STONE_SHOVEL("Drakefang", WeaponCategory.LEGENDARY),
    WOODEN_PICKAXE("Abbadon", WeaponCategory.LEGENDARY),
    DIAMOND_PICKAXE("Void Twig", WeaponCategory.LEGENDARY),
    COD("Frostbite", WeaponCategory.LEGENDARY),
    POISONOUS_POTATO("Ruby Thorn", WeaponCategory.LEGENDARY),
    APPLE("Enderfist", WeaponCategory.LEGENDARY),
    BAKED_POTATO("Broccomace", WeaponCategory.LEGENDARY),
    COOKED_SALMON("Felflame Blade", WeaponCategory.LEGENDARY),
    COOKED_MUTTON("Amaranth", WeaponCategory.LEGENDARY),
    COOKED_BEEF("Armblade", WeaponCategory.LEGENDARY),
    COOKED_PORKCHOP("Gemini", WeaponCategory.LEGENDARY),
    GOLDEN_CARROT("Void Edge", WeaponCategory.LEGENDARY),
    UNKNOWN("Unknown Weapon", WeaponCategory.DEFAULT);

    private final String weaponName;
    private final WeaponCategory category;

    private WeaponName(String weapon, WeaponCategory category){
        this.weaponName = weapon;
        this.category = category;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public WeaponCategory getCategory() {
        return category;
    }

    public static WeaponName fromDatabase(String material){
        switch (material){
            //Common
            case "WOODEN_AXE": return WOODEN_AXE;
            case "WOOD_AXE": return WOODEN_AXE;
            case "STONE_AXE": return STONE_AXE;
            case "GOLDEN_HOE": return GOLDEN_HOE;
            case "IRON_SHOVEL": return IRON_SHOVEL;
            case "STONE_PICKAXE": return STONE_PICKAXE;
            case "SALMON": return SALMON;
            case "ROTTEN_FLESH": return ROTTEN_FLESH;
            case "MUTTON": return MUTTON;
            case "PUMPKIN_PIE": return PUMPKIN_PIE;
            case "RABBIT_STEW": return RABBIT_STEW;
            //Rare
            case "IRON_AXE": return IRON_AXE;
            case "GOLD_AXE": return GOLD_AXE;
            case "DIAMOND_HOE": return DIAMOND_HOE;
            case "GOLDEN_SHOVEL": return GOLDEN_SHOVEL;
            case "IRON_PICKAXE": return IRON_PICKAXE;
            case "PUFFERFISH": return PUFFERFISH;
            case "POTATO": return POTATO;
            case "PORKCHOP": return PORKCHOP;
            case "COOKED_COD": return COOKED_COD;
            case "COOKED_RABBIT": return COOKED_RABBIT;
            //Epic
            case "DIAMOND_AXE": return DIAMOND_AXE;
            case "WOODEN_HOE": return WOODEN_HOE;
            case "WOOD_HOE": return WOODEN_HOE;
            case "STONE_HOE": return STONE_HOE;
            case "IRON_HOE": return IRON_HOE;
            case "WOODEN_SHOVEL": return WOODEN_SHOVEL;
            case "WOOD_SPADE": return WOODEN_SHOVEL;
            case "DIAMOND_SHOVEL": return DIAMOND_SHOVEL;
            case "GOLDEN_PICKAXE": return GOLDEN_PICKAXE;
            case "CLOWNFISH": return CLOWNFISH;
            case "MELON": return MELON;
            case "STRING": return STRING;
            case "CHICKEN": return CHICKEN;
            case "BEEF": return BEEF;
            case "BREAD": return BREAD;
            case "MUSHROOM_STEW": return MUSHROOM_STEW;
            case "COOKED_CHICKEN": return COOKED_CHICKEN;
                //Legendary
            case "STONE_SHOVEL": return STONE_SHOVEL;
            case "WOODEN_PICKAXE": return WOODEN_PICKAXE;
            case "WOOD_PICKAXE": return WOODEN_PICKAXE;
            case "DIAMOND_PIXKAXE": return DIAMOND_PICKAXE;
            case "COD": return COD;
            case "POISONOUS_POTATO": return POISONOUS_POTATO;
            case "APPLE": return APPLE;
            case "BAKED_POTATO": return BAKED_POTATO;
            case "COOKED_SALMON": return COOKED_SALMON;
            case "COOKED_MUTTON": return COOKED_MUTTON;
            case "COOKED_BEEF": return COOKED_BEEF;
            case "COOKED_PORKCHOP": return COOKED_PORKCHOP;
            case "GOLDEN_CARROT": return GOLDEN_CARROT;
        }
        return UNKNOWN;
    }
}
