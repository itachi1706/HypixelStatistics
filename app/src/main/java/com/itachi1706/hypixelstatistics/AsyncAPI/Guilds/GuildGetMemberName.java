package com.itachi1706.hypixelstatistics.AsyncAPI.Guilds;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.GuildMemberAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.GuildMemberDesc;
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
public class GuildGetMemberName extends AsyncTask<GuildMemberDesc, Void, String> {

    Exception except = null;
    Activity mContext;
    GuildMemberDesc playerName;
    ListView _memberInfo;
    boolean retry = false;

    public GuildGetMemberName(Activity activity, ListView memberInfo){
        mContext = activity;
        _memberInfo = memberInfo;
    }

    public GuildGetMemberName(Activity activity, ListView memberInfo, boolean retry){
        this.mContext = activity;
        this._memberInfo = memberInfo;
        this.retry = retry;
    }

    @Override
    protected String doInBackground(GuildMemberDesc... playerData) {
        playerName = playerData[0];
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey + "&uuid=" + playerName.get_uuid();
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
            if (except instanceof SocketTimeoutException) {
                if (retry)
                    Toast.makeText(mContext.getApplicationContext(), "Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
                else
                    new GuildGetMemberName(mContext, _memberInfo, true).execute(playerName);
            } else {
                Toast.makeText(mContext.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
            }
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)) {
                Log.d("Invalid JSON", json + " is invalid");
                Log.d("RESOLVE", "Retrying");
                new GuildGetMemberName(mContext, _memberInfo).execute(playerName);
            } else {
                PlayerReply reply = gson.fromJson(json, PlayerReply.class);
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    //Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d("THROTTLED", "BOOSTER API NAME GET: " + playerName.get_uuid());
                    Log.d("RESOLVE", "Retrying");
                    new GuildGetMemberName(mContext, _memberInfo).execute(playerName);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    Toast.makeText(mContext.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
                    Log.d("UNSUCCESSFUL", "BOOSTER API NAME GET: " + playerName.get_uuid());
                    Log.d("RESOLVE", "Retrying");
                    new GuildGetMemberName(mContext, _memberInfo).execute(playerName);
                } else if (reply.getPlayer() == null) {
                    Toast.makeText(mContext.getApplicationContext(), "Invalid Player " + playerName.get_uuid(), Toast.LENGTH_SHORT).show();
                } else {
                    //Succeeded
                    if (!MinecraftColorCodes.checkDisplayName(reply)) {
                        playerName.set_mcName(reply.getPlayer().get("playername").getAsString());
                        playerName.set_mcNameWithRank(reply.getPlayer().get("playername").getAsString());
                    } else {
                        playerName.set_mcName(reply.getPlayer().get("displayname").getAsString());
                        playerName.set_mcNameWithRank(MinecraftColorCodes.parseHypixelRanks(reply));
                    }
                    playerName.set_done(true);
                    if (!checkHistory(reply)) {
                        CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(mContext));
                        Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
                    }
                    MainStaticVars.guildList.add(playerName);
                    checkIfComplete();
                    //new BoosterGetPlayerHead(mContext, list, isActive, bar).execute(playerName);
                }
            }
        }
    }

    private boolean checkHistory(PlayerReply reply){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mContext));
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

    private void checkIfComplete(){
        boolean done = true;
        for (GuildMemberDesc desc : MainStaticVars.guildList){
            if (!desc.is_done()) {
                done = false;
                break;
            }
        }
        if (done){
            GuildMemberAdapter adapter = new GuildMemberAdapter(mContext, R.layout.listview_guild_desc, MainStaticVars.guildList);
            _memberInfo.setAdapter(adapter);
        }
    }
}
