package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.PlayerInfoActivity;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PlayerReply;

import org.apache.commons.lang3.ObjectUtils;
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

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetPlayerByName extends AsyncTask<String,Void,String> {

    TextView debug,result, details;
    Context mContext;
    Exception except = null;

    public GetPlayerByName(TextView resultView, TextView debugView, TextView general, Context context){
        debug = debugView;
        result = resultView;
        mContext = context;
        details = general;
    }

    @Override
    protected String doInBackground(String... playerName) {
        String url = HypixelAPI.API_BASE_URL + "player?key=" + mContext.getResources().getString(R.string.hypixel_api_key) + "&name=" + playerName[0];
        String tmp = "";
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
            debug.setText(except.getMessage());
        } else {
            Gson gson = new Gson();
            PlayerInfoActivity.lastGsonObtained = json;
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            //debug.setText(reply.toString());
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                result.setText(reply.getCause());
                Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                result.setTextColor(Color.RED);
            } else if (!reply.isSuccess()){
                //Not Successful
                result.setText(reply.getCause());
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
            } else if (reply.getPlayer() == null) {
                result.setText("Invalid Player");
                result.setTextColor(Color.RED);
                debug.setText("Unsuccessful Query!\n Reason: Invalid Player Name (" + reply.getCause() + ")");
            } else {
                //Succeeded
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

                if (reply.getPlayer().has("rank")){
                    if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")){
                        if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")){
                            builder.append("<br /><br /><b><u>YouTuber Information</u></b><br />");
                        } else {
                            builder.append("<br /><br /><b><u>Staff Information</u></b><br />");
                        }
                        builder.append(parsePriviledged(reply));
                    }
                }

                details.setText(Html.fromHtml(builder.toString()));
            }
        }
    }

    //Parsing General Information
    /*
    rank, displayname, uuid, packageRank, disguise, eulaCoins, gadget, karma, firstLogin, lastLogin, timePlaying, networkExp,
    networkLevel, mostRecentlyThanked, mostRecentlyTipped, thanksSent, tipsSent, channel, chat, tournamentTokens,
    vanityTokens, mostRecentGameType, seeRequest, tipsReceived, thanksReceived
     */
    private String parseGeneral(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("rank"))
            tmp.append("Rank: " + reply.getPlayer().get("rank").getAsString() + "<br />");
        else
            tmp.append("Rank: NORMAL<br />");
        tmp.append("Name: " + reply.getPlayer().get("displayname").getAsString() + "<br />");
        tmp.append("UUID: " + reply.getPlayer().get("uuid").getAsString() + "<br />");
        if (reply.getPlayer().has("packageRank"))
            tmp.append("Donor Rank: " + reply.getPlayer().get("packageRank").getAsString() + "<br />");
        if (reply.getPlayer().has("disguise"))
            tmp.append("Disguise: " + reply.getPlayer().get("disguise").getAsString() + "<br />");
        if (reply.getPlayer().has("eulaCoins"))
            tmp.append("Veteran Donor: true <br />");
        if (reply.getPlayer().has("gadget"))
            tmp.append("Lobby Gadget: " + reply.getPlayer().get("gadget").getAsString() + "<br />");
        if (reply.getPlayer().has("karma"))
            tmp.append("Karma: " + reply.getPlayer().get("karma").getAsString() + "<br />");
        tmp.append("First Login: " + new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("firstLogin").getAsLong())) + "<br />");
        tmp.append("Last Login: " + new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getPlayer().get("lastLogin").getAsLong())) + "<br />");
        //TODO Parse Time Played
        tmp.append("Time Played: Soon™ <br />" );
        if (reply.getPlayer().has("networkExp"))
            tmp.append("Network XP: " + reply.getPlayer().get("networkExp").getAsString() + "<br />");
        if (reply.getPlayer().has("networkLevel"))
            tmp.append("Network Level: " + reply.getPlayer().get("networkLevel").getAsString() + "<br />");
        else
            tmp.append("Network Level: 1<br />");
        if (reply.getPlayer().has("mostRecentlyThanked"))
            tmp.append("Last Thanked: " + reply.getPlayer().get("mostRecentlyThanked").getAsString() + "<br />");
        if (reply.getPlayer().has("mostRecentlyTipped"))
            tmp.append("Last Tipped: " + reply.getPlayer().get("mostRecentlyTipped").getAsString() + "<br />");
        if (reply.getPlayer().has("thanksSent"))
            tmp.append("No of Thanks sent: " + reply.getPlayer().get("thanksSent").getAsString() + "<br />");
        if (reply.getPlayer().has("tipsSent"))
            tmp.append("No of Tips sent Tipped: " + reply.getPlayer().get("tipsSent").getAsString() + "<br />");
        if (reply.getPlayer().has("thanksReceived"))
            tmp.append("No of Thanks received: " + reply.getPlayer().get("thanksReceived").getAsString() + "<br />");
        if (reply.getPlayer().has("tipsReceived"))
            tmp.append("No of Tips sent received: " + reply.getPlayer().get("tipsReceived").getAsString() + "<br />");
        if (reply.getPlayer().has("channel"))
            tmp.append("Current Chat Channel: " + reply.getPlayer().get("channel").getAsString() + "<br />");
        else
            tmp.append("Current Chat Channel: ALL <br />");
        if (reply.getPlayer().has("chat"))
            tmp.append("Chat Enabled: " + reply.getPlayer().get("chat") + "<br />");
        else
            tmp.append("Chat Enabled: true <br />");
        if (reply.getPlayer().has("tournamentTokens"))
            tmp.append("Tournament Tokens: " + reply.getPlayer().get("tournamentTokens").getAsString() + "<br />");
        else
            tmp.append("Tournament Tokens: 0 <br />");
        if (reply.getPlayer().has("vanityTokens"))
            tmp.append("Vanity Tokens: " + reply.getPlayer().get("vanityTokens").getAsString() + "<br />");
        else
            tmp.append("Vanity Tokens: 0 <br />");
        if (reply.getPlayer().has("mostRecentGameType"))
            tmp.append("Last Game Played: " + reply.getPlayer().get("mostRecentGameType").getAsString() + "<br />");
        if (reply.getPlayer().has("seeRequests"))
            tmp.append("Friend Requests: " + reply.getPlayer().get("seeRequests").getAsString() + "<br />");
        else
            tmp.append("Friend Requests: true <br />");
        return tmp.toString();
    }

    /* Donor Only Information
        fly, petActive, pp, testpass wardrobe, auto_spawn_pet, legacyGolem
     */
    private String parseDonor(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("fly"))
            tmp.append("Fly Mode: " + reply.getPlayer().get("fly").getAsString() + "<br />");
        if (reply.getPlayer().has("petActive"))
            tmp.append("Active Pet: " + reply.getPlayer().get("petActive").getAsString() + "<br />");
        else
            tmp.append("Active Pet: false <br />");
        if (reply.getPlayer().has("pp"))
            tmp.append("Particle Pack: " + reply.getPlayer().get("pp").getAsString() + "<br />");
        if (reply.getPlayer().has("testpass"))
            tmp.append("Test Server Access: " + reply.getPlayer().get("testpass").getAsString() + "<br />");
        if (reply.getPlayer().has("wardrobe"))
            tmp.append("Wardrobe (H,C,L,B): " + reply.getPlayer().get("wardrobe").getAsString() + "<br />");
        if (reply.getPlayer().has("auto_spawn_pet"))
            tmp.append("Auto-Spawn Pet: " + reply.getPlayer().get("auto_spawn_pet").getAsString() + "<br />");
        if (reply.getPlayer().has("legacyGolem"))
            tmp.append("Golem Supporter: " + reply.getPlayer().get("legacyGolem").getAsString() + "<br />");
        return tmp.toString();
    }

    /* Staff/YT Only Information
        vanished, stoggle, silence, chatTunnel, nick, prefix
     */
    private String parsePriviledged(PlayerReply reply){
        StringBuilder tmp = new StringBuilder();
        if (reply.getPlayer().has("vanished"))
            tmp.append("Vanished: " + reply.getPlayer().get("vanished").getAsString() + "<br />");
        if (reply.getPlayer().has("stoggle")) {
            if (reply.getPlayer().get("stoggle").getAsBoolean())
                tmp.append("Staff Chat: Enabled <br />");
            else
                tmp.append("Staff Chat: Disabled <br />");
        }
        if (reply.getPlayer().has("silence"))
            tmp.append("Chat Silenced: " + reply.getPlayer().get("silence").getAsString() + "<br />");
        if (reply.getPlayer().has("chatTunnel")) {
            if (reply.getPlayer().get("chatTunnel").isJsonNull())
                tmp.append("Tunneled Into: None <br />");
            else
                tmp.append("Tunned Into: " + reply.getPlayer().get("chatTunnel").getAsString() + "<br />");
        }
        if (reply.getPlayer().has("nick"))
            tmp.append("Nicked As: " + reply.getPlayer().get("nick").getAsString() + "<br />");
        if (reply.getPlayer().has("prefix"))
            tmp.append("Rank Prefix: " + reply.getPlayer().get("prefix").getAsString() + "<br />");
        return tmp.toString();
    }
}
