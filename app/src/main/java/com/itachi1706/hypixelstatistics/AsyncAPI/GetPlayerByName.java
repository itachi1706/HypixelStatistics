package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.OldPlayerInfoActivity;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.ResultDescListAdapter;
import com.itachi1706.hypixelstatistics.util.ResultDescription;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PlayerReply;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetPlayerByName extends AsyncTask<String,Void,String> {

    TextView debug, result;
    ListView details;
    Context mContext;
    Exception except = null;
    ImageView ivHead;
    ProgressDialog progress;
    ProgressBar pro;

    ArrayList<ResultDescription> resultArray;

    public GetPlayerByName(TextView resultView, TextView debugView, ListView general, ImageView head, ProgressDialog prog, ProgressBar header, Context context){
        debug = debugView;
        result = resultView;
        mContext = context;
        details = general;
        ivHead = head;
        progress = prog;
        pro = header;
    }

    @Override
    protected String doInBackground(String... playerName) {
        String url = HypixelAPI.API_BASE_URL + "player?key=" + mContext.getResources().getString(R.string.hypixel_api_key) + "&name=" + playerName[0];
        String tmp = "";
        //Get Statistics
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                str.append(line);
            }
            in.close();
            tmp = str.toString();


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            except = e;
        }

        return tmp;

    }

    protected void onPostExecute(String json) {
        if (except != null){
            progress.dismiss();
            debug.setText(except.getMessage());
        } else {
            Gson gson = new Gson();
            OldPlayerInfoActivity.lastGsonObtained = json;
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            debug.setText(reply.toString());
            ivHead.setImageDrawable(null);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                result.setText(reply.getCause());
                Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                result.setTextColor(Color.RED);
                details.setVisibility(View.INVISIBLE);
            } else if (!reply.isSuccess()){
                //Not Successful
                progress.dismiss();
                result.setText(reply.getCause());
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
                details.setVisibility(View.INVISIBLE);
            } else if (reply.getPlayer() == null) {
                progress.dismiss();
                result.setText("Invalid Player");
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: Invalid Player Name (" + reply.getCause() + ")");
                details.setVisibility(View.INVISIBLE);
            } else {
                //Succeeded
                resultArray = new ArrayList<>();
                progress.dismiss();
                pro.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                new GetPlayerHead(pro, ivHead, mContext).execute(reply.getPlayer().get("displayname").getAsString());
                result.setText(Html.fromHtml("Success! Statistics for <br />" + MinecraftColorCodes.parseHypixelRanks(reply)));
                result.setTextColor(Color.GREEN);
                //Parse
                resultArray.add(new ResultDescription("<b>General Statistics</b>", null, false));
                parseGeneral(reply);

                if (reply.getPlayer().has("packageRank")) {
                    resultArray.add(new ResultDescription(" ", " "));
                    resultArray.add(new ResultDescription("<b>Donator Information</b>", null, false));
                    parseDonor(reply);
                }

                if (reply.getPlayer().has("rank")){
                    if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")){
                        resultArray.add(new ResultDescription(" ", " "));
                        if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")){
                            resultArray.add(new ResultDescription("<b>YouTuber Information</b>", null, false));
                        } else {
                            resultArray.add(new ResultDescription("<b>Staff Information</b>", null, false));
                        }
                        parsePriviledged(reply);
                    }
                }

                if (reply.getPlayer().has("achievements")){
                    resultArray.add(new ResultDescription(" ", " "));
                    resultArray.add(new ResultDescription("<b>Achievements</b>", null, false));
                    parseOngoingAchievements(reply);
                }

                if (reply.getPlayer().has("quests")){
                    resultArray.add(new ResultDescription(" ", " "));
                    resultArray.add(new ResultDescription("<b>Quest Stats</b>", null, false));
                    parseQuests(reply);
                }
                if (reply.getPlayer().has("parkourCompletions")) {
                    resultArray.add(new ResultDescription(" ", " "));
                    resultArray.add(new ResultDescription("<b>Parkour Stats</b>", null, false));
                    parseParkourCounts(reply);
                }

                if (reply.getPlayer().has("stats")){
                    resultArray.add(new ResultDescription(" ", " "));
                    resultArray.add(new ResultDescription("<b>Game Statistics</b>", null, false));
                    parseStats(reply);
                }

                for (ResultDescription e : resultArray) {
                    String r = e.get_result();
                    if (e.get_result() != null) {
                        if (e.get_result().equalsIgnoreCase("true") || e.get_result().equalsIgnoreCase("enabled")) {
                            e.set_result(MinecraftColorCodes.parseColors("§a" + r + "§r"));
                        }
                        if (e.get_result().equalsIgnoreCase("false") || e.get_result().equalsIgnoreCase("disabled")) {
                            e.set_result(MinecraftColorCodes.parseColors("§c" + r + "§r"));
                        }
                    }
                }
                ResultDescListAdapter adapter = new ResultDescListAdapter(this.mContext, R.layout.listview_result_desc, resultArray);
                details.setAdapter(adapter);
            }
        }
    }

    //Parsing General Information
    /*
    rank, displayname, uuid, packageRank, disguise, eulaCoins, gadget, karma, firstLogin, lastLogin, timePlaying, networkExp,
    networkLevel, mostRecentlyThanked, mostRecentlyTipped, thanksSent, tipsSent, channel, chat, tournamentTokens,
    vanityTokens, mostRecentGameType, seeRequest, tipsReceived, thanksReceived, achievementsOneTime
     */
    private void parseGeneral(PlayerReply reply){
        if (reply.getPlayer().has("rank"))
            resultArray.add(new ResultDescription("Rank: ", reply.getPlayer().get("rank").getAsString()));
        else
            resultArray.add(new ResultDescription("Rank: ", "NORMAL"));
        resultArray.add(new ResultDescription("Name: ",reply.getPlayer().get("displayname").getAsString()));
        resultArray.add(new ResultDescription("UUID: ",reply.getPlayer().get("uuid").getAsString()));
        if (reply.getPlayer().has("packageRank"))
            resultArray.add(new ResultDescription("Donor Rank: ",reply.getPlayer().get("packageRank").getAsString()));
        if (reply.getPlayer().has("disguise"))
            resultArray.add(new ResultDescription("Disguise: ",reply.getPlayer().get("disguise").getAsString()));
        if (reply.getPlayer().has("eulaCoins"))
            resultArray.add(new ResultDescription("Veteran Donor: ", "true"));
        if (reply.getPlayer().has("gadget"))
            resultArray.add(new ResultDescription("Lobby Gadget: ",reply.getPlayer().get("gadget").getAsString()));
        if (reply.getPlayer().has("karma"))
            resultArray.add(new ResultDescription("Karma: ",reply.getPlayer().get("karma").getAsString()));
        if (reply.getPlayer().has("firstLogin"))
            resultArray.add(new ResultDescription("First Login: ",new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("firstLogin").getAsLong()))));
        if (reply.getPlayer().has("lastLogin"))
            resultArray.add(new ResultDescription("Last Login: ",new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("lastLogin").getAsLong()))));
        //TODO Parse Time Played (MIN)
        resultArray.add(new ResultDescription("Time Played: ",MinecraftColorCodes.parseColors("§cComing Soon™§r")));
        if (reply.getPlayer().has("networkExp"))
            resultArray.add(new ResultDescription("Network XP: ",reply.getPlayer().get("networkExp").getAsString()));
        if (reply.getPlayer().has("networkLevel"))
            resultArray.add(new ResultDescription("Network Level: ",reply.getPlayer().get("networkLevel").getAsString()));
        else
            resultArray.add(new ResultDescription("Network Level: " , "1"));
        if (reply.getPlayer().has("mostRecentlyThanked"))
            resultArray.add(new ResultDescription("Last Thanked: ",reply.getPlayer().get("mostRecentlyThanked").getAsString()));
        if (reply.getPlayer().has("mostRecentlyTipped"))
            resultArray.add(new ResultDescription("Last Tipped: ",reply.getPlayer().get("mostRecentlyTipped").getAsString()));
        if (reply.getPlayer().has("thanksSent"))
            resultArray.add(new ResultDescription("No of Thanks sent: ",reply.getPlayer().get("thanksSent").getAsString()));
        if (reply.getPlayer().has("tipsSent"))
            resultArray.add(new ResultDescription("No of Tips sent: ",reply.getPlayer().get("tipsSent").getAsString()));
        if (reply.getPlayer().has("thanksReceived"))
            resultArray.add(new ResultDescription("No of Thanks received: ",reply.getPlayer().get("thanksReceived").getAsString()));
        if (reply.getPlayer().has("tipsReceived"))
            resultArray.add(new ResultDescription("No of Tips sent received: ",reply.getPlayer().get("tipsReceived").getAsString()));
        if (reply.getPlayer().has("channel"))
            resultArray.add(new ResultDescription("Current Chat Channel: ",reply.getPlayer().get("channel").getAsString()));
        else
            resultArray.add(new ResultDescription("Current Chat Channel: ", "ALL"));
        if (reply.getPlayer().has("chat")) {
            if (reply.getPlayer().get("chat").getAsBoolean())
                resultArray.add(new ResultDescription("Chat Enabled: ", "Enabled"));
            else
                resultArray.add(new ResultDescription("Chat Enabled: ", "Disabled"));
        } else
            resultArray.add(new ResultDescription("Chat Enabled: ", "Enabled"));
        if (reply.getPlayer().has("tournamentTokens"))
            resultArray.add(new ResultDescription("Tournament Tokens: ",reply.getPlayer().get("tournamentTokens").getAsString()));
        else
            resultArray.add(new ResultDescription("Tournament Tokens: ", "0"));
        if (reply.getPlayer().has("vanityTokens"))
            resultArray.add(new ResultDescription("Vanity Tokens: ",reply.getPlayer().get("vanityTokens").getAsString()));
        else
            resultArray.add(new ResultDescription("Vanity Tokens: ", "0 "));
        if (reply.getPlayer().has("mostRecentGameType"))
            resultArray.add(new ResultDescription("Last Game Played: ",reply.getPlayer().get("mostRecentGameType").getAsString()));
        if (reply.getPlayer().has("seeRequests")) {
            if (reply.getPlayer().get("seeRequests").getAsBoolean())
                resultArray.add(new ResultDescription("Friend Requests: ", "Enabled"));
            else
                resultArray.add(new ResultDescription("Friend Requests: ", "Disabled"));
        } else
            resultArray.add(new ResultDescription("Friend Requests: ", "Enabled"));
        if (reply.getPlayer().has("achievementsOneTime"))
            resultArray.add(new ResultDescription("No of 1-time Achievements Done: ", reply.getPlayer().getAsJsonArray("achievementsOneTime").size() + ""));
    }

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    private void parseDonor(PlayerReply reply){
        if (reply.getPlayer().has("fly"))
            resultArray.add(new ResultDescription("Fly Mode: ", reply.getPlayer().get("fly").getAsString()));
        if (reply.getPlayer().has("petActive"))
            resultArray.add(new ResultDescription("Active Pet: ", reply.getPlayer().get("petActive").getAsString()));
        else
            resultArray.add(new ResultDescription("Active Pet: ", "false"));
        if (reply.getPlayer().has("pp"))
            resultArray.add(new ResultDescription("Particle Pack: ", reply.getPlayer().get("pp").getAsString()));
        if (reply.getPlayer().has("testpass"))
            resultArray.add(new ResultDescription("Test Server Access: ", reply.getPlayer().get("testpass").getAsString()));
        if (reply.getPlayer().has("wardrobe"))
            resultArray.add(new ResultDescription("Wardrobe (H,C,L,B): ", reply.getPlayer().get("wardrobe").getAsString()));
        if (reply.getPlayer().has("auto_spawn_pet"))
            resultArray.add(new ResultDescription("Auto-Spawn Pet: ", reply.getPlayer().get("auto_spawn_pet").getAsString()));
        if (reply.getPlayer().has("legacyGolem"))
            resultArray.add(new ResultDescription("Golem Supporter: ", reply.getPlayer().get("legacyGolem").getAsString()));
    }

    /* Staff/YT Only Information
        vanished, stoggle, silence, chatTunnel, nick, prefix
     */
    private void parsePriviledged(PlayerReply reply){
        if (reply.getPlayer().has("vanished"))
            resultArray.add(new ResultDescription("Vanished: ", reply.getPlayer().get("vanished").getAsString()));
        if (reply.getPlayer().has("stoggle")) {
            if (reply.getPlayer().get("stoggle").getAsBoolean())
                resultArray.add(new ResultDescription("Staff Chat: " , "Enabled"));
            else
                resultArray.add(new ResultDescription("Staff Chat: ", "Disabled"));
        }
        if (reply.getPlayer().has("silence"))
            resultArray.add(new ResultDescription("Chat Silenced: ", reply.getPlayer().get("silence").getAsString()));
        if (reply.getPlayer().has("chatTunnel")) {
            if (reply.getPlayer().get("chatTunnel").isJsonNull())
                resultArray.add(new ResultDescription("Tunneled Into: ", "None"));
            else
                resultArray.add(new ResultDescription("Tunneled Into: ", reply.getPlayer().get("chatTunnel").getAsString()));
        }
        if (reply.getPlayer().has("nick"))
            resultArray.add(new ResultDescription("Nicked As: ", reply.getPlayer().get("nick").getAsString()));
        if (reply.getPlayer().has("prefix"))
            resultArray.add(new ResultDescription("Rank Prefix: ", reply.getPlayer().get("prefix").getAsString()));
    }

    /**
     * Parse statistics (Split based on GameType)
     * @param reply PlayerReply object
     */
    private void parseStats(PlayerReply reply){
        //TODO Parse the Statistics based on the gameType
        JsonObject mainStats = reply.getPlayer().getAsJsonObject("stats");
        boolean first = true;
        for (Map.Entry<String, JsonElement> entry : mainStats.entrySet()){
            if (first){
                first = false;
            } else {
                resultArray.add(new ResultDescription(" ", null, false, true));
            }
            //Based on stat go parse it
            JsonObject statistic = entry.getValue().getAsJsonObject();
            switch (entry.getKey().toLowerCase()){
                case "arena": break;
                case "arcade": break;
                case "hungergames": break;
                case "mcgo": break;
                case "paintball": break;
                case "quake": break;
                case "spleef": parseSpleef(statistic);
                    break;
                case "tntgames": break;
                case "vampirez": parseVampZ(statistic);
                    break;
                case "walls": parseWalls(statistic);
                    break;
                case "walls3": parseWalls3(statistic);
                    break;
                default: resultArray.add(new ResultDescription(entry.getKey(), MinecraftColorCodes.parseColors("§cPlease contact the dev to add this into the statistics§r")));
                    break;
            }
        }
    }

    /**
     * Parse Ongoing Achievements
     * @param reply PlayerReply object
     */
    private void parseOngoingAchievements(PlayerReply reply){
        JsonObject achievements = reply.getPlayer().getAsJsonObject("achievements");
        for (Map.Entry<String, JsonElement> entry : achievements.entrySet()){
            resultArray.add(new ResultDescription(entry.getKey() + ": ", entry.getValue().toString()));
        }
    }

    /**
     * Parse Lobby Parkour Staistics
     * @param reply PlayerReply object
     */
    private void parseParkourCounts(PlayerReply reply){
        JsonObject parkourMain = reply.getPlayer().getAsJsonObject("parkourCompletions");
        boolean first = true;
        for (Map.Entry<String, JsonElement> entry : parkourMain.entrySet()){
            //Get the location
            if (first){
                first = false;
            } else {
                resultArray.add(new ResultDescription(" ", null, false, true));
            }
            resultArray.add(new ResultDescription("<b>" + entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + "</b>", null, false, true));
            resultArray.add(new ResultDescription("Amount of Times Completed", entry.getValue().getAsJsonArray().size() + ""));
            //Get the count of times its completed
            JsonArray completionArray = entry.getValue().getAsJsonArray();
            int i = 1;
            for (JsonElement e : completionArray){
                JsonObject timings = e.getAsJsonObject();
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(timings.get("timeStart").getAsLong()));
                int timeDurationWork = timings.get("timeTook").getAsInt();
                String timeDuration = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(timeDurationWork), TimeUnit.MILLISECONDS.toSeconds(timeDurationWork) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDurationWork)));
                resultArray.add(new ResultDescription("Attempt #" + i + " (" + timeDuration + ")", "On: " + timeStamp));
                i++;
            }
        }
    }

    /**
     * Parse the Quests statistics
     * @param reply PlayerReply object
     */
    private void parseQuests(PlayerReply reply){
        JsonObject questMain = reply.getPlayer().getAsJsonObject("quests");
        boolean first = true;
        for (Map.Entry<String, JsonElement> entry : questMain.entrySet()){
            //Get each quest name
            if (first){
                first = false;
            } else {
                resultArray.add(new ResultDescription(" ", null, false, true));
            }
            resultArray.add(new ResultDescription("<b>" + entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + "</b>", null, false, true));
            if(entry.getValue().getAsJsonObject().has("active")){
                resultArray.add(new ResultDescription("Status", MinecraftColorCodes.parseColors("§aActive§r")));
                //Get Start Time
                long timings = entry.getValue().getAsJsonObject().get("active").getAsJsonObject().get("started").getAsLong();
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(timings));
                resultArray.add(new ResultDescription("Date Started", timeStamp));
                //TODO Eventually add a way to view objectives in an alert dialog
            } else {
                resultArray.add(new ResultDescription("Status", MinecraftColorCodes.parseColors("§cInactive§r")));
            }
            //Get number of completion times
            if (entry.getValue().getAsJsonObject().has("completions")){
                int numberOfTimes = entry.getValue().getAsJsonObject().get("completions").getAsJsonArray().size();
                resultArray.add(new ResultDescription("No of Times Completed", numberOfTimes + ""));
            } else {
                resultArray.add(new ResultDescription("No of Times Completed", "0"));
            }
        }
    }

    //STATISTICS PARSING

    /**
     * Walls Game
     * @param obj Statistics
     */
    private void parseWalls(JsonObject obj){
        resultArray.add(new ResultDescription("<b>Walls</b>", null, false, true));
        if (obj.has("coins"))
            resultArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            resultArray.add(new ResultDescription("Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            resultArray.add(new ResultDescription("Games Lost", obj.get("losses").getAsString()));
        if (obj.has("deaths"))
            resultArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            resultArray.add(new ResultDescription("Kills", obj.get("kills").getAsString()));
        if (obj.has("packages")){
            StringBuilder packageBuilder = new StringBuilder();
            JsonArray packages = obj.get("packages").getAsJsonArray();
            boolean firstPack = true;
            for (JsonElement e : packages){
                if (firstPack){
                    firstPack = false;
                    packageBuilder.append(e.getAsString());
                }
                else {
                    packageBuilder.append(",").append(e.getAsString());
                }
            }
            resultArray.add(new ResultDescription("Kills", packageBuilder.toString()));
        }
    }

    /**
     * Walls 3 Game
     * chosen_class, coins, deaths, kills, finalDeaths, finalKills, wins, losses
     * @param obj Statistics
     */
    private void parseWalls3(JsonObject obj){
        resultArray.add(new ResultDescription("<b>Walls 3</b>", null, false, true));
        if (obj.has("chosen_class"))
            resultArray.add(new ResultDescription("Class Selected", obj.get("chosen_class").getAsString()));
        if (obj.has("coins"))
            resultArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));

        //Overall
        if (obj.has("deaths"))
            resultArray.add(new ResultDescription("Total Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            resultArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("finalDeaths"))
            resultArray.add(new ResultDescription("Total Final Deaths", obj.get("finalDeaths").getAsString()));
        if (obj.has("finalKills"))
            resultArray.add(new ResultDescription("Total Final Kills", obj.get("finalKills").getAsString()));
        if (obj.has("wins"))
            resultArray.add(new ResultDescription("Total Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            resultArray.add(new ResultDescription("Total Games Lost", obj.get("losses").getAsString()));

        //TODO Eventually displays statistics of each class in an AlertDialog
        resultArray.add(new ResultDescription("Herobrine Statistics", MinecraftColorCodes.parseColors("§cIndividual Classes Statistics will be coming soon§r")));
        //Herobrine
        //Skeleton
        //Zombie
        //Creeper
        //Enderman
        //Spider
        //Dreadlord
        //Shaman
        //Arcanist
        //Golem
        //Blaze
        //Pigman

        //Weekly
        //TODO Display Weekly Statistics in an AlertDialog
        resultArray.add(new ResultDescription("Weekly Statistics", MinecraftColorCodes.parseColors("§cWeekly Statistics will be coming soon§r")));
    }

    private void parseQuake(JsonObject obj){}
    private void parseHG(JsonObject obj){}

    /**
     * VampireZ Statistics
     * coins, human_deaths, human_wins, human_kills, vampire_deaths, vampire_wins, vampire_kills
     * @param obj Statistics
     */
    private void parseVampZ(JsonObject obj){
        resultArray.add(new ResultDescription("<b>VampireZ</b>", null, false, true));
        if (obj.has("coins"))
            resultArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("human_deaths"))
            resultArray.add(new ResultDescription("Total Deaths (Human)", obj.get("human_deaths").getAsString()));
        if (obj.has("human_wins"))
            resultArray.add(new ResultDescription("Total Wins (Human)", obj.get("human_wins").getAsString()));
        if (obj.has("human_kills"))
            resultArray.add(new ResultDescription("Total Kills (Human)", obj.get("human_wins").getAsString()));
        if (obj.has("vampire_deaths"))
            resultArray.add(new ResultDescription("Total Deaths (Vampire)", obj.get("vampire_deaths").getAsString()));
        if (obj.has("vampire_wins"))
            resultArray.add(new ResultDescription("Total Wins (Vampire)", obj.get("vampire_wins").getAsString()));
        if (obj.has("vampire_kills"))
            resultArray.add(new ResultDescription("Total Kills (Vampire)", obj.get("vampire_kills").getAsString()));
    }
    private void parseArcade(JsonObject obj){}
    private void parseArena(JsonObject obj){}

    /**
     * Legacy Spleef Game?
     * @param obj Statistics
     */
    private void parseSpleef(JsonObject obj){
        resultArray.add(new ResultDescription("<b>Legacy Spleef</b>", null, false, true));
        if (obj.has("wins"))
            resultArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            resultArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
    }

    private void parseTntGames(JsonObject obj){}
    private void parsePaintball(JsonObject obj){}
    private void parseMcGo(JsonObject obj){}
}
