package com.itachi1706.hypixelstatistics.AsyncAPI.Players;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.AsyncAPI.Session.GetSessionInfoPlayerStats;
import com.itachi1706.hypixelstatistics.util.GameTypeCapsReturn;
import com.itachi1706.hypixelstatistics.util.GeneralPlayerStats.LobbyList;
import com.itachi1706.hypixelstatistics.util.GeneralPlayerStats.OngoingAchievements;
import com.itachi1706.hypixelstatistics.util.GeneralPlayerStats.QuestName;
import com.itachi1706.hypixelstatistics.util.GeneralPlayerStats.QuestObjectives;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.ExpandedResultDescListAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.util.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.util.Warlords.DetailedWeaponStatistics;
import com.itachi1706.hypixelstatistics.util.Warlords.WarlordsMounts;

import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.util.GameType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetPlayerByNameExpanded extends AsyncTask<String,Void,String> {

    TextView debug, result, sessionTV;
    ExpandableListView details;
    Activity mContext;
    Exception except = null;
    ImageView ivHead;
    ProgressDialog progress;
    ProgressBar pro;
    boolean isUUID;
    private String localPlayerName;

    android.support.v7.app.ActionBar ab;

    ArrayList<ResultDescription> resultArray;

    public GetPlayerByNameExpanded(TextView resultView, TextView debugView, ExpandableListView general, ImageView head, ProgressDialog prog, ProgressBar header, Activity context, boolean uuidState, android.support.v7.app.ActionBar acb, TextView sessionTV){
        debug = debugView;
        result = resultView;
        mContext = context;
        details = general;
        ivHead = head;
        progress = prog;
        pro = header;
        isUUID = uuidState;
        this.ab = acb;
        this.sessionTV = sessionTV;
    }

    @Override
    protected String doInBackground(String... playerName) {
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey;
        if (!isUUID) {
            url += "&name=" + playerName[0];
        } else {
            url += "&uuid=" + playerName[0];
        }
        String tmp = "";
        //Get Statistics
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setConnectTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            InputStream in = conn.getInputStream();

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
            if (progress != null && progress.isShowing())
                progress.dismiss();
            if (except instanceof SocketTimeoutException) {
                result.setText("Connection Timed Out. Try again later");
                result.setTextColor(Color.RED);
            } else {
                debug.setText(except.getMessage());
            }
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    Toast.makeText(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "An error occured. (Invalid JSON String) Please Try Again", Toast.LENGTH_SHORT).show();
                return;
            }
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            debug.setText(json);
            ivHead.setImageDrawable(null);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                result.setText(reply.getCause());
                Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                result.setTextColor(Color.RED);
                details.setVisibility(View.INVISIBLE);
            } else if (!reply.isSuccess()){
                //Not Successful
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                result.setText(reply.getCause());
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
                details.setVisibility(View.INVISIBLE);
            } else if (reply.getPlayer() == null) {
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                if (isUUID){
                    result.setText("Invalid UUID");
                    result.setTextColor(Color.RED);
                    Toast.makeText(mContext, "Unable to find a player with this UUID. If you are searching with a name, select Search with Name option in the menu!", Toast.LENGTH_SHORT).show();
                } else {
                    result.setText("Invalid Player");
                    result.setTextColor(Color.RED);
                    Toast.makeText(mContext, "Unable to find this player. If you are searching with a UUID, select Search with UUID option in the menu!", Toast.LENGTH_SHORT).show();
                }
                debug.setText("Unsuccessful Query!\n Reason: Invalid Player Name/UUID (" + reply.getCause() + ")");
                details.setVisibility(View.INVISIBLE);
            } else {
                //Succeeded
                resultArray = new ArrayList<>();
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                pro.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                if (MinecraftColorCodes.checkDisplayName(reply)) {
                    new GetPlayerHead(pro, ivHead, mContext, ab).execute(reply.getPlayer().get("displayname").getAsString());
                } else
                    pro.setVisibility(View.GONE);
                result.setText(Html.fromHtml("Success! Statistics for <br />" + MinecraftColorCodes.parseHypixelRanks(reply)));
                result.setTextColor(Color.GREEN);

                //Get Session Info
                String uuidSession = reply.getPlayer().get("uuid").getAsString();
                sessionTV.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§fQuerying session info...§r")));
                sessionTV.setVisibility(View.VISIBLE);
                new GetSessionInfoPlayerStats(sessionTV).execute(uuidSession);

                if (!checkHistory(reply)) {
                    CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(mContext));
                    Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
                }
                //Parse
                resultArray.add(new ResultDescription("<b>General Statistics</b>", null, false, parseGeneral(reply), null));
                //parseGeneral(reply);

                if (reply.getPlayer().has("packageRank")) {
                    resultArray.add(new ResultDescription("<b>Donator Information</b>", null, false, parseDonor(reply), null));
                    //parseDonor(reply);
                }

                if (MainStaticVars.isStaff || MainStaticVars.isCreator) {
                    if (reply.getPlayer().has("rank")) {
                        if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")) {
                            if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")) {
                                resultArray.add(new ResultDescription("<b>YouTuber Information</b>", null, false, parsePriviledged(reply), null));
                            } else {
                                resultArray.add(new ResultDescription("<b>Staff Information</b>", null, false, parsePriviledged(reply), null));
                            }
                            //parsePriviledged(reply);
                        }
                    }
                }

                if (reply.getPlayer().has("achievements")){
                    resultArray.add(new ResultDescription("<b>Ongoing Achievements</b>", null, false, parseOngoingAchievements(reply), null));
                    //parseOngoingAchievements(reply);
                }

                if (reply.getPlayer().has("quests")){
                    resultArray.add(new ResultDescription("<b>Quest Stats</b>", null, false, parseQuests(reply), null));
                    //parseQuests(reply);
                }
                if (reply.getPlayer().has("parkourCompletions")) {
                    resultArray.add(new ResultDescription("<b>Parkour Stats</b>", null, false, parseParkourCounts(reply), null));
                    //parseParkourCounts(reply);
                }

                if (reply.getPlayer().has("stats")){
                    //resultArray.add(new ResultDescription("<b>Game Statistics</b>", null, false, parseStats(reply), null));
                    parseStats(reply);
                }

                for (ResultDescription e : resultArray) {
                    if (e.get_result() != null) {
                        e.set_result(parseColorsInResults(e));
                    }
                    if (e.get_childItems() != null){
                        for (ResultDescription ex : e.get_childItems()){
                            if (ex.get_result() != null){
                                ex.set_result(parseColorsInResults(ex));
                            }
                        }
                    }
                }

                ExpandedResultDescListAdapter adapter = new ExpandedResultDescListAdapter(this.mContext, resultArray);
                details.setAdapter(adapter);
            }
        }
    }

    private String parseColorsInResults(ResultDescription e){
        String r = e.get_result();
        if (e.get_result().equalsIgnoreCase("true") || e.get_result().equalsIgnoreCase("enabled")) {
            return MinecraftColorCodes.parseColors("§a" + r + "§r");
        }
        if (e.get_result().equalsIgnoreCase("false") || e.get_result().equalsIgnoreCase("disabled")) {
            return MinecraftColorCodes.parseColors("§c" + r + "§r");
        }
        if((e.get_result().equalsIgnoreCase("null") || e.get_result() == null) && e.is_hasDescription()){
            return MinecraftColorCodes.parseColors("§c" + "NONE" + "§r");
        }
        return r;
    }

    private boolean checkHistory(PlayerReply reply){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mContext));
        Log.d("HISTORY STRING", hist);
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            JsonArray histCheck = check.getHistory();
            Log.d("HISTORY ORIGINAL", histCheck.toString());
            for (JsonElement el : histCheck) {
                JsonObject histCheckName = el.getAsJsonObject();
                if (histCheckName.get("playername").getAsString().equals(reply.getPlayer().get("playername").getAsString())) {
                    //Remove and let it reupdate
                    histCheck.remove(histCheckName);
                    Log.d("HISTORY AFTER REMOVAL", histCheck.toString());
                    CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mContext), histCheck);
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    //Parsing General Information
    /*
    rank, displayname, uuid, packageRank, disguise, eulaCoins, gadget, karma, firstLogin, lastLogin, timePlaying, networkExp,
    networkLevel, mostRecentlyThanked, mostRecentlyTipped, thanksSent, tipsSent, channel, chat, tournamentTokens,
    vanityTokens, mostRecentGameType, seeRequest, tipsReceived, thanksReceived, achievementsOneTime
     */
    @SuppressLint("SimpleDateFormat")
    private ArrayList<ResultDescription> parseGeneral(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (reply.getPlayer().has("rank"))
            descArray.add(new ResultDescription("Rank", reply.getPlayer().get("rank").getAsString()));
        else
            descArray.add(new ResultDescription("Rank", "NORMAL"));
        if (MinecraftColorCodes.checkDisplayName(reply))
            this.localPlayerName = reply.getPlayer().get("displayname").getAsString();
        else
            this.localPlayerName = reply.getPlayer().get("playername").getAsString();
        descArray.add(new ResultDescription("Name", this.localPlayerName));
        descArray.add(new ResultDescription("UUID",reply.getPlayer().get("uuid").getAsString()));
        //Donor Rank (packageRank and newPackageRank)
        if (reply.getPlayer().has("newPackageRank")){
            //Post-EULA Donator
            descArray.add(new ResultDescription("Donor Rank", reply.getPlayer().get("newPackageRank").getAsString()));
            if (reply.getPlayer().has("packageRank"))
                //Pre-EULA donator that upgraded rank post-EULA
                descArray.add(new ResultDescription(MinecraftColorCodes.parseColors("Legacy Donor Rank §6(Pre-EULA)§r"), reply.getPlayer().get("packageRank").getAsString()));
        } else {
            //Pre-EULA Donator and Non-Donator
            if (reply.getPlayer().has("packageRank"))
                descArray.add(new ResultDescription("Donor Rank", reply.getPlayer().get("packageRank").getAsString()));
        }
        if (reply.getPlayer().has("disguise"))
            descArray.add(new ResultDescription("Disguise",reply.getPlayer().get("disguise").getAsString()));
        if (reply.getPlayer().has("eulaCoins"))
            descArray.add(new ResultDescription(MinecraftColorCodes.parseColors("Veteran Donor §6(Pre-EULA)§r"), "true"));
        if (reply.getPlayer().has("gadget"))
            descArray.add(new ResultDescription("Lobby Gadget",reply.getPlayer().get("gadget").getAsString()));
        if (reply.getPlayer().has("karma"))
            descArray.add(new ResultDescription("Karma",reply.getPlayer().get("karma").getAsString()));
        if (reply.getPlayer().has("firstLogin"))
            descArray.add(new ResultDescription("First Login",new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("firstLogin").getAsLong()))));
        if (reply.getPlayer().has("lastLogin"))
            descArray.add(new ResultDescription("Last Login",new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("lastLogin").getAsLong()))));
        if (reply.getPlayer().has("timePlaying"))
            descArray.add(new ResultDescription("Time Played (From 16 May 2014) ",MinecraftColorCodes.parseColors(parseTimeOnline(reply.getPlayer().get("timePlaying").getAsLong()))));
        if (reply.getPlayer().has("networkExp"))
            descArray.add(new ResultDescription("Network XP",reply.getPlayer().get("networkExp").getAsString()));
        if (reply.getPlayer().has("networkLevel"))
            descArray.add(new ResultDescription("Network Level",reply.getPlayer().get("networkLevel").getAsString()));
        else
            descArray.add(new ResultDescription("Network Level" , "1"));
        if (reply.getPlayer().has("mostRecentlyThanked"))
            descArray.add(new ResultDescription("Last Thanked (Legacy)",reply.getPlayer().get("mostRecentlyThanked").getAsString()));
        if (reply.getPlayer().has("mostRecentlyTipped"))
            descArray.add(new ResultDescription("Last Tipped (Legacy)",reply.getPlayer().get("mostRecentlyTipped").getAsString()));
        if (reply.getPlayer().has("mostRecentlyThankedUuid"))
            descArray.add(new ResultDescription("Last Thanked",reply.getPlayer().get("mostRecentlyThankedUuid").getAsString(), true, "=+=senduuid=+= " + reply.getPlayer().get("mostRecentlyThankedUuid").getAsString()));
        if (reply.getPlayer().has("mostRecentlyTippedUuid"))
            descArray.add(new ResultDescription("Last Tipped",reply.getPlayer().get("mostRecentlyTippedUuid").getAsString(), true, "=+=senduuid=+= " + reply.getPlayer().get("mostRecentlyTippedUuid").getAsString()));
        if (reply.getPlayer().has("thanksSent"))
            descArray.add(new ResultDescription("No of Thanks sent",reply.getPlayer().get("thanksSent").getAsString()));
        if (reply.getPlayer().has("tipsSent"))
            descArray.add(new ResultDescription("No of Tips sent",reply.getPlayer().get("tipsSent").getAsString()));
        if (reply.getPlayer().has("thanksReceived"))
            descArray.add(new ResultDescription("No of Thanks received",reply.getPlayer().get("thanksReceived").getAsString()));
        if (reply.getPlayer().has("tipsReceived"))
            descArray.add(new ResultDescription("No of Tips sent received",reply.getPlayer().get("tipsReceived").getAsString()));
        if (reply.getPlayer().has("channel"))
            descArray.add(new ResultDescription("Current Chat Channel",reply.getPlayer().get("channel").getAsString()));
        else
            descArray.add(new ResultDescription("Current Chat Channel", "ALL"));
        if (reply.getPlayer().has("chat")) {
            if (reply.getPlayer().get("chat").getAsBoolean())
                descArray.add(new ResultDescription("Chat Enabled", "Enabled"));
            else
                descArray.add(new ResultDescription("Chat Enabled", "Disabled"));
        } else
            descArray.add(new ResultDescription("Chat Enabled", "Enabled"));
        if (reply.getPlayer().has("tournamentTokens"))
            descArray.add(new ResultDescription("Tournament Tokens",reply.getPlayer().get("tournamentTokens").getAsString()));
        else
            descArray.add(new ResultDescription("Tournament Tokens", "0"));
        if (reply.getPlayer().has("vanityTokens"))
            descArray.add(new ResultDescription("Vanity Tokens",reply.getPlayer().get("vanityTokens").getAsString()));
        else
            descArray.add(new ResultDescription("Vanity Tokens", "0 "));
        if (reply.getPlayer().has("mostRecentGameType")) {
            GameType recentGameType = GameTypeCapsReturn.fromDatabase(reply.getPlayer().get("mostRecentGameType").getAsString());
            if (recentGameType == null) {
                descArray.add(new ResultDescription("Last Game Played", reply.getPlayer().get("mostRecentGameType").getAsString()));
            } else {
                descArray.add(new ResultDescription("Last Game Played", recentGameType.getName()));
            }
        }
        if (reply.getPlayer().has("seeRequests")) {
            if (reply.getPlayer().get("seeRequests").getAsBoolean())
                descArray.add(new ResultDescription("Friend Requests", "Enabled"));
            else
                descArray.add(new ResultDescription("Friend Requests", "Disabled"));
        } else
            descArray.add(new ResultDescription("Friend Requests", "Enabled"));
        if (reply.getPlayer().has("achievementsOneTime"))
            descArray.add(new ResultDescription("No of 1-time Achievements Done", reply.getPlayer().getAsJsonArray("achievementsOneTime").size() + ""));
        if (reply.getPlayer().has("knownAliases")){
            JsonArray arr = reply.getPlayer().getAsJsonArray("knownAliases");
            StringBuilder listOfAliases = new StringBuilder();
            for (JsonElement e : arr){
                listOfAliases.append(e.getAsString()).append("\n");
            }
            MainStaticVars.knownAliases = listOfAliases.toString();
            descArray.add(new ResultDescription("Known Aliases", listOfAliases.toString().replace("\n", " ")));
        }
        return descArray;
    }

    private String parseTimeOnline(long time){
        //Get Days if there is
        if (time > 1440){
            //Theres Days (D, H, M)
            return String.format("%d Days, %d Hours, %d Minutes", TimeUnit.MINUTES.toDays(time), TimeUnit.MINUTES.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MINUTES.toDays(time)), time - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(time)));
        }
        if (time > 60){
            //Theres Hours (H, M)
            return String.format("%d Hours, %d Minutes", TimeUnit.MINUTES.toHours(time) ,time - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(time)));
        }
        //Minutes only (M)
        return time + " Minutes";
    }

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    private ArrayList<ResultDescription> parseDonor(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (reply.getPlayer().has("fly"))
            descArray.add(new ResultDescription("Fly Mode", reply.getPlayer().get("fly").getAsString()));
        if (reply.getPlayer().has("petActive"))
            descArray.add(new ResultDescription("Active Pet", reply.getPlayer().get("petActive").getAsString()));
        else
            descArray.add(new ResultDescription("Active Pet", "false"));
        if (reply.getPlayer().has("pp"))
            descArray.add(new ResultDescription("Particle Pack", reply.getPlayer().get("pp").getAsString()));
        if (reply.getPlayer().has("testpass"))
            descArray.add(new ResultDescription("Test Server Access", reply.getPlayer().get("testpass").getAsString()));
        if (reply.getPlayer().has("wardrobe"))
            descArray.add(new ResultDescription("Wardrobe (H,C,L,B)", reply.getPlayer().get("wardrobe").getAsString()));
        if (reply.getPlayer().has("auto_spawn_pet"))
            descArray.add(new ResultDescription("Auto-Spawn Pet", reply.getPlayer().get("auto_spawn_pet").getAsString()));
        if (reply.getPlayer().has("legacyGolem"))
            descArray.add(new ResultDescription(MinecraftColorCodes.parseColors("Golem Supporter §6(Pre-EULA)§r"), reply.getPlayer().get("legacyGolem").getAsString()));
        return descArray;
    }

    /* Staff/YT Only Information
        vanished, stoggle, silence, chatTunnel, nick, prefix
     */
    private ArrayList<ResultDescription> parsePriviledged(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (reply.getPlayer().has("vanished"))
            descArray.add(new ResultDescription("Vanished", reply.getPlayer().get("vanished").getAsString()));
        if (reply.getPlayer().has("stoggle")) {
            if (reply.getPlayer().get("stoggle").getAsBoolean())
                descArray.add(new ResultDescription("Staff Chat" , "Enabled"));
            else
                descArray.add(new ResultDescription("Staff Chat", "Disabled"));
        }
        if (reply.getPlayer().has("silence"))
            descArray.add(new ResultDescription("Chat Silenced", reply.getPlayer().get("silence").getAsString()));
        if (reply.getPlayer().has("chatTunnel")) {
            if (reply.getPlayer().get("chatTunnel").isJsonNull())
                descArray.add(new ResultDescription("Tunneled Into", "None"));
            else
                descArray.add(new ResultDescription("Tunneled Into", reply.getPlayer().get("chatTunnel").getAsString()));
        }
        if (reply.getPlayer().has("nick"))
            descArray.add(new ResultDescription("Nicked As", reply.getPlayer().get("nick").getAsString()));
        if (reply.getPlayer().has("prefix"))
            descArray.add(new ResultDescription("Rank Prefix", reply.getPlayer().get("prefix").getAsString()));
        return descArray;
    }

    /**
     * Parse statistics (Split based on GameType)
     * @param reply PlayerReply object
     */
    private ArrayList<ResultDescription> parseStats(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        JsonObject mainStats = reply.getPlayer().getAsJsonObject("stats");
        boolean first = true;
        for (Map.Entry<String, JsonElement> entry : mainStats.entrySet()){
            if (first){
                first = false;
            } else {
                descArray.add(new ResultDescription(" ", null, false));
            }
            //Based on stat go parse it
            JsonObject statistic = entry.getValue().getAsJsonObject();
            GameType parseVariousGamemode = GameType.fromDatabase(entry.getKey());
            if (parseVariousGamemode == null) {
                switch (entry.getKey().toLowerCase()) {
                    case "spleef":
                        resultArray.add(new ResultDescription("Legacy Spleef Statistics", null, false, parseSpleef(statistic)));
                        break;
                    case "holiday": //descArray.remove(descArray.size() - 1);
                        break;
                    default:
                        resultArray.add(new ResultDescription(entry.getKey() + " (ERROR - INFORM DEV)", MinecraftColorCodes.parseColors("§cPlease contact the dev to add this into the statistics§r")));
                        break;
                }
            } else {
                switch (parseVariousGamemode) {
                    case ARENA:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseArena(statistic)));
                        break;
                    case ARCADE:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseArcade(statistic)));
                        break;
                    case SURVIVAL_GAMES:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseHG(statistic)));
                        break;
                    case MCGO:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseMcGo(statistic)));
                        break;
                    case PAINTBALL:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parsePaintball(statistic)));
                        break;
                    case QUAKECRAFT:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseQuake(statistic)));
                        break;
                    case TNTGAMES:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseTntGames(statistic)));
                        break;
                    case VAMPIREZ:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseVampZ(statistic)));
                        break;
                    case WALLS:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseWalls(statistic)));
                        break;
                    case WALLS3:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseWalls3(statistic)));
                        break;
                    case UHC:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseUHC(statistic)));
                        break;
                    case WARLORDS:
                        resultArray.add(new ResultDescription(parseVariousGamemode.getName() + " Statistics", null, false, parseWarlords(statistic)));
                        break;
                    default:
                        resultArray.add(new ResultDescription(entry.getKey() + " (ERROR - INFORM DEV)", MinecraftColorCodes.parseColors("§cPlease contact the dev to add this into the statistics§r")));
                        break;
                }
            }
        }
        return descArray;
    }

    /**
     * Parse Ongoing Achievements with Achievement Enums
     * @param reply PlayerReply object
     */
    private ArrayList<ResultDescription> parseOngoingAchievements(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        JsonObject achievements = reply.getPlayer().getAsJsonObject("achievements");
        Map<String, JsonElement> tmpMapping = new HashMap<>();
        Map<String, JsonElement> toRemove = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : achievements.entrySet()){
            //Temp transfer to another map
            tmpMapping.put(e.getKey(), e.getValue());
        }

        //Iterate through the tmpMapping based on lobbies
        for (LobbyList list : LobbyList.values()) {
            ArrayList<ResultDescription> perLobbyArray = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : tmpMapping.entrySet()) {
                OngoingAchievements achievement = OngoingAchievements.fromDatabase(entry.getKey());
                if (achievement.getAchievementLobbies() == list){
                    if (achievement == OngoingAchievements.UNKNOWN)
                        perLobbyArray.add(new ResultDescription(entry.getKey() + "(" + list.getName() + ")", entry.getValue().toString()));
                    else {
                        ArrayList<ResultDescription> tmpArray = splitAchievements(achievement, entry.getValue().getAsInt());
                        for (ResultDescription d : tmpArray) {
                            perLobbyArray.add(d);
                        }
                        tmpArray.clear();
                    }
                    toRemove.put(entry.getKey(), entry.getValue());
                }
            }
            if (perLobbyArray.size() > 0){
                descArray.add(new ResultDescription("<b>" + list.getName() + "</b>", MinecraftColorCodes.GOLD.getHtmlCode() + perLobbyArray.size() + " achievements" + MinecraftColorCodes.CLEAR.getHtmlCode()));
                for (ResultDescription d : perLobbyArray)
                descArray.add(d);
            }
            perLobbyArray.clear();
        }

        //Remove the already displayed stuff from tmpMapping
        for (Map.Entry<String, JsonElement> entry : toRemove.entrySet()){
            tmpMapping.remove(entry.getKey());
        }

        //Remaining ones in Hashmap just also print
        for (Map.Entry<String, JsonElement> entry : tmpMapping.entrySet()){
            OngoingAchievements achievement = OngoingAchievements.fromDatabase(entry.getKey());
            if (achievement == OngoingAchievements.UNKNOWN)
                descArray.add(new ResultDescription(entry.getKey() + "(" + LobbyList.UNKNOWN.getName() + ")", entry.getValue().toString()));
            else {
                ArrayList<ResultDescription> tmpArray = splitAchievements(achievement, entry.getValue().getAsInt());
                for (ResultDescription d : tmpArray) {
                    descArray.add(d);
                }
            }
        }

        tmpMapping.clear();
        toRemove.clear();
        return descArray;
    }

    private ArrayList<ResultDescription> splitAchievements(OngoingAchievements achievement, int achievedValue){
        ArrayList<ResultDescription> endResult = new ArrayList<>();
        for (int i = 1; i <= achievement.getMax_tiers(); i++){
            String title = OngoingAchievements.getTitleByTier(achievement, i);
            String description = OngoingAchievements.getDescriptionByTier(achievement, i);
            int tierValue = OngoingAchievements.getTierByTier(achievement, i);
            String progression;
            if (tierValue <= achievedValue){
                //Completed alr (Show Completed)
                progression = MinecraftColorCodes.GREEN.getHtmlCode() + "Completed" + MinecraftColorCodes.CLEAR.getHtmlCode();
            } else {
                //In progress (Show progress)
                progression = MinecraftColorCodes.LIGHT_PURPLE.getHtmlCode() + achievedValue + MinecraftColorCodes.CLEAR.getHtmlCode() +
                        "/" + MinecraftColorCodes.AQUA.getHtmlCode() + tierValue + MinecraftColorCodes.CLEAR.getHtmlCode();
            }
            String compiledSubString = description + "<br />" + progression;
            endResult.add(new ResultDescription(title, compiledSubString));
        }
        return endResult;
    }

    /**
     * Parse Lobby Parkour Staistics
     * @param reply PlayerReply object
     */
    private ArrayList<ResultDescription> parseParkourCounts(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        JsonObject parkourMain = reply.getPlayer().getAsJsonObject("parkourCompletions");
        int legacyCount = 0, newLobbyParkourCount = 0;
        for (Map.Entry<String, JsonElement> entry : parkourMain.entrySet()){
            ArrayList<ResultDescription> parkArray = new ArrayList<>();
            //Get the count of times its completed
            JsonArray completionArray = entry.getValue().getAsJsonArray();
            int i = 1;
            for (JsonElement e : completionArray){
                JsonObject timings = e.getAsJsonObject();
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(timings.get("timeStart").getAsLong()));
                int timeDurationWork = timings.get("timeTook").getAsInt();
                String timeDuration = String.format("%d min, %d sec %d millis", TimeUnit.MILLISECONDS.toMinutes(timeDurationWork), TimeUnit.MILLISECONDS.toSeconds(timeDurationWork) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDurationWork)), timeDurationWork - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeDurationWork)));
                parkArray.add(new ResultDescription("Attempt #" + i + ": " + timeDuration + "", "On: " + timeStamp));
                i++;
            }
            if (parkArray.size() > 0){
                StringBuilder msg = new StringBuilder();
                msg.append("Amount of Times Completed: ").append(entry.getValue().getAsJsonArray().size()).append("<br />");
                for (ResultDescription t : parkArray){
                    msg.append(t.get_title()).append("<br />").append(t.get_result()).append("<br />");
                }
                //Check for which parkour it is (if legacy or new)

                //Currently there is 12 parkour (with Warlords coming soon)
                switch(entry.getKey()){
                    case "ArcadeGames":
                        descArray.add(new ResultDescription(GameType.ARCADE.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "Arena":
                        descArray.add(new ResultDescription(GameType.ARENA.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "BlitzLobby":
                        descArray.add(new ResultDescription(GameType.SURVIVAL_GAMES.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "CopsnCrims":
                        descArray.add(new ResultDescription(GameType.MCGO.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "MainLobby":
                        descArray.add(new ResultDescription("Main Lobby Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "MegaWalls":
                        descArray.add(new ResultDescription(GameType.WALLS3.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "Paintball":
                        descArray.add(new ResultDescription(GameType.PAINTBALL.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "QuakeCraft":
                        descArray.add(new ResultDescription(GameType.QUAKECRAFT.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "TNT":
                        descArray.add(new ResultDescription(GameType.TNTGAMES.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "TheWallsLobby":
                        descArray.add(new ResultDescription(GameType.WALLS.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "uhc":
                        descArray.add(new ResultDescription(GameType.UHC.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "vampirez":
                        descArray.add(new ResultDescription(GameType.VAMPIREZ.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    case "Warlords":
                        descArray.add(new ResultDescription(GameType.WARLORDS.getName() + " Parkour", "Click here to view parkour statistics", true, msg.toString()));
                        newLobbyParkourCount++;
                        break;
                    default:
                        descArray.add(new ResultDescription(entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + " (Legacy)", "Click here to view parkour statistics", true, msg.toString()));
                        legacyCount++;
                        break;
                }
            }
        }
        //Add to front the count of parkour completions etc
        descArray.add(0, new ResultDescription("Legacy Parkour Completed", legacyCount + ""));
        descArray.add(0, new ResultDescription("Parkour Completed", newLobbyParkourCount + "/" + MainStaticVars.SERVER_PARKOUR_COUNT));
        return descArray;
    }

    /**
     * Parse the Quests statistics
     * @param reply PlayerReply object
     */
    private ArrayList<ResultDescription> parseQuests(PlayerReply reply){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        JsonObject questMain = reply.getPlayer().getAsJsonObject("quests");
        for (Map.Entry<String, JsonElement> entry : questMain.entrySet()){
            //Get each quest name
            ArrayList<ResultDescription> qArray = new ArrayList<>();
            //descArray.add(new ResultDescription("<b>" + entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + "</b>", null, false, true));
            if(entry.getValue().getAsJsonObject().has("active")){
                qArray.add(new ResultDescription("Status", MinecraftColorCodes.parseColors("§aActive§r")));
                //Get Start Time
                long timings = entry.getValue().getAsJsonObject().get("active").getAsJsonObject().get("started").getAsLong();
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(timings));
                qArray.add(new ResultDescription("Date Started", timeStamp + "<br />"));

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
                        qArray.add(new ResultDescription("<b>Objectives</b>", build.toString()));
                    }
                }
            } else {
                qArray.add(new ResultDescription("Status", MinecraftColorCodes.parseColors("§cInactive§r")));
            }


            //Get number of completion times
            if (entry.getValue().getAsJsonObject().has("completions")){
                int numberOfTimes = entry.getValue().getAsJsonObject().get("completions").getAsJsonArray().size();
                qArray.add(new ResultDescription("No of Times Completed", numberOfTimes + ""));
            } else {
                qArray.add(new ResultDescription("No of Times Completed", "0"));
            }
            if (qArray.size() > 0){
                StringBuilder msg = new StringBuilder();
                for (ResultDescription t : qArray){
                    msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
                }
                //Add quest statistics to DB
                QuestName questN = QuestName.fromDB(entry.getKey());
                if (questN == QuestName.UNKNOWN){
                    //This is default unknown quests
                    String tryFormatQuestName = entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
                    descArray.add(new ResultDescription(tryFormatQuestName, "Click here to see " + tryFormatQuestName + " quest statistics", true, msg.toString()));
                } else {
                    descArray.add(new ResultDescription(questN.getQuestTitle(), "Click here to see " + questN.getQuestTitle() + " quest statistics", true, msg.toString()));
                }



            }
        }
        return descArray;
    }

    //STATISTICS PARSING

    /**
     * Walls Game
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseWalls(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Walls</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            descArray.add(new ResultDescription("Games Lost", obj.get("losses").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Kills", obj.get("kills").getAsString()));
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
            descArray.add(new ResultDescription("Packages", packageBuilder.toString()));
        }
        return descArray;
    }

    /**
     * Walls 3 Game
     * chosen_class, coins, deaths, kills, finalDeaths, finalKills, wins, losses
     * individual/weekly statistics soon
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseWalls3(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Walls 3</b>", null, false, true));
        if (obj.has("chosen_class"))
            descArray.add(new ResultDescription("Class Selected", obj.get("chosen_class").getAsString()));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));

        //Overall
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Total Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("finalDeaths"))
            descArray.add(new ResultDescription("Total Final Deaths", obj.get("finalDeaths").getAsString()));
        if (obj.has("finalKills"))
            descArray.add(new ResultDescription("Total Final Kills", obj.get("finalKills").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Total Games Won", obj.get("wins").getAsString()));
        if (obj.has("losses"))
            descArray.add(new ResultDescription("Total Games Lost", obj.get("losses").getAsString()));

        //Herobrine
        descArray = parseIndividualMW(obj, "Herobrine", descArray);
        //Skeleton
        descArray = parseIndividualMW(obj, "Skeleton", descArray);
        //Zombie
        descArray = parseIndividualMW(obj, "Zombie", descArray);
        //Creeper
        descArray = parseIndividualMW(obj, "Creeper", descArray);
        //Enderman
        descArray = parseIndividualMW(obj, "Enderman", descArray);
        //Spider
        descArray = parseIndividualMW(obj, "Spider", descArray);
        //Dreadlord
        descArray = parseIndividualMW(obj, "Dreadlord", descArray);
        //Shaman
        descArray = parseIndividualMW(obj, "Shaman", descArray);
        //Arcanist
        descArray = parseIndividualMW(obj, "Arcanist", descArray);
        //Golem
        descArray = parseIndividualMW(obj, "Golem", descArray);
        //Blaze
        descArray = parseIndividualMW(obj, "Blaze", descArray);
        //Pigman
        descArray = parseIndividualMW(obj, "Pigman", descArray);
        return descArray;
    }

    private ArrayList<ResultDescription> parseIndividualMW(JsonObject obj, String className, ArrayList<ResultDescription> descArray){
        ArrayList<ResultDescription> classArray = new ArrayList<>();
        if (obj.has("deaths_" + className))
            classArray.add(new ResultDescription("Deaths", obj.get("deaths_" + className).getAsString()));
        if (obj.has("kills_" + className))
            classArray.add(new ResultDescription("Kills", obj.get("kills_" + className).getAsString()));
        if (obj.has("finalDeaths_" + className))
            classArray.add(new ResultDescription("Final Deaths", obj.get("finalDeaths_" + className).getAsString()));
        if (obj.has("finalKills_" + className))
            classArray.add(new ResultDescription("Final Kills", obj.get("finalKills_" + className).getAsString()));
        if (obj.has("wins_" + className))
            classArray.add(new ResultDescription("Games Won", obj.get("wins_" + className).getAsString()));
        if (obj.has("losses_" + className))
            classArray.add(new ResultDescription("Games Lost", obj.get("losses_" + className).getAsString()));
        if (classArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : classArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription(className + " Statistics", "Click here to view " + className + " Statistics", true, msg.toString()));
        //} else {
        //    descArray.add(new ResultDescription(className + " Statistics", "Click here to view " + className + " Statistics", true, "This player does not have any statistics for this class yet!"));
        }
        return descArray;
    }

    /**
     * Quakecraft Game
     * displayed: coins, deaths, kills, killstreaks, wins
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseQuake(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>QuakeCraft</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("killstreaks"))
            descArray.add(new ResultDescription("Longest Killstreak", obj.get("killstreaks").getAsString()));
        return descArray;
    }

    /**
     * BSG (Hunger Games)
     * displayed: aura, chosen_taunt, blood, chosen_victorydance, coins, deaths, kills, wins
     * soon: class levels
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseHG(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Blitz Survival Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("blood"))
            descArray.add(new ResultDescription("Blood Enabled", obj.get("blood").getAsString()));
        if (obj.has("aura"))
            descArray.add(new ResultDescription("Chosen Aura", obj.get("aura").getAsString()));
        if (obj.has("chosen_taunt"))
            descArray.add(new ResultDescription("Chosen Taunt", obj.get("chosen_taunt").getAsString()));
        if (obj.has("chosen_victorydance"))
            descArray.add(new ResultDescription("Chosen Victory Dance", obj.get("chosen_victorydance").getAsString()));
        return descArray;
    }

    /**
     * VampireZ Statistics
     * coins, human_deaths, human_wins, human_kills, vampire_deaths, vampire_wins, vampire_kills
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseVampZ(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>VampireZ</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("human_deaths"))
            descArray.add(new ResultDescription("Total Deaths (Human)", obj.get("human_deaths").getAsString()));
        if (obj.has("human_wins"))
            descArray.add(new ResultDescription("Total Wins (Human)", obj.get("human_wins").getAsString()));
        if (obj.has("human_kills"))
            descArray.add(new ResultDescription("Total Kills (Human)", obj.get("human_kills").getAsString()));
        if (obj.has("vampire_deaths"))
            descArray.add(new ResultDescription("Total Deaths (Vampire)", obj.get("vampire_deaths").getAsString()));
        if (obj.has("vampire_wins"))
            descArray.add(new ResultDescription("Total Wins (Vampire)", obj.get("vampire_wins").getAsString()));
        if (obj.has("vampire_kills"))
            descArray.add(new ResultDescription("Total Kills (Vampire)", obj.get("vampire_kills").getAsString()));
        return descArray;
    }

    /**
     * The Arcade Games
     * displayed: blood, coins
     * (Creeper Attack) max_wave
     * (Ender Spleef) wins_ender
     * (Party Games) wins_party, wins_party_2
     * (Bounty Hunter) bounty_kills_oneinthequiver, bounty_head, deaths_oneinthequiver, kills_oneinthequiver, wins_oneinthequiver
     * (Farm Hunt) wins_farm_hunt, poop_collected
     * (The Blocking Dead) headshots_dayone, kills_dayone, wins_dayone
     * (Throw Out) wins_throw_out, kills_throw_out, deaths_throw_out
     * (Dragon Wars) kills_dragonwars2, wins_dragonwars2
     * (Galaxy Wars) sw_kills, sw_shot_fired, sw_rebel_kills, sw_deaths, sw_empire_kills
     * (Build Battle) wins_buildbattle, wins_buildbattle_teams
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseArcade(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>The Arcade Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("blood"))
            descArray.add(new ResultDescription("Blood Enabled", obj.get("blood").getAsString()));
        if (obj.has("max_wave"))
            descArray.add(new ResultDescription("Creeper Attack Max Wave", obj.get("max_wave").getAsString()));
        if (obj.has("wins_ender"))
            descArray.add(new ResultDescription("Ender Spleef Wins", obj.get("wins_ender").getAsString()));
        if (obj.has("wins_party"))
            descArray.add(new ResultDescription("Party Games Wins", obj.get("wins_party").getAsString()));
        if (obj.has("wins_party_2"))
            descArray.add(new ResultDescription("Party Games 2 Wins", obj.get("wins_party_2").getAsString()));

        //Bounty Hunter
        ArrayList<ResultDescription> bhArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Bounty Hunter</i>", null, false, true));
        if (obj.has("bounty_head"))
            bhArray.add(new ResultDescription("Bounty Head", obj.get("bounty_head").getAsString()));
        if (obj.has("bounty_kills_oneinthequiver"))
            bhArray.add(new ResultDescription("Total Bounty Kills", obj.get("bounty_kills_oneinthequiver").getAsString()));
        if (obj.has("deaths_oneinthequiver"))
            bhArray.add(new ResultDescription("Deaths", obj.get("deaths_oneinthequiver").getAsString()));
        if (obj.has("kills_oneinthequiver"))
            bhArray.add(new ResultDescription("Kills", obj.get("kills_oneinthequiver").getAsString()));
        if (obj.has("wins_oneinthequiver"))
            bhArray.add(new ResultDescription("Wins", obj.get("wins_oneinthequiver").getAsString()));
        if (bhArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : bhArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Bounty Hunter", "Click here to view statistics from Bounty Hunter", true, msg.toString()));
        }

        //Farm Hunt
        ArrayList<ResultDescription> fhArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Farm Hunt</i>", null, false, true));
        if (obj.has("wins_farm_hunt"))
            fhArray.add(new ResultDescription("Wins", obj.get("wins_farm_hunt").getAsString()));
        if (obj.has("poop_collected"))
            fhArray.add(new ResultDescription("Poop Collected", obj.get("poop_collected").getAsString()));
        if (fhArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : fhArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Farm Hunt", "Click here to view statistics from Farm Hunt", true, msg.toString()));
        }

        //Blocking Dead
        ArrayList<ResultDescription> tbdArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>The Blocking Dead</i>", null, false, true));
        if (obj.has("headshots_dayone"))
            tbdArray.add(new ResultDescription("Headshots", obj.get("headshots_dayone").getAsString()));
        if (obj.has("kills_dayone"))
            tbdArray.add(new ResultDescription("Kills", obj.get("kills_dayone").getAsString()));
        if (obj.has("wins_dayone"))
            tbdArray.add(new ResultDescription("Wins", obj.get("wins_dayone").getAsString()));
        if (tbdArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tbdArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("The Blocking Dead", "Click here to view statistics from The Blocking Dead", true, msg.toString()));
        }

        //Throwout
        ArrayList<ResultDescription> toArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Throwout</i>", null, false, true));
        if (obj.has("wins_throw_out"))
            toArray.add(new ResultDescription("Wins", obj.get("wins_throw_out").getAsString()));
        if (obj.has("kills_throw_out"))
            toArray.add(new ResultDescription("Kills", obj.get("kills_throw_out").getAsString()));
        if (obj.has("deaths_throw_out"))
            toArray.add(new ResultDescription("Deaths", obj.get("deaths_throw_out").getAsString()));
        if (toArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : toArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Throwout", "Click here to view statistics from Throwout", true, msg.toString()));
        }

        //Dragon Wars
        ArrayList<ResultDescription> dwArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Dragon Wars</i>", null, false, true));
        if (obj.has("kills_dragonwars2"))
            dwArray.add(new ResultDescription("Kills", obj.get("kills_dragonwars2").getAsString()));
        if (obj.has("wins_dragonwars2"))
            dwArray.add(new ResultDescription("Wins", obj.get("wins_dragonwars2").getAsString()));
        if (dwArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : dwArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Dragon Wars", "Click here to view statistics from Dragon Wars", true, msg.toString()));
        }

        //Galaxy Wars
        ArrayList<ResultDescription> gwArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Dragon Wars</i>", null, false, true));
        if (obj.has("sw_kills"))
            gwArray.add(new ResultDescription("Kills", obj.get("sw_kills").getAsString()));
        if (obj.has("sw_shots_fired"))
            gwArray.add(new ResultDescription("Shots Fired", obj.get("sw_shots_fired").getAsString()));
        if (obj.has("sw_rebel_kills"))
            gwArray.add(new ResultDescription("Rebel Kills", obj.get("sw_rebel_kills").getAsString()));
        if (obj.has("sw_deaths"))
            gwArray.add(new ResultDescription("Deaths", obj.get("sw_deaths").getAsString()));
        if (obj.has("sw_empire_kills"))
            gwArray.add(new ResultDescription("Empire Kills", obj.get("sw_empire_kills").getAsString()));
        if (gwArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : gwArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Galaxy Wars", "Click here to view statistics from Galaxy Wars", true, msg.toString()));
        }

        //Build Battle
        ArrayList<ResultDescription> bbArray = new ArrayList<>();
        if (obj.has("wins_buildbattle"))
            bbArray.add(new ResultDescription("Wins (Solo)", obj.get("wins_buildbattle").getAsString()));
        if (obj.has("wins_buildbattle_teams"))
            bbArray.add(new ResultDescription("Wins (Teams)", obj.get("wins_buildbattle_teams").getAsString()));
        if (bbArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : bbArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Build Battle", "Click here to view statistics from Build Battle", true, msg.toString()));
        }

        return descArray;
    }

    /**
     * Arena Game
     * displayed: active_rune, chest_opens, coins, coins_spent, keys, magical_chest, rating,
     * support, ultimate, utility, offensive,
     * damage_2v2, deaths_2v2, games_2v2, healed_2v2, kills_2v2, losses_2v2, wins_2v2, win_streaks_2v2
     * damage_4v4, deaths_4v4, games_4v4, healed_4v4, kills_4v4, losses_4v4, wins_4v4, win_streaks_4v4
     * damage_ffa, deaths_ffa, games_ffa, healed_ffa, kills_ffa, losses_ffa, wins_ffa, win_streaks_ffa
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseArena(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Arena Brawl</b>", null, false, true));
        if (obj.has("rating"))
            descArray.add(new ResultDescription("Arena Rating", obj.get("rating").getAsString()));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("coins_spent"))
            descArray.add(new ResultDescription("Total Coins Spent", obj.get("coins_spent").getAsString()));
        if (obj.has("active_rune"))
            descArray.add(new ResultDescription("Active Rune", obj.get("active_rune").getAsString()));
        if (obj.has("chest_opens"))
            descArray.add(new ResultDescription("Total Chest Opened", obj.get("chest_opens").getAsString()));
        if (obj.has("keys"))
            descArray.add(new ResultDescription("Keys", obj.get("keys").getAsString()));
        if (obj.has("magical_chest"))
            descArray.add(new ResultDescription("Total No of times Magical Chest Opened", obj.get("magical_chest").getAsString()));

        ArrayList<ResultDescription> eqArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Equips</i>", null, false, true));
        if (obj.has("offensive"))
            eqArray.add(new ResultDescription("Offensive", obj.get("offensive").getAsString()));
        if (obj.has("support"))
            eqArray.add(new ResultDescription("Support", obj.get("support").getAsString()));
        if (obj.has("utility"))
            eqArray.add(new ResultDescription("Utility", obj.get("utility").getAsString()));
        if (obj.has("ultimate"))
            eqArray.add(new ResultDescription("Ultimate", obj.get("ultimate").getAsString()));
        if (eqArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : eqArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Equips", "Click here to view Arena Equips", true, msg.toString()));
        }

        ArrayList<ResultDescription> twoArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>2v2</i>", null, false, true));
        if (obj.has("damage_2v2"))
            twoArray.add(new ResultDescription("Total Damage Dealt", obj.get("damage_2v2").getAsString()));
        if (obj.has("deaths_2v2"))
            twoArray.add(new ResultDescription("Deaths", obj.get("deaths_2v2").getAsString()));
        if (obj.has("games_2v2"))
            twoArray.add(new ResultDescription("Games Played", obj.get("games_2v2").getAsString()));
        if (obj.has("healed_2v2"))
            twoArray.add(new ResultDescription("Total Health Healed", obj.get("healed_2v2").getAsString()));
        if (obj.has("kills_2v2"))
            twoArray.add(new ResultDescription("Kills", obj.get("kills_2v2").getAsString()));
        if (obj.has("losses_2v2"))
            twoArray.add(new ResultDescription("Games Lost", obj.get("losses_2v2").getAsString()));
        if (obj.has("wins_2v2"))
            twoArray.add(new ResultDescription("Games Won", obj.get("wins_2v2").getAsString()));
        if (obj.has("win_streaks_2v2"))
            twoArray.add(new ResultDescription("Longest Win Streak", obj.get("win_streaks_2v2").getAsString()));
        if (twoArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : twoArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("2v2", "Click here to view statistics from 2v2 Arena", true, msg.toString()));
        }

        ArrayList<ResultDescription> fourArr = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>4v4</i>", null, false, true));
        if (obj.has("damage_4v4"))
            fourArr.add(new ResultDescription("Total Damage Dealt", obj.get("damage_4v4").getAsString()));
        if (obj.has("deaths_4v4"))
            fourArr.add(new ResultDescription("Deaths", obj.get("deaths_4v4").getAsString()));
        if (obj.has("games_4v4"))
            fourArr.add(new ResultDescription("Games Played", obj.get("games_4v4").getAsString()));
        if (obj.has("healed_4v4"))
            fourArr.add(new ResultDescription("Total Health Healed", obj.get("healed_4v4").getAsString()));
        if (obj.has("kills_4v4"))
            fourArr.add(new ResultDescription("Kills", obj.get("kills_4v4").getAsString()));
        if (obj.has("losses_4v4"))
            fourArr.add(new ResultDescription("Games Lost", obj.get("losses_4v4").getAsString()));
        if (obj.has("wins_4v4"))
            fourArr.add(new ResultDescription("Games Won", obj.get("wins_4v4").getAsString()));
        if (obj.has("win_streaks_4v4"))
            fourArr.add(new ResultDescription("Longest Win Streak", obj.get("win_streaks_4v4").getAsString()));
        if (fourArr.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : fourArr){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("4v4", "Click here to view statistics from 4v4 Arena", true, msg.toString()));
        }

        ArrayList<ResultDescription> ffaArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>Free For All</i>", null, false, true));
        if (obj.has("damage_ffa"))
            ffaArray.add(new ResultDescription("Total Damage Dealt", obj.get("damage_ffa").getAsString()));
        if (obj.has("deaths_ffa"))
            ffaArray.add(new ResultDescription("Deaths", obj.get("deaths_ffa").getAsString()));
        if (obj.has("games_ffa"))
            ffaArray.add(new ResultDescription("Games Played", obj.get("games_ffa").getAsString()));
        if (obj.has("healed_ffa"))
            ffaArray.add(new ResultDescription("Total Health Healed", obj.get("healed_ffa").getAsString()));
        if (obj.has("kills_ffa"))
            ffaArray.add(new ResultDescription("Kills", obj.get("kills_ffa").getAsString()));
        if (obj.has("losses_ffa"))
            ffaArray.add(new ResultDescription("Games Lost", obj.get("losses_ffa").getAsString()));
        if (obj.has("wins_ffa"))
            ffaArray.add(new ResultDescription("Games Won", obj.get("wins_ffa").getAsString()));
        if (obj.has("win_streaks_ffa"))
            ffaArray.add(new ResultDescription("Longest Win Streak", obj.get("win_streaks_ffa").getAsString()));
        if (ffaArray.size() > 0){
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : ffaArray){
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("Free For All (FFA)", "Click here to view statistics from FFA Arena", true, msg.toString()));
        }
        return descArray;
    }

    /**
     * Legacy Spleef Game?
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseSpleef(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Legacy Spleef</b>", null, false, true));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        return descArray;
    }

    /**
     * TNT Games (TNT Wizards, Bow Spleef, TNT Run, TNT Tag
     * TNT Wizards - capture, Bow Spleef - bowspleef, TNT Tag - tnttag, TNT Run - tntrun
     * displayed: coins, selected_hat, wins_capture, kills_capture, deaths_capture, deaths_bowspleef, wins_bowspleef, wins_tntag,
     * wins_tntrun
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseTntGames(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>TNT Games</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("selected_hat"))
            if (obj.get("selected_hat").isJsonNull())
                descArray.add(new ResultDescription("Selected Hat", "null"));
            else
                descArray.add(new ResultDescription("Selected Hat", obj.get("selected_hat").getAsString()));

        ArrayList<ResultDescription> tntWArr = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>TNT Wizards</i>", null, false, true));
        if (obj.has("wins_capture"))
            tntWArr.add(new ResultDescription("Wins", obj.get("wins_capture").getAsString()));
        if (obj.has("kills_capture"))
            tntWArr.add(new ResultDescription("Kills", obj.get("kills_capture").getAsString()));
        if (obj.has("deaths_capture"))
            tntWArr.add(new ResultDescription("Deaths", obj.get("deaths_capture").getAsString()));
        if (tntWArr.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tntWArr) {
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("TNT Wizards", "Click here to view statistics from TNT Wizards", true, msg.toString()));
        }

        ArrayList<ResultDescription> tntBSArr = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>TNT Bow Spleef</i>", null, false, true));
        if (obj.has("deaths_bowspleef"))
            tntBSArr.add(new ResultDescription("Deaths", obj.get("deaths_bowspleef").getAsString()));
        if (obj.has("wins_bowspleef"))
            tntBSArr.add(new ResultDescription("Wins", obj.get("wins_bowspleef").getAsString()));
        if (tntBSArr.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tntBSArr) {
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("TNT Bow Spleef", "Click here to view statistics from Bow Spleef", true, msg.toString()));
        }

        ArrayList<ResultDescription> tntTRArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<i>TNT Tag/TNT Run</i>", null, false, true));
        if (obj.has("wins_tntag"))
            tntTRArray.add(new ResultDescription("TNTTag Wins", obj.get("wins_tntag").getAsString()));
        if (obj.has("wins_tntrun"))
            tntTRArray.add(new ResultDescription("TNTRun Wins", obj.get("wins_tntrun").getAsString()));
        if (tntTRArray.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (ResultDescription t : tntTRArray) {
                msg.append(t.get_title()).append(": ").append(t.get_result()).append("<br />");
            }
            descArray.add(new ResultDescription("TNT Tag/TNT Run", "Click here to view statistics from the 2 games", true, msg.toString()));
        }
        return descArray;
    }

    /**
     * Paintball Game
     * displayed: coins, deaths, wins, kills, killstreaks, shots_fired, hat, packages
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parsePaintball(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Paintball</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("shots_fired"))
            descArray.add(new ResultDescription("Total Shots Fired", obj.get("shots_fired").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("killstreaks"))
            descArray.add(new ResultDescription("Longest Killstreak", obj.get("killstreaks").getAsString()));
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
            descArray.add(new ResultDescription("Packages", packageBuilder.toString()));
        }
        return descArray;
    }

    /**
     * MC GO (Cops and Crims)
     * displayed: bombs_defused, bombs_planted, coins, deaths, game_wins, headshot_kills, kills, round_wins,
     * shots_fired, cop_kills, criminal_kills, packages
     * @param obj Statistics
     */
    private ArrayList<ResultDescription> parseMcGo(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        //descArray.add(new ResultDescription("<b>Cops and Crims</b>", null, false, true));
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("game_wins"))
            descArray.add(new ResultDescription("Game Wins", obj.get("game_wins").getAsString()));
        if (obj.has("round_wins"))
            descArray.add(new ResultDescription("Round Wins", obj.get("round_wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("bombs_defused"))
            descArray.add(new ResultDescription("Bombs Defused", obj.get("bombs_defused").getAsString()));
        if (obj.has("bombs_planted"))
            descArray.add(new ResultDescription("Bombs Planted", obj.get("bombs_planted").getAsString()));
        if (obj.has("shots_fired"))
            descArray.add(new ResultDescription("Total Shots Fired", obj.get("shots_fired").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Total Kills", obj.get("kills").getAsString()));
        if (obj.has("headshot_kills"))
            descArray.add(new ResultDescription("Total Headshot Kills", obj.get("headshot_kills").getAsString()));
        if (obj.has("cop_kills"))
            descArray.add(new ResultDescription("Total Cops Kills", obj.get("cop_kills").getAsString()));
        if (obj.has("criminal_kills"))
            descArray.add(new ResultDescription("Total Criminals Kills", obj.get("criminal_kills").getAsString()));
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
            descArray.add(new ResultDescription("Packages", packageBuilder.toString()));
        }
        return descArray;
    }

    /**
     * UHC (UHC Champions)
     * displayed: coins, score, rank, wins, deaths, kills, heads_eaten, equippedKit
     * @param obj Statistics
     * @return parsed List
     */
    private ArrayList<ResultDescription> parseUHC(JsonObject obj){
        ArrayList<ResultDescription> descArray = new ArrayList<>();
        if (obj.has("coins"))
            descArray.add(new ResultDescription("Coins", obj.get("coins").getAsString()));
        if (obj.has("score")) {
            descArray.add(new ResultDescription("Score", obj.get("score").getAsInt() + ""));
            descArray.add(new ResultDescription("Ranking", getUHCRank(obj.get("score").getAsInt())));
        } else {
            descArray.add(new ResultDescription("Score", "0"));
            descArray.add(new ResultDescription("Ranking", getUHCRank(0)));
        }
        if (obj.has("wins"))
            descArray.add(new ResultDescription("Wins", obj.get("wins").getAsString()));
        if (obj.has("deaths"))
            descArray.add(new ResultDescription("Deaths", obj.get("deaths").getAsString()));
        if (obj.has("kills"))
            descArray.add(new ResultDescription("Kills", obj.get("kills").getAsString()));
        if (obj.has("heads_eaten"))
            descArray.add(new ResultDescription("Heads Eaten", obj.get("heads_eaten").getAsString()));
        if (obj.has("equippedKit"))
            descArray.add(new ResultDescription("Equipped Kit", obj.get("equippedKit").getAsString()));
        return descArray;
    }

    private String getUHCRank(int score){
        if (score > 10210)
            return "Champion";
        if (score > 5210)
            return "Warlord";
        if (score > 2710)
            return "Gladiator";
        if (score > 1710)
            return "Centurion";
        if (score > 960)
            return "Captain";
        if (score > 460)
            return "Knight";
        if (score > 210)
            return "Sergeant";
        if (score > 60)
            return "Soldier";
        if (score > 10)
            return "Initiate";
        return "Recruit";
    }

    private ArrayList<ResultDescription> parseWarlords(JsonObject obj){
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
    private ArrayList<ResultDescription> parseIndividualWarlordsStats(JsonObject obj, String className, String title, ArrayList<ResultDescription> descArray){
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
