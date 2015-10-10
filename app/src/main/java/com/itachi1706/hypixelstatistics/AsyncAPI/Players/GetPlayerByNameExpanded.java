package com.itachi1706.hypixelstatistics.AsyncAPI.Players;

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

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.AsyncAPI.Session.GetSessionInfoPlayerStats;
import com.itachi1706.hypixelstatistics.ListViewAdapters.ExpandedResultDescListAdapter;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.PlayerStatistics.DonatorStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatisticsHandler;
import com.itachi1706.hypixelstatistics.PlayerStatistics.GeneralStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.OngoingAchievementStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.ParkourStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.QuestStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.StaffOrYtStatistics;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.PlayerReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
@Deprecated
public class GetPlayerByNameExpanded extends AsyncTask<String,Void,String> {

    TextView debug, result, sessionTV;
    ExpandableListView details;
    Activity mContext;
    Exception except = null;
    ImageView ivHead;
    ProgressDialog progress;
    ProgressBar pro;
    boolean isUUID;

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
        String url = MainStaticVars.API_BASE_URL + "?type=player";
        if (!isUUID) {
            url += "&name=" + playerName[0];
        } else {
            url += "&uuid=" + playerName[0];
        }
        url = MainStaticVars.updateURLWithApiKeyIfExists(url);
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
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again");
                else
                    NotifyUserUtil.createShortToast(mContext, "An error occured. (Invalid JSON String) Please Try Again");
                return;
            }
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            debug.setText(json);
            ivHead.setImageDrawable(null);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                result.setText(reply.getCause());
                NotifyUserUtil.createShortToast(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
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
                    NotifyUserUtil.createShortToast(mContext, "Unable to find a player with this UUID. If you are searching with a name, select Search with Name option in the menu!");
                } else {
                    result.setText("Invalid Player");
                    result.setTextColor(Color.RED);
                    NotifyUserUtil.createShortToast(mContext, "Unable to find this player. If you are searching with a UUID, select Search with UUID option in the menu!");
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

                //Get Local Player Name
                String localPlayerName;
                if (MinecraftColorCodes.checkDisplayName(reply))
                    localPlayerName = reply.getPlayer().get("displayname").getAsString();
                else
                    localPlayerName = reply.getPlayer().get("playername").getAsString();

                //Parse
                resultArray.add(new ResultDescription("<b>General Statistics</b>", null, false, GeneralStatistics.parseGeneral(reply, localPlayerName), null));

                if (reply.getPlayer().has("packageRank")) {
                    resultArray.add(new ResultDescription("<b>Donator Information</b>", null, false, DonatorStatistics.parseDonor(reply), null));
                }

                if (MainStaticVars.isStaff || MainStaticVars.isCreator) {
                    if (reply.getPlayer().has("rank")) {
                        if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")) {
                            if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")) {
                                resultArray.add(new ResultDescription("<b>YouTuber Information</b>", null, false, StaffOrYtStatistics.parsePriviledged(reply), null));
                            } else {
                                resultArray.add(new ResultDescription("<b>Staff Information</b>", null, false, StaffOrYtStatistics.parsePriviledged(reply), null));
                            }
                        }
                    }
                }

                if (reply.getPlayer().has("achievements")){
                    resultArray.add(new ResultDescription("<b>Ongoing Achievements</b>", null, false, OngoingAchievementStatistics.parseOngoingAchievements(reply), null));
                }

                if (reply.getPlayer().has("quests")){
                    resultArray.add(new ResultDescription("<b>Quest Stats</b>", null, false, QuestStatistics.parseQuests(reply), null));
                }
                if (reply.getPlayer().has("parkourCompletions")) {
                    resultArray.add(new ResultDescription("<b>Parkour Stats</b>", null, false, ParkourStatistics.parseParkourCounts(reply), null));
                }

                if (reply.getPlayer().has("stats")){
                    ArrayList<ResultDescription> tmp = GameStatisticsHandler.parseStats(reply, localPlayerName);
                    for (ResultDescription t : tmp){
                        resultArray.add(t);
                    }
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
        Log.d("HISTORY STRING", hist == null ? "No History" : hist);
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            Log.d("HISTORY ORIGINAL", histCheck.toString());
            for (HistoryArrayObject histCheckName : histCheck) {
                if (histCheckName.getPlayername().equals(reply.getPlayer().get("playername").getAsString())) {
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
}
