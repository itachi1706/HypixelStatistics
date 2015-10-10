package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatistics;

import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords.DetailedWeaponStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.Warlords.WarlordsMounts;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatistics
 */
public class WarlordsStatistics {

    public static ArrayList<PlayerInfoStatistics> parseWarlords(JsonObject obj, String localPlayerName){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        if (obj.has("coins"))
            descArray.add(new PlayerInfoStatistics("Coins", obj.get("coins").getAsString()));
        if (obj.has("magic_dust"))
            descArray.add(new PlayerInfoStatistics("Magic Dust", obj.get("magic_dust").getAsString()));
        if (obj.has("void_shards"))
            descArray.add(new PlayerInfoStatistics("Void Shards", obj.get("void_shards").getAsString()));
        if (obj.has("wins"))
            descArray.add(new PlayerInfoStatistics("Wins", obj.get("wins").getAsString()));
        if (obj.has("win_streak"))
            descArray.add(new PlayerInfoStatistics("Current Win Streak", obj.get("win_streak").getAsString()));
        if (obj.has("losses"))
            descArray.add(new PlayerInfoStatistics("Games Lost", obj.get("losses").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new PlayerInfoStatistics("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new PlayerInfoStatistics("Kills", obj.get("kills").getAsString()));
        if (obj.has("assists"))
            descArray.add(new PlayerInfoStatistics("Assists", obj.get("assists").getAsString()));

        // Overall Kill Death Ratio ((Kills+Assists)/Deaths)
        if (obj.has("assists") && obj.has("kills") && obj.has("deaths")){
            int warAssists = obj.get("assists").getAsInt();
            int warKills = obj.get("kills").getAsInt();
            int warDeaths = obj.get("deaths").getAsInt();
            if (warDeaths == 0)
                warDeaths = 1;  //Done to prevent Divide by Zero Exception
            double warKDA = (double) (warKills + warAssists) / warDeaths;
            warKDA = (double) Math.round(warKDA * 100) / 100;
            descArray.add(new PlayerInfoStatistics("K/D/A Ratio", warKDA + ""));
        }

        if (obj.has("play_streak"))
            descArray.add(new PlayerInfoStatistics("Current Play Streak", MinecraftColorCodes.parseColors("§a" + obj.get("play_streak").getAsString() + "§r/§b3§r")));
        if (obj.has("hotkeymode"))
            descArray.add(new PlayerInfoStatistics("Hot Key Mode Enabled", obj.get("hotkeymode").getAsString()));
        if (obj.has("damage"))
            descArray.add(new PlayerInfoStatistics("Total Damage Dealt", obj.get("damage").getAsString()));
        if (obj.has("damage_taken"))
            descArray.add(new PlayerInfoStatistics("Total Damage Taken", obj.get("damage_taken").getAsString()));
        if (obj.has("damage_prevented"))
            descArray.add(new PlayerInfoStatistics("Total Damage Prevented", obj.get("damage_prevented").getAsString()));
        if (obj.has("heal"))
            descArray.add(new PlayerInfoStatistics("Total Health Healed", obj.get("heal").getAsString()));
        if (obj.has("life_leeched"))
            descArray.add(new PlayerInfoStatistics("Total Life Leached", obj.get("life_leeched").getAsString()));
        if (obj.has("broken_inventory"))
            descArray.add(new PlayerInfoStatistics("Broken Items in Inventory", obj.get("broken_inventory").getAsString()));

        //Repaired
        if (obj.has("repaired"))
            descArray.add(new PlayerInfoStatistics("Total Items Repaired", obj.get("repaired").getAsString()));
        if (obj.has("repaired_common"))
            descArray.add(new PlayerInfoStatistics("Total Common Items Repaired", obj.get("repaired_common").getAsString()));
        if (obj.has("repaired_rare"))
            descArray.add(new PlayerInfoStatistics("Total Rare Items Repaired", obj.get("repaired_rare").getAsString()));
        if (obj.has("repaired_epic"))
            descArray.add(new PlayerInfoStatistics("Total Epic Items Repaired", obj.get("repaired_epic").getAsString()));
        if (obj.has("repaired_legendary"))
            descArray.add(new PlayerInfoStatistics("Total Legendary Items Repaired", obj.get("repaired_legendary").getAsString()));

        //Salvaged
        if (obj.has("salvaged_dust_reward"))
            descArray.add(new PlayerInfoStatistics("Total Magic Dust Salvaged", obj.get("salvaged_dust_reward").getAsString()));
        if (obj.has("salvaged_shards_reward"))
            descArray.add(new PlayerInfoStatistics("Total Void Shards Salvaged", obj.get("salvaged_shards_reward").getAsString()));
        if (obj.has("salvaged_weapons"))
            descArray.add(new PlayerInfoStatistics("Total Weapons Salvaged", obj.get("salvaged_weapons").getAsString()));
        if (obj.has("salvaged_weapons_common"))
            descArray.add(new PlayerInfoStatistics("Total Common Weapons Salvaged", obj.get("salvaged_weapons_common").getAsString()));
        if (obj.has("salvaged_weapons_rare"))
            descArray.add(new PlayerInfoStatistics("Total Rare Weapons Salvaged", obj.get("salvaged_weapons_rare").getAsString()));
        if (obj.has("salvaged_weapons_epic"))
            descArray.add(new PlayerInfoStatistics("Total Epic Weapons Salvaged", obj.get("salvaged_weapons_epic").getAsString()));
        if (obj.has("salvaged_weapons_legendary"))
            descArray.add(new PlayerInfoStatistics("Total Legendary Weapons Salvaged", obj.get("salvaged_weapons_legendary").getAsString()));

        //Crafted
        if (obj.has("crafted"))
            descArray.add(new PlayerInfoStatistics("Total Items Crafted", obj.get("crafted").getAsString()));
        if (obj.has("crafted_rare"))
            descArray.add(new PlayerInfoStatistics("Total Rare Items Crafted", obj.get("crafted_rare").getAsString()));
        if (obj.has("crafted_epic"))
            descArray.add(new PlayerInfoStatistics("Total Epic Items Crafted", obj.get("crafted_epic").getAsString()));
        if (obj.has("crafted_legendary"))
            descArray.add(new PlayerInfoStatistics("Total Legendary Items Crafted", obj.get("crafted_legendary").getAsString()));

        if (obj.has("chosen_class")) {
            String classChosen = obj.get("chosen_class").getAsString();
            String formattedClass = classChosen.substring(0, 1).toUpperCase() + classChosen.substring(1);
            descArray.add(new PlayerInfoStatistics("Class Chosen", formattedClass));
            String spec = "An Error Occured!";
            switch (classChosen){
                case "mage": if (obj.has("mage_spec")) {spec = obj.get("mage_spec").getAsString();} else { spec = "error"; } break;
                case "paladin": if (obj.has("paladin_spec")) {spec = obj.get("paladin_spec").getAsString();} else { spec = "error"; } break;
                case "warrior": if (obj.has("warrior_spec")) {spec = obj.get("warrior_spec").getAsString();} else { spec = "error"; } break;
                case "shaman": if (obj.has("shaman_spec")) {spec = obj.get("shaman_spec").getAsString();} else { spec = "error"; } break;
            }
            descArray.add(new PlayerInfoStatistics(formattedClass + " Spec Chosen", spec.substring(0,1).toUpperCase() + spec.substring(1)));
        }
        if (obj.has("selected_mount")){
            WarlordsMounts mountSelected = WarlordsMounts.fromDatabase(obj.get("selected_mount").getAsString());
            if (mountSelected == WarlordsMounts.UNKNOWN)
                descArray.add(new PlayerInfoStatistics("Selected Mount", obj.get("selected_mount").getAsString()));
            else
                descArray.add(new PlayerInfoStatistics("Selected Mount", MinecraftColorCodes.parseColors(mountSelected.getName())));
        }

        if (obj.has("current_weapon") && obj.has("weapon_inventory")){
            descArray.add(new PlayerInfoStatistics("Weapon Currently Equipped",
                    MinecraftColorCodes.parseColors(DetailedWeaponStatistics.getCurrentEquippedWeaponName(obj.get("current_weapon").getAsString(),
                            obj.getAsJsonArray("weapon_inventory")) + "§r <br />Click for detailed statistics of the weapon"),
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
                descArray.add(new PlayerInfoStatistics("Times warned for AFK", obj.get("afk_warned").getAsString()));
            if (obj.has("penalty"))
                descArray.add(new PlayerInfoStatistics("Times Penalized", obj.get("penalty").getAsString()));
        }
        return descArray;
    }


    //damage_<>,damage_prevented_<>,losses_<>,<>_plays
    private static ArrayList<PlayerInfoStatistics> parseIndividualWarlordsStats(JsonObject obj, String className, String title, ArrayList<PlayerInfoStatistics> descArray){
        ArrayList<PlayerInfoStatistics> classArray = new ArrayList<>();
        if (obj.has("damage_" + className))
            classArray.add(new PlayerInfoStatistics("Damage Dealt", obj.get("damage_" + className).getAsString()));
        if (obj.has("damage_prevented_" + className))
            classArray.add(new PlayerInfoStatistics("Damage Prevented", obj.get("damage_prevented_" + className).getAsString()));
        if (obj.has("losses_" + className))
            classArray.add(new PlayerInfoStatistics("Games Lost", obj.get("losses_" + className).getAsString()));
        if (obj.has("wins_" + className))
            classArray.add(new PlayerInfoStatistics("Games Won", obj.get("wins_" + className).getAsString()));
        if (obj.has(className + "_plays"))
            classArray.add(new PlayerInfoStatistics("Times Played as " + className, obj.get(className + "_plays").getAsString()));

        if (classArray.size() > 0){
            descArray.add(new PlayerInfoStatistics(title + " Statistics", "Click here to view " + title + " Statistics", StatisticsHelper.generateDialogStatisticsString(classArray)));
            //} else {
            //    descArray.add(new PlayerInfoStatistics(title + " Statistics", "Click here to view " + title + " Statistics", "This player does not have any statistics for this class/spec yet!"));
        }
        return descArray;
    }
}
