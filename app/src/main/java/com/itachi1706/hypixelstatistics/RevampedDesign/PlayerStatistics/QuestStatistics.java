package com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.GeneralPlayerStats.QuestName;
import com.itachi1706.hypixelstatistics.GeneralPlayerStats.QuestObjectives;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.PlayerStatistics
 */
public class QuestStatistics {

    /**
     * Parse the Quests statistics
     * @param reply PlayerReply object
     */
    public static ArrayList<PlayerInfoStatistics> parseQuests(PlayerReply reply){
        ArrayList<PlayerInfoStatistics> descArray = new ArrayList<>();
        JsonObject questMain = reply.getPlayer().getAsJsonObject("quests");
        for (Map.Entry<String, JsonElement> entry : questMain.entrySet()){
            //Get each quest name
            ArrayList<PlayerInfoStatistics> qArray = new ArrayList<>();
            //descArray.add(new ResultDescription("<b>" + entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + "</b>", null, false, true));
            if(entry.getValue().getAsJsonObject().has("active")){
                qArray.add(new PlayerInfoStatistics("Status", MinecraftColorCodes.parseColors("§aActive§r")));
                //Get Start Time
                long timings = entry.getValue().getAsJsonObject().get("active").getAsJsonObject().get("started").getAsLong();
                String timeStamp = new SimpleDateFormat(MainStaticVars.DATE_FORMAT, Locale.US).format(new Date(timings));
                qArray.add(new PlayerInfoStatistics("Date Started", timeStamp + "<br />"));

                if (entry.getValue().getAsJsonObject().get("active").getAsJsonObject().has("objectives")){
                    JsonObject arr = entry.getValue().getAsJsonObject().get("active").getAsJsonObject().getAsJsonObject("objectives");
                    if (arr.entrySet().size() > 0) {
                        StringBuilder build = new StringBuilder();
                        build.append("<br />");
                        for (Map.Entry<String, JsonElement> key : arr.entrySet()) {
                            QuestObjectives qObj = QuestObjectives.fromDB(key.getKey());
                            if (qObj == QuestObjectives.UNKNOWN){
                                //Unknown Variable, set Default
                                build.append(key.getKey()).append(": ").append(key.getValue());
                            } else if (qObj.getMaxLimit() == -1){
                                //No Max Limit
                                build.append(qObj.getHumanReadableDesc()).append(": ").append(key.getValue());
                            } else {
                                //Known Variable with max limit
                                build.append(qObj.getHumanReadableDesc()).append(": ").append(key.getValue())
                                        .append("/").append(qObj.getMaxLimit());
                            }
                            build.append("<br />");
                        }
                        qArray.add(new PlayerInfoStatistics("<b>Objectives</b>", build.toString()));
                    }
                }
            } else {
                qArray.add(new PlayerInfoStatistics("Status", MinecraftColorCodes.parseColors("§cInactive§r")));
            }


            //Get number of completion times
            if (entry.getValue().getAsJsonObject().has("completions")){
                int numberOfTimes = entry.getValue().getAsJsonObject().get("completions").getAsJsonArray().size();
                qArray.add(new PlayerInfoStatistics("No of Times Completed", numberOfTimes + ""));
            } else {
                qArray.add(new PlayerInfoStatistics("No of Times Completed", "0"));
            }
            if (qArray.size() > 0){
                StringBuilder msg = new StringBuilder();
                for (PlayerInfoStatistics t : qArray){
                    msg.append(t.getTitle()).append(": ").append(t.getMessage()).append("<br />");
                }
                //Add quest statistics to DB
                QuestName questN = QuestName.fromDB(entry.getKey());
                if (questN == QuestName.UNKNOWN){
                    //This is default unknown quests
                    String tryFormatQuestName = entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
                    descArray.add(new PlayerInfoStatistics(tryFormatQuestName, "Click here to see " + tryFormatQuestName + " quest statistics", msg.toString()));
                } else {
                    descArray.add(new PlayerInfoStatistics(questN.getQuestTitle(), "Click here to see " + questN.getQuestTitle() + " quest statistics", msg.toString()));
                }



            }
        }
        return descArray;
    }
}
