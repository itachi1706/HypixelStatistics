package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetPlayerByName extends AsyncTask<String,Void,String> {

    TextView debug, result;
    ListView details;
    Context mContext;
    Exception except = null;
    Drawable playerHead = null;
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
                resultArray = new ArrayList<ResultDescription>();
                progress.dismiss();
                pro.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                new GetPlayerHead(pro, ivHead, mContext).execute(reply.getPlayer().get("displayname").getAsString());
                result.setText(Html.fromHtml("Success! Statistics for <br />" + MinecraftColorCodes.parseHypixelRanks(reply)));
                result.setTextColor(Color.GREEN);
                //Parse
                StringBuilder builder = new StringBuilder();
                resultArray.add(new ResultDescription("<b>General Statistics</b>", null, false));
                builder.append(parseGeneral(reply));

                if (reply.getPlayer().has("packageRank")) {
                    resultArray.add(new ResultDescription("<b>Donator Information</b>", null, false));
                    builder.append(parseDonor(reply));
                }

                if (reply.getPlayer().has("rank")){
                    if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")){
                        if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")){
                            resultArray.add(new ResultDescription("<b>YouTuber Information</b>", null, false));
                        } else {
                            resultArray.add(new ResultDescription("<b>Staff Information</b>", null, false));
                        }
                        builder.append(parsePriviledged(reply));
                    }
                }

                if (reply.getPlayer().has("achievements")){
                    resultArray.add(new ResultDescription("<b>Achievements</b>", null, false));
                    builder.append(parseOngoingAchievements(reply));
                }

                //TODO Add (if present) stats parse, parkour parse, quest parse

                //TODO Final formatting parse
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
    private String parseGeneral(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
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
            resultArray.add(new ResultDescription("Veteran Donor: ", "true "));
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
            resultArray.add(new ResultDescription("Current Chat Channel: ", "ALL "));
        if (reply.getPlayer().has("chat")) {
            if (reply.getPlayer().get("chat").getAsBoolean())
                resultArray.add(new ResultDescription("Chat Enabled: ", "Enabled "));
            else
                resultArray.add(new ResultDescription("Chat Enabled: ", "Disabled "));
        } else
            resultArray.add(new ResultDescription("Chat Enabled: ", "Enabled "));
        if (reply.getPlayer().has("tournamentTokens"))
            resultArray.add(new ResultDescription("Tournament Tokens: ",reply.getPlayer().get("tournamentTokens").getAsString()));
        else
            resultArray.add(new ResultDescription("Tournament Tokens: ", "0 "));
        if (reply.getPlayer().has("vanityTokens"))
            resultArray.add(new ResultDescription("Vanity Tokens: ",reply.getPlayer().get("vanityTokens").getAsString()));
        else
            resultArray.add(new ResultDescription("Vanity Tokens: ", "0 "));
        if (reply.getPlayer().has("mostRecentGameType"))
            resultArray.add(new ResultDescription("Last Game Played: ",reply.getPlayer().get("mostRecentGameType").getAsString()));
        if (reply.getPlayer().has("seeRequests")) {
            if (reply.getPlayer().get("seeRequests").getAsBoolean())
                resultArray.add(new ResultDescription("Friend Requests: ", "Enabled "));
            else
                resultArray.add(new ResultDescription("Friend Requests: ", "Disabled "));
        } else
            resultArray.add(new ResultDescription("Friend Requests: ", "Enabled "));
        if (reply.getPlayer().has("achievementsOneTime"))
            resultArray.add(new ResultDescription("No of 1-time Achievements Done: ", reply.getPlayer().getAsJsonArray("achievementsOneTime").size() + ""));
        return tmp.toString();
    }

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    private String parseDonor(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("fly"))
            resultArray.add(new ResultDescription("Fly Mode: ", reply.getPlayer().get("fly").getAsString()));
        if (reply.getPlayer().has("petActive"))
            resultArray.add(new ResultDescription("Active Pet: ", reply.getPlayer().get("petActive").getAsString()));
        else
            resultArray.add(new ResultDescription("Active Pet: ", "false "));
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
        return tmp.toString();
    }

    /* Staff/YT Only Information
        vanished, stoggle, silence, chatTunnel, nick, prefix
     */
    private String parsePriviledged(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("vanished"))
            resultArray.add(new ResultDescription("Vanished: ", reply.getPlayer().get("vanished").getAsString()));
        if (reply.getPlayer().has("stoggle")) {
            if (reply.getPlayer().get("stoggle").getAsBoolean())
                resultArray.add(new ResultDescription("Staff Chat: " , "Enabled "));
            else
                resultArray.add(new ResultDescription("Staff Chat: ", "Disabled "));
        }
        if (reply.getPlayer().has("silence"))
            resultArray.add(new ResultDescription("Chat Silenced: ", reply.getPlayer().get("silence").getAsString()));
        if (reply.getPlayer().has("chatTunnel")) {
            if (reply.getPlayer().get("chatTunnel").isJsonNull())
                resultArray.add(new ResultDescription("Tunneled Into: ", "None "));
            else
                resultArray.add(new ResultDescription("Tunneled Into: ", reply.getPlayer().get("chatTunnel").getAsString()));
        }
        if (reply.getPlayer().has("nick"))
            resultArray.add(new ResultDescription("Nicked As: ", reply.getPlayer().get("nick").getAsString()));
        if (reply.getPlayer().has("prefix"))
            resultArray.add(new ResultDescription("Rank Prefix: ", reply.getPlayer().get("prefix").getAsString()));
        return tmp.toString();
    }

    /**
     * Parse statistics (Split based on GameType)
     * @param reply PlayerReply object
     * @return result
     */
    private String parseStats(PlayerReply reply){
        //TODO Parse the Statistics based on the gameType
        return null;
    }

    /**
     * Parse Ongoing Achievements
     * @param reply PlayerReply object
     * @return result
     */
    private String parseOngoingAchievements(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        JsonObject achievements = reply.getPlayer().getAsJsonObject("achievements");
        for (Map.Entry<String, JsonElement> entry : achievements.entrySet()){
            resultArray.add(new ResultDescription(entry.getKey() + ": ", entry.getValue().toString()));
        }
        return tmp.toString();
    }

    /**
     * Parse Lobby Parkour Staistics
     * @param reply PlayerReply object
     * @return result
     */
    private String parseParkourCounts(PlayerReply reply){
        //TODO Parse the number of times a parkour is completed
        return null;
    }

    /**
     * Parse the Quests statistics
     * @param reply PlayerReply object
     * @return Statistics string
     */
    private String parseQuests(PlayerReply reply){
        //TODO Do the string for parsing the number of time a quest is completed/active
        //TODO If a quest is active, show when its started
        return null;
    }
}
