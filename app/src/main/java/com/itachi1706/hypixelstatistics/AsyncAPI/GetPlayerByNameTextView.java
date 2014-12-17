package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.OldPlayerInfoActivity;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

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
import java.util.Date;
import java.util.Map;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
@Deprecated
public class GetPlayerByNameTextView extends AsyncTask<String,Void,String> {

    TextView debug,result, details;
    Context mContext;
    Exception except = null;
    ImageView ivHead;
    ProgressDialog progress;
    ProgressBar pro;

    public GetPlayerByNameTextView(TextView resultView, TextView debugView, TextView general, ImageView head, ProgressDialog prog, ProgressBar header, Context context){
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
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey + "&name=" + playerName[0];
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
            } else if (!reply.isSuccess()){
                //Not Successful
                progress.dismiss();
                result.setText(reply.getCause());
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
                details.setText("");
            } else if (reply.getPlayer() == null) {
                progress.dismiss();
                result.setText("Invalid Player");
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: Invalid Player Name (" + reply.getCause() + ")");
                details.setText("");
            } else {
                //Succeeded
                //progress.setMessage("Getting Player Head for " + reply.getPlayer().get("displayname").getAsString() + "...");
                progress.dismiss();
                pro.setVisibility(View.VISIBLE);
                if (MinecraftColorCodes.checkDisplayName(reply))
                    new GetPlayerHead(pro, ivHead, mContext).execute(reply.getPlayer().get("displayname").getAsString());
                else
                    pro.setVisibility(View.GONE);
                result.setText(Html.fromHtml("Success! Statistics for <br /> " + MinecraftColorCodes.parseHypixelRanks(reply)));
                result.setTextColor(Color.GREEN);
                //Parse
                StringBuilder builder = new StringBuilder();
                builder.append("<b><u>General Statistics</u></b><br />");
                builder.append(parseGeneral(reply));

                if (reply.getPlayer().has("packageRank")) {
                    builder.append("<br /><br /><b><u>Donator Information</u></b><br />");
                    builder.append(parseDonor(reply));
                }

                if (MainStaticVars.isStaff) {
                    if (reply.getPlayer().has("rank")) {
                        if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")) {
                            if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")) {
                                builder.append("<br /><br /><b><u>YouTuber Information</u></b><br />");
                            } else {
                                builder.append("<br /><br /><b><u>Staff Information</u></b><br />");
                            }
                            builder.append(parsePriviledged(reply));
                        }
                    }
                }

                if (reply.getPlayer().has("achievements")){
                    builder.append("<br /><br /><b><u>Achievements</u></b><br />");
                    builder.append(parseOngoingAchievements(reply));
                }
                details.setText(Html.fromHtml(builder.toString()));
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
            tmp.append("Rank: ").append(reply.getPlayer().get("rank").getAsString()).append("<br />");
        else
            tmp.append("Rank: NORMAL<br />");
        if (MinecraftColorCodes.checkDisplayName(reply))
            tmp.append("Name: ").append(reply.getPlayer().get("displayname").getAsString()).append("<br />");
        else
            tmp.append("Name: ").append(reply.getPlayer().get("playername").getAsString()).append("<br />");
        tmp.append("UUID: ").append(reply.getPlayer().get("uuid").getAsString()).append("<br />");
        if (reply.getPlayer().has("packageRank"))
            tmp.append("Donor Rank: ").append(reply.getPlayer().get("packageRank").getAsString()).append("<br />");
        if (reply.getPlayer().has("disguise"))
            tmp.append("Disguise: ").append(reply.getPlayer().get("disguise").getAsString()).append("<br />");
        if (reply.getPlayer().has("eulaCoins"))
            tmp.append("Veteran Donor: true <br />");
        if (reply.getPlayer().has("gadget"))
            tmp.append("Lobby Gadget: ").append(reply.getPlayer().get("gadget").getAsString()).append("<br />");
        if (reply.getPlayer().has("karma"))
            tmp.append("Karma: ").append(reply.getPlayer().get("karma").getAsString()).append("<br />");
        if (reply.getPlayer().has("firstLogin"))
        tmp.append("First Login: ").append(new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("firstLogin").getAsLong()))).append("<br />");
        if (reply.getPlayer().has("lastLogin"))
        tmp.append("Last Login: ").append(new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("lastLogin").getAsLong()))).append("<br />");
        tmp.append("Time Played: ").append(MinecraftColorCodes.parseColors("§cComing Soon™§r")).append(" <br />");
        if (reply.getPlayer().has("networkExp"))
            tmp.append("Network XP: ").append(reply.getPlayer().get("networkExp").getAsString()).append("<br />");
        if (reply.getPlayer().has("networkLevel"))
            tmp.append("Network Level: ").append(reply.getPlayer().get("networkLevel").getAsString()).append("<br />");
        else
            tmp.append("Network Level: 1<br />");
        if (reply.getPlayer().has("mostRecentlyThanked"))
            tmp.append("Last Thanked: ").append(reply.getPlayer().get("mostRecentlyThanked").getAsString()).append("<br />");
        if (reply.getPlayer().has("mostRecentlyTipped"))
            tmp.append("Last Tipped: ").append(reply.getPlayer().get("mostRecentlyTipped").getAsString()).append("<br />");
        if (reply.getPlayer().has("thanksSent"))
            tmp.append("No of Thanks sent: ").append(reply.getPlayer().get("thanksSent").getAsString()).append("<br />");
        if (reply.getPlayer().has("tipsSent"))
            tmp.append("No of Tips sent: ").append(reply.getPlayer().get("tipsSent").getAsString()).append("<br />");
        if (reply.getPlayer().has("thanksReceived"))
            tmp.append("No of Thanks received: ").append(reply.getPlayer().get("thanksReceived").getAsString()).append("<br />");
        if (reply.getPlayer().has("tipsReceived"))
            tmp.append("No of Tips sent received: ").append(reply.getPlayer().get("tipsReceived").getAsString()).append("<br />");
        if (reply.getPlayer().has("channel"))
            tmp.append("Current Chat Channel: ").append(reply.getPlayer().get("channel").getAsString()).append("<br />");
        else
            tmp.append("Current Chat Channel: ALL <br />");
        if (reply.getPlayer().has("chat")) {
            if (reply.getPlayer().get("chat").getAsBoolean())
                tmp.append("Chat Enabled: Disabled <br />");
            else
                tmp.append("Chat Enabled: Disabled <br />");
        } else
            tmp.append("Chat Enabled: Enabled <br />");
        if (reply.getPlayer().has("tournamentTokens"))
            tmp.append("Tournament Tokens: ").append(reply.getPlayer().get("tournamentTokens").getAsString()).append("<br />");
        else
            tmp.append("Tournament Tokens: 0 <br />");
        if (reply.getPlayer().has("vanityTokens"))
            tmp.append("Vanity Tokens: ").append(reply.getPlayer().get("vanityTokens").getAsString()).append("<br />");
        else
            tmp.append("Vanity Tokens: 0 <br />");
        if (reply.getPlayer().has("mostRecentGameType"))
            tmp.append("Last Game Played: ").append(reply.getPlayer().get("mostRecentGameType").getAsString()).append("<br />");
        if (reply.getPlayer().has("seeRequests")) {
            if (reply.getPlayer().get("seeRequests").getAsBoolean())
                tmp.append("Friend Requests: Enabled <br />");
            else
                tmp.append("Friend Requests: Disabled <br />");
        } else
            tmp.append("Friend Requests: Enabled <br />");
        if (reply.getPlayer().has("achievementsOneTime"))
            tmp.append("No of 1-time Achievements Done: ").append(reply.getPlayer().getAsJsonArray("achievementsOneTime").size()).append("<br />");
        return tmp.toString();
    }

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    private String parseDonor(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("fly"))
            tmp.append("Fly Mode: ").append(reply.getPlayer().get("fly").getAsString()).append("<br />");
        if (reply.getPlayer().has("petActive"))
            tmp.append("Active Pet: ").append(reply.getPlayer().get("petActive").getAsString()).append("<br />");
        else
            tmp.append("Active Pet: false <br />");
        if (reply.getPlayer().has("pp"))
            tmp.append("Particle Pack: ").append(reply.getPlayer().get("pp").getAsString()).append("<br />");
        if (reply.getPlayer().has("testpass"))
            tmp.append("Test Server Access: ").append(reply.getPlayer().get("testpass").getAsString()).append("<br />");
        if (reply.getPlayer().has("wardrobe"))
            tmp.append("Wardrobe (H,C,L,B): ").append(reply.getPlayer().get("wardrobe").getAsString()).append("<br />");
        if (reply.getPlayer().has("auto_spawn_pet"))
            tmp.append("Auto-Spawn Pet: ").append(reply.getPlayer().get("auto_spawn_pet").getAsString()).append("<br />");
        if (reply.getPlayer().has("legacyGolem"))
            tmp.append("Golem Supporter: ").append(reply.getPlayer().get("legacyGolem").getAsString()).append("<br />");
        return tmp.toString();
    }

    /* Staff/YT Only Information
        vanished, stoggle, silence, chatTunnel, nick, prefix
     */
    private String parsePriviledged(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("vanished"))
            tmp.append("Vanished: ").append(reply.getPlayer().get("vanished").getAsString()).append("<br />");
        if (reply.getPlayer().has("stoggle")) {
            if (reply.getPlayer().get("stoggle").getAsBoolean())
                tmp.append("Staff Chat: Enabled <br />");
            else
                tmp.append("Staff Chat: Disabled <br />");
        }
        if (reply.getPlayer().has("silence"))
            tmp.append("Chat Silenced: ").append(reply.getPlayer().get("silence").getAsString()).append("<br />");
        if (reply.getPlayer().has("chatTunnel")) {
            if (reply.getPlayer().get("chatTunnel").isJsonNull())
                tmp.append("Tunneled Into: None <br />");
            else
                tmp.append("Tunneled Into: ").append(reply.getPlayer().get("chatTunnel").getAsString()).append("<br />");
        }
        if (reply.getPlayer().has("nick"))
            tmp.append("Nicked As: ").append(reply.getPlayer().get("nick").getAsString()).append("<br />");
        if (reply.getPlayer().has("prefix"))
            tmp.append("Rank Prefix: ").append(reply.getPlayer().get("prefix").getAsString()).append("<br />");
        return tmp.toString();
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
            tmp.append(entry.getKey()).append(": ").append(entry.getValue()).append("<br />");
        }
        return tmp.toString();
    }
}
