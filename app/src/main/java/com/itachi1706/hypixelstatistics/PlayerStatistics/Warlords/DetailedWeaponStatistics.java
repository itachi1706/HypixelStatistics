package com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Kenneth on 8/3/2015, 4:28 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
@Deprecated
public class DetailedWeaponStatistics {

    //Warlords

    public static String getCurrentEquippedWeaponName(String currentEquipped, JsonArray weaponInventory){
        //Name format: <rarityColor><unknown> <name> of the <className>
        JsonObject weaponStats = getWeaponStats(currentEquipped, weaponInventory);
        if (weaponStats == null){
            return "An error occured";
        }
        WarlordsSpecs className = getClassSpecName(weaponStats.get("spec").getAsJsonObject());
        WeaponCategory rarityColor = getRarityColor(weaponStats.get("category").getAsString());
        WeaponName name = getWeaponName(weaponStats.get("material").getAsString());
        if (name == WeaponName.UNKNOWN)
            return "(UNKNOWN) " + rarityColor.getColorCode() + " " + weaponStats.get("material").getAsString() + " of the " + className.getSpecName();
        return rarityColor.getColorCode() + " " + name.getWeaponName() + " of the " + className.getSpecName();
    }

    public static String getCurrentEquippedWeaponSpecification(String currentEquipped, JsonArray weaponInventory, String player){
        /*
        Spec
        ====
        Ability - Ability Name,
        abilityboost - Boost by how many %,
        damage - Damage Range ID,
        energy - Energy Boost,
        chance - Crit Chance in %,
        multiplier - Crit Multiplier in %,
        health - Health Boost,
        cooldown - Cooldown Reduction in %,
        movement - Speed Increase in %,
        crafted - is it crafted or not,
        upgradedMax - no of times it can be upgraded,
        upgradedTimes - times upgraded
         */

        //Get the specs
        JsonObject weaponStats = getWeaponStats(currentEquipped, weaponInventory);
        if (weaponStats == null){
            return "An error occurred";
        }
        WarlordsSpecs className = getClassSpecName(weaponStats.get("spec").getAsJsonObject());
        WeaponCategory rarityColor = getRarityColor(weaponStats.get("category").getAsString());
        WeaponName name = getWeaponName(weaponStats.get("material").getAsString());
        WeaponAbility ability = getWeaponAbilityName(weaponStats.get("ability").getAsInt(), className);
        WeaponDamage damage = getWeaponDamage(weaponStats.get("damage").getAsInt());
        int abilityMultiplier = weaponStats.get("abilityBoost").getAsInt();
        int energy = weaponStats.get("energy").getAsInt(), chance = weaponStats.get("chance").getAsInt();
        int multiplier = weaponStats.get("multiplier").getAsInt(), health = weaponStats.get("health").getAsInt();
        int cooldown = weaponStats.get("cooldown").getAsInt(), movement = weaponStats.get("movement").getAsInt();
        boolean crafted = false;
        if (weaponStats.has("crafted"))
            crafted = weaponStats.get("crafted").getAsBoolean();
        int maxUpgrades, upgradeTimes;
        if (weaponStats.has("upgradeMax"))
            maxUpgrades = weaponStats.get("upgradeMax").getAsInt();
        else
            maxUpgrades = 0;
        if (weaponStats.has("upgradeTimes"))
            upgradeTimes = weaponStats.get("upgradeTimes").getAsInt();
        else
            upgradeTimes = 0;

        //Craft the dialog box string :D
        StringBuilder builder = new StringBuilder();
        if (name == WeaponName.UNKNOWN)
            builder.append("Name: §b(Unknown) ").append(weaponStats.get("material").getAsString()).append(" of the ").append(className.getSpecName()).append("§r<br />");
        else
            builder.append("Name: §b").append(name.getWeaponName()).append(" of the ").append(className.getSpecName()).append("§r<br />");
        builder.append("Rarity: ").append(rarityColor.getColorCode()).append(rarityColor.getName()).append("§r<br /><br />");

        builder.append("Damage ID: §4").append(weaponStats.get("damage").getAsInt()).append("§r <br />");
        if (damage == WeaponDamage.WIP)
            builder.append("Base Damage: §4").append(damage.getDamageRangeString()).append(" §r<br />");
        else
            builder.append("Base Damage: §c").append(damage.getMinDamage()).append("§r - §c").append(damage.getMaxDamage()).append("§r<br />");
        builder.append("Crit Chance: §c").append(chance).append("%§r <br />");
        builder.append("Crit Multiplier: §c").append(multiplier).append("%§r <br /><br />");

        builder.append("§a").append(className.getClassName()).append(" (").append(className.getSpecName()).append("): <br />");
        builder.append("Increase the damage you deal with ").append(ability.getAbilityName()).append(" by §r§c")
                .append(abilityMultiplier).append("%§r <br /><br />");

        if (health != 0)
            builder.append("Health: §a+").append(health).append("§r<br />");
        if (energy != 0)
            builder.append("Max Energy: §a+").append(energy).append("§r<br />");
        if (movement != 0)
            builder.append("Speed: §a+").append(movement).append("%§r <br />");
        if (cooldown != 0)
            builder.append("Cooldown Reduction: §a+").append(cooldown).append("%§r<br />");

        builder.append("<br />");

        if (crafted) {
            builder.append("§3Crafted§r <br />");
        } else {
            builder.append("§5Repaired§r <br />");
        }
        if (rarityColor == WeaponCategory.EPIC) {
            //Magic Forged
            builder.append("§bMagic Forged [").append(upgradeTimes).append("/").append(maxUpgrades).append("]§r <br />");
        } else if (rarityColor == WeaponCategory.LEGENDARY) {
            //Void Forged
            builder.append("§dVoid Forged [").append(upgradeTimes).append("/").append(maxUpgrades).append("]§r <br />");
        }
            //builder.append("Max Upgrades: §d").append(maxUpgrades).append("§r<br />");
            //builder.append("Times Upgraded: §d").append(upgradeTimes).append("§r<br />");

        String weaponString = "http://www.plancke.nl/hypixel/generators/warlords/weapon/";
        weaponString += player + "/";
        weaponString += currentEquipped + ".png";
        builder.append("<br />");
        builder.append("<a href='").append(weaponString).append("'>View weapon on Plancke.nl</a>");
        return builder.toString();
    }

    private static WeaponDamage getWeaponDamage(int damage){
        return WeaponDamage.fromDatabase(damage);
    }

    private static WeaponAbility getWeaponAbilityName(int ability, WarlordsSpecs spec){
        return WeaponAbility.fromDatabase(spec, ability);
    }

    private static WeaponName getWeaponName(String mat){
        return WeaponName.fromDatabase(mat);
    }

    private static WeaponCategory getRarityColor(String cat){
        return WeaponCategory.fromDatabase(cat);
    }

    private static WarlordsSpecs getClassSpecName(JsonObject spec){
        int specID = spec.get("spec").getAsInt();
        int classID = spec.get("playerClass").getAsInt();
        return WarlordsSpecs.fromDatabase(specID, classID);
    }

    private static JsonObject getWeaponStats(String weaponID, JsonArray weaponInventory){
        for (JsonElement e : weaponInventory){
            JsonObject weapon = e.getAsJsonObject();
            if (weapon.get("id").getAsString().equals(weaponID)){
                return weapon;
            }
        }
        return null;
    }
}
