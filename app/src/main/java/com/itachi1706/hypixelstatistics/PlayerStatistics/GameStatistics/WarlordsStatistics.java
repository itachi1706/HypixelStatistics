package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords.DetailedWeaponStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords.WarlordsMounts;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class WarlordsStatistics {

    public static ArrayList<ResultDescription> parseWarlords(JsonObject obj, String localPlayerName){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("magic_dust"))
            descArray.add(new ResultDescription("Magic Dust", obj.get("magic_dust").getAsString()));
        if (obj.has("void_shards"))
            descArray.add(new ResultDescription("Void Shards", obj.get("void_shards").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("win_streak"))
            descArray.add(new ResultDescription("Current Win Streak", obj.get("win_streak").getAsString()));
        if (obj.has("losses"))
            descArray.add(new ResultDescription("Games Lost", obj.get("losses").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Kills", obj.get("kills").getAsString()));
        if (obj.has("assists"))
            descArray.add(new ResultDescription("Assists", obj.get("assists").getAsString()));

        // Overall Kill Death Ratio ((Kills+Assists)/Deaths)
        if (obj.has("assists") && obj.has("kills") && obj.has("deaths")){
            int warAssists = obj.get("assists").getAsInt();
            int warKills = obj.get("kills").getAsInt();
            int warDeaths = obj.get("deaths").getAsInt();
            if (warDeaths == 0)
                warDeaths = 1;  //Done to prevent Divide by Zero Exception
            double warKDA = (double) (warKills + warAssists) / warDeaths;
            warKDA = (double) Math.round(warKDA * 100) / 100;
            descArray.add(new ResultDescription("K/D/A Ratio", warKDA + ""));
        }

        if (obj.has("play_streak"))
            descArray.add(new ResultDescription("Current Play Streak", MinecraftColorCodes.parseColors("§a" + obj.get("play_streak").getAsString() + "§r/§b3§r")));
        if (obj.has("hotkeymode"))
            descArray.add(new ResultDescription("Hot Key Mode Enabled", obj.get("hotkeymode").getAsString()));
        if (obj.has("damage"))
            descArray.add(new ResultDescription("Total Damage Dealt", obj.get("damage").getAsString()));
        if (obj.has("damage_taken"))
            descArray.add(new ResultDescription("Total Damage Taken", obj.get("damage_taken").getAsString()));
        if (obj.has("damage_prevented"))
            descArray.add(new ResultDescription("Total Damage Prevented", obj.get("damage_prevented").getAsString()));
        if (obj.has("heal"))
            descArray.add(new ResultDescription("Total Health Healed", obj.get("heal").getAsString()));
        if (obj.has("life_leeched"))
            descArray.add(new ResultDescription("Total Life Leached", obj.get("life_leeched").getAsString()));
        if (obj.has("broken_inventory"))
            descArray.add(new ResultDescription("Broken Items in Inventory", obj.get("broken_inventory").getAsString()));

        //Repaired
        if (obj.has("repaired"))
            descArray.add(new ResultDescription("Total Items Repaired", obj.get("repaired").getAsString()));
        if (obj.has("repaired_common"))
            descArray.add(new ResultDescription("Total Common Items Repaired", obj.get("repaired_common").getAsString()));
        if (obj.has("repaired_rare"))
            descArray.add(new ResultDescription("Total Rare Items Repaired", obj.get("repaired_rare").getAsString()));
        if (obj.has("repaired_epic"))
            descArray.add(new ResultDescription("Total Epic Items Repaired", obj.get("repaired_epic").getAsString()));
        if (obj.has("repaired_legendary"))
            descArray.add(new ResultDescription("Total Legendary Items Repaired", obj.get("repaired_legendary").getAsString()));

        //Salvaged
        if (obj.has("salvaged_dust_reward"))
            descArray.add(new ResultDescription("Total Magic Dust Salvaged", obj.get("salvaged_dust_reward").getAsString()));
        if (obj.has("salvaged_shards_reward"))
            descArray.add(new ResultDescription("Total Void Shards Salvaged", obj.get("salvaged_shards_reward").getAsString()));
        if (obj.has("salvaged_weapons"))
            descArray.add(new ResultDescription("Total Weapons Salvaged", obj.get("salvaged_weapons").getAsString()));
        if (obj.has("salvaged_weapons_common"))
            descArray.add(new ResultDescription("Total Common Weapons Salvaged", obj.get("salvaged_weapons_common").getAsString()));
        if (obj.has("salvaged_weapons_rare"))
            descArray.add(new ResultDescription("Total Rare Weapons Salvaged", obj.get("salvaged_weapons_rare").getAsString()));
        if (obj.has("salvaged_weapons_epic"))
            descArray.add(new ResultDescription("Total Epic Weapons Salvaged", obj.get("salvaged_weapons_epic").getAsString()));
        if (obj.has("salvaged_weapons_legendary"))
            descArray.add(new ResultDescription("Total Legendary Weapons Salvaged", obj.get("salvaged_weapons_legendary").getAsString()));

        //Crafted
        if (obj.has("crafted"))
            descArray.add(new ResultDescription("Total Items Crafted", obj.get("crafted").getAsString()));
        if (obj.has("crafted_rare"))
            descArray.add(new ResultDescription("Total Rare Items Crafted", obj.get("crafted_rare").getAsString()));
        if (obj.has("crafted_epic"))
            descArray.add(new ResultDescription("Total Epic Items Crafted", obj.get("crafted_epic").getAsString()));
        if (obj.has("crafted_legendary"))
            descArray.add(new ResultDescription("Total Legendary Items Crafted", obj.get("crafted_legendary").getAsString()));

        if (obj.has("chosen_class")) {
            String classChosen = obj.get("chosen_class").getAsString();
            String formattedClass = classChosen.substring(0, 1).toUpperCase() + classChosen.substring(1);
            descArray.add(new ResultDescription("Class Chosen", formattedClass));
            String spec = "An Error Occured!";
            switch (classChosen){
                case "mage": if (obj.has("mage_spec")) {spec = obj.get("mage_spec").getAsString();} else { spec = "error"; } break;
                case "paladin": if (obj.has("paladin_spec")) {spec = obj.get("paladin_spec").getAsString();} else { spec = "error"; } break;
                case "warrior": if (obj.has("warrior_spec")) {spec = obj.get("warrior_spec").getAsString();} else { spec = "error"; } break;
                case "shaman": if (obj.has("shaman_spec")) {spec = obj.get("shaman_spec").getAsString();} else { spec = "error"; } break;
            }
            descArray.add(new ResultDescription(formattedClass + " Spec Chosen", spec.substring(0,1).toUpperCase() + spec.substring(1)));
        }
        if (obj.has("selected_mount")){
            WarlordsMounts mountSelected = WarlordsMounts.fromDatabase(obj.get("selected_mount").getAsString());
            if (mountSelected == WarlordsMounts.UNKNOWN)
                descArray.add(new ResultDescription("Selected Mount", obj.get("selected_mount").getAsString()));
            else
                descArray.add(new ResultDescription("Selected Mount", MinecraftColorCodes.parseColors(mountSelected.getName())));
        }

        if (obj.has("current_weapon") && obj.has("weapon_inventory")){
            descArray.add(new ResultDescription("Weapon Currently Equipped",
                    MinecraftColorCodes.parseColors(DetailedWeaponStatistics.getCurrentEquippedWeaponName(obj.get("current_weapon").getAsString(),
                            obj.getAsJsonArray("weapon_inventory")) + "§r <br />Click for detailed statistics of the weapon"),true,
                    MinecraftColorCodes.parseColors(DetailedWeaponStatistics.getCurrentEquippedWeaponSpecification(obj.get("current_weapon").getAsString(),
                            obj.getAsJsonArray("weapon_inventory"), localPlayerName))));
        }

        //Individual Classes/Specs Statistics
        //Mage
        descArray = parseIndividualWarlordsStats(obj, "mage", "Mage Class", descArray);
        //Pyromancer
        descArray = parseIndividualWarlordsStats(obj, "pyromancer", "Pyromancer", descArray);
        //Cryomancer
        descArray = parseIndividualWarlordsStats(obj, "cryomancer", "Cryomancer", descArray);
        //Aquamancer
        descArray = parseIndividualWarlordsStats(obj, "aquamancer", "Aquamancer", descArray);
        //Warrior
        descArray = parseIndividualWarlordsStats(obj, "warrior", "Warrior Class", descArray);
        //Berserker
        descArray = parseIndividualWarlordsStats(obj, "berserker", "Berserker", descArray);
        //Defender
        descArray = parseIndividualWarlordsStats(obj, "defender", "Defender", descArray);
        //Paladin
        descArray = parseIndividualWarlordsStats(obj, "paladin", "Paladin Class", descArray);
        //Avenger
        descArray = parseIndividualWarlordsStats(obj, "avenger", "Avenger", descArray);
        //Crusader
        descArray = parseIndividualWarlordsStats(obj, "crusader", "Crusader", descArray);
        //Protector
        descArray = parseIndividualWarlordsStats(obj, "protector", "Protector", descArray);
        //Shaman
        descArray = parseIndividualWarlordsStats(obj, "shaman", "Shaman Class", descArray);
        //Thunderlord
        descArray = parseIndividualWarlordsStats(obj, "thunderlord", "Thunderlord", descArray);
        //Earthwarden
        descArray = parseIndividualWarlordsStats(obj, "earthwarden", "Earthwarden", descArray);

        //Priviledged Info
        if (MainStaticVars.isStaff || MainStaticVars.isCreator){
            if (obj.has("afk_warned"))
                descArray.add(new ResultDescription("Times warned for AFK", obj.get("afk_warned").getAsString()));
            if (obj.has("penalty"))
                descArray.add(new ResultDescription("Times Penalized", obj.get("penalty").getAsString()));
        }
        return descArray;
    }


    //damage_<>,damage_prevented_<>,losses_<>,<>_plays
    private static ArrayList<ResultDescription> parseIndividualWarlordsStats(JsonObject obj, String className, String title, ArrayList<ResultDescription> descArray){
        ArrayList<ResultDescription> classArray = new ArrayList<>();
        if (obj.has("damage_" + className))
            classArray.add(new ResultDescription("Damage Dealt", obj.get("damage_" + className).getAsString()));
        if (obj.has("damage_prevented_" + className))
            classArray.add(new ResultDescription("Damage Prevented", obj.get("damage_prevented_" + className).getAsString()));
        if (obj.has("losses_" + className))
            classArray.add(new ResultDescription("Games Lost", obj.get("losses_" + className).getAsString()));
        if (obj.has("wins_" + className))
            classArray.add(new ResultDescription("Games Won", obj.get("wins_" + className).getAsString()));
        if (obj.has(className + "_plays"))
            classArray.add(new ResultDescription("Times Played as " + className, obj.get(className + "_plays").getAsString()));

        if (classArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : classArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription(title + " Statistics", "Click here to view " + title + " Statistics", true, msg.toString()));
            //} else {
            //    descArray.add(new ResultDescription(title + " Statistics", "Click here to view " + title + " Statistics", true, "This player does not have any statistics for this class/spec yet!"));
        }
        return descArray;
    }
}
