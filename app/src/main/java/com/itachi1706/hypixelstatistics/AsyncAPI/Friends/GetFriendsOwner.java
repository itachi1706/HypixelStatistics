package com.itachi1706.hypixelstatistics.AsyncAPI.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.HistoryObject;

import net.hypixel.api.reply.PlayerReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetFriendsOwner extends AsyncTask<String, Void, String> {

    Exception except = null;
    Activity mActivity;
    String uuid;
    int retry = 0, friendListSize = 0;
    TextView desc;

    public GetFriendsOwner(Activity activity, TextView description, int friendListSize){
        mActivity = activity;
        desc = description;
        this.friendListSize = friendListSize;

    }

    public GetFriendsOwner(Activity activity, TextView description, int friendListSize, int retry){
        mActivity = activity;
        desc = description;
        this.retry = retry;
        this.friendListSize = friendListSize;
    }

    @Override
    protected String doInBackground(String... ownerUUID) {
        uuid = ownerUUID[0];
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey + "&uuid=" + uuid;
        String tmp = "";
        Log.i("FRIENDS-OWNER", "Getting Owner Name for " + uuid);
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
        if (except != null) {
            if (except instanceof SocketTimeoutException){
                if (retry > 10)
                    Toast.makeText(mActivity.getApplicationContext(), "Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
                else {
                    Log.d("RESOLVE", "Retrying");
                    new GetFriendsOwner(mActivity, desc, friendListSize, retry + 1).execute(uuid);
                }
            } else
                Toast.makeText(mActivity.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                Log.d("Invalid JSON", json + " is invalid");
                Log.d("RESOLVE", "Retrying");
                new GetFriendsOwner(mActivity, desc, friendListSize).execute(uuid);
            } else {
                PlayerReply reply = gson.fromJson(json, PlayerReply.class);
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    //Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d("THROTTLED", "FRIENDS API OWNER GET: " + uuid);
                    Log.d("RESOLVE", "Retrying");
                    new GetFriendsOwner(mActivity, desc, friendListSize).execute(uuid);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    Toast.makeText(mActivity.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
                    Log.d("UNSUCCESSFUL", "FRIENDS API OWNER GET: " + uuid);
                    Log.d("RESOLVE", "Retrying");
                    new GetFriendsOwner(mActivity, desc, friendListSize).execute(uuid);
                } else if (reply.getPlayer() == null) {
                    Toast.makeText(mActivity.getApplicationContext(), "Invalid Player " + uuid, Toast.LENGTH_SHORT).show();
                } else {
                    //Succeeded
                    String playername;
                    if (!MinecraftColorCodes.checkDisplayName(reply)) {
                        playername = reply.getPlayer().get("playername").getAsString();
                    } else {
                        playername = MinecraftColorCodes.parseHypixelRanks(reply);
                    }
                    desc.setText(Html.fromHtml(MinecraftColorCodes.parseColors(playername + "'s friends" +
                            "<br />Friends: §b" + friendListSize + "§r")));
                    if (!checkHistory(reply)) {
                        CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
                        Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
                    }
                    MainStaticVars.friendOwner = playername;
                }
            }
        }
    }

    private boolean checkHistory(PlayerReply reply){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            JsonArray histCheck = check.getHistory();
            for (JsonElement el : histCheck) {
                JsonObject histCheckName = el.getAsJsonObject();
                if (histCheckName.get("playername").getAsString().equals(reply.getPlayer().get("playername").getAsString())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
