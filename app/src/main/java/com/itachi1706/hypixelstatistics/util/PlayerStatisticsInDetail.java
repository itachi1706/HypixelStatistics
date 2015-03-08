package com.itachi1706.hypixelstatistics.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.Warlords.WarlordsSpecs;
import com.itachi1706.hypixelstatistics.util.Warlords.WeaponAbility;
import com.itachi1706.hypixelstatistics.util.Warlords.WeaponCategory;
import com.itachi1706.hypixelstatistics.util.Warlords.WeaponDamage;
import com.itachi1706.hypixelstatistics.util.Warlords.WeaponName;

/**
 * Created by Kenneth on 8/3/2015, 4:28 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class PlayerStatisticsInDetail {

    //Warlords

    public static String getCurrentEquippedWeaponName(JsonObject currentEquipped, JsonObject weaponInventory){
        //Name format: <rarityColor><unknown> <name> of the <className>
        String weaponID = currentEquipped.getAsString();
        JsonArray weaponInvArr = weaponInventory.getAsJsonArray();
        JsonObject weaponStats = getWeaponStats(weaponID, weaponInvArr);
        if (weaponStats == null){
            return "An error occured";
        }
        WarlordsSpecs className = getClassSpecName(weaponStats.get("spec").getAsJsonObject());
        WeaponCategory rarityColor = getRarityColor(weaponStats.get("category").getAsJsonObject());
        WeaponName name = getWeaponName(weaponStats.get("material").getAsJsonObject());
        return rarityColor.getColorCode() + " " + name.getWeaponName() + " of the " + className.getSpecName();
    }

    public static String getCurrentEquippedWeaponSpecification(JsonObject currentEquipped, JsonObject weaponInventory){
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
        String weaponID = currentEquipped.getAsString();
        JsonArray weaponInvArr = weaponInventory.getAsJsonArray();
        JsonObject weaponStats = getWeaponStats(weaponID, weaponInvArr);
        if (weaponStats == null){
            return "An error occurred";
        }
        WarlordsSpecs className = getClassSpecName(weaponStats.get("spec").getAsJsonObject());
        WeaponCategory rarityColor = getRarityColor(weaponStats.get("category").getAsJsonObject());
        WeaponName name = getWeaponName(weaponStats.get("material").getAsJsonObject());
        WeaponAbility ability = getWeaponAbilityName(weaponStats.getAsJsonObject("ability"), className);
        WeaponDamage damage = getWeaponDamage(weaponStats.getAsJsonObject("damage"));
        int abilityMultiplier = weaponStats.get("abilityBoost").getAsInt();
        int energy = weaponStats.get("energy").getAsInt(), chance = weaponStats.get("chance").getAsInt();
        int multiplier = weaponStats.get("multiplier").getAsInt(), health = weaponStats.get("health").getAsInt();
        int cooldown = weaponStats.get("cooldown").getAsInt(), movement = weaponStats.get("movement").getAsInt();
        boolean crafted = weaponStats.get("crafted").getAsBoolean();
        int maxUpgrades = weaponStats.get("upgradeMax").getAsInt(), upgradeTimes = weaponStats.get("upgradeTimes").getAsInt();

        //Craft the dialog box string :D
        StringBuilder builder = new StringBuilder();
        builder.append("Specifications <br /><br />");
        builder.append("Name: ").append(name.getWeaponName()).append("<br />");
        builder.append("Rarity: ").append(rarityColor.getName()).append("<br /><br />");

        if (damage == WeaponDamage.WIP)
            builder.append("Damage: ").append(damage.getDamageRangeString()).append("<br />");
        else
            builder.append("Damage: ").append(damage.getMinDamage()).append(" - ").append(damage.getMaxDamage()).append("<br />");
        builder.append("Crit Chance: ").append(chance).append("% <br />");
        builder.append("Crit Multiplier: ").append(multiplier).append("% <br /><br />");

        builder.append(className.getClassName()).append(" (").append(className.getSpecName()).append("): <br />");
        builder.append("Increase the damage you deal with ").append(ability.getAbilityName()).append(" by ")
                .append(abilityMultiplier).append("% <br /><br />");

        if (health != 0)
            builder.append("Health: +").append(health).append("<br />");
        if (energy != 0)
            builder.append("Max Energy: +").append(energy).append("<br />");
        if (movement != 0)
            builder.append("Speed: +").append(movement).append("% <br />");
        if (cooldown != 0)
            builder.append("Cooldown Reduction: +").append(cooldown).append("%<br />");

        builder.append("<br />");

        if (crafted) {
            builder.append("Crafted: ").append("True").append("<br />");
        } else {
            builder.append("Crafted: ").append("False").append("<br />");
        }
        builder.append("Max Upgrades: ").append(maxUpgrades).append("<br />");
        builder.append("Times Upgraded: ").append(upgradeTimes).append("<br />");

        return builder.toString();
    }

    private static WeaponDamage getWeaponDamage(JsonObject damage){
        int damageID = damage.getAsInt();
        return WeaponDamage.fromDatabase(damageID);
    }

    private static WeaponAbility getWeaponAbilityName(JsonObject ability, WarlordsSpecs spec){
        int abilityID = ability.getAsInt();
        return WeaponAbility.fromDatabase(spec, abilityID);
    }

    private static WeaponName getWeaponName(JsonObject mat){
        String material = mat.getAsString();
        return WeaponName.fromDatabase(material);
    }

    private static WeaponCategory getRarityColor(JsonObject cat){
        String categoryName = cat.getAsString();
        return WeaponCategory.fromDatabase(categoryName);
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
