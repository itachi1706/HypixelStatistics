package com.itachi1706.hypixelstatistics.AsyncAPI.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.FriendsObject;
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
public class GetFriendsName extends AsyncTask<FriendsObject, Void, String> {

    Exception except = null;
    Activity mActivity;
    FriendsObject playerName;
    int retry = 0;

    //Progress Info
    private ProgressBar progressCircle;
    private TextView progressInfo;

    public GetFriendsName(Activity activity, ProgressBar progress, TextView progressInfo){
        mActivity = activity;
        this.progressInfo = progressInfo;
        this.progressCircle = progress;
    }

    public GetFriendsName(Activity activity, ProgressBar progress, TextView progressInfo, int retry){
        mActivity = activity;
        this.retry = retry;
        this.progressInfo = progressInfo;
        this.progressCircle = progress;
    }

    @Override
    protected String doInBackground(FriendsObject... playerData) {
        playerName = playerData[0];
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey + "&uuid=" + playerName.getFriendUUID();
        String tmp = "";
        Log.i("FRIENDS-NAME", "Getting Friend Name for " + playerData[0].getFriendUUID());
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
                    new GetFriendsName(mActivity, progressCircle, progressInfo, retry + 1).execute(playerName);
                }
            } else
                Toast.makeText(mActivity.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                Log.d("Invalid JSON", json + " is invalid");
                Log.d("RESOLVE", "Retrying");
                new GetFriendsName(mActivity, progressCircle, progressInfo).execute(playerName);
            } else {
                PlayerReply reply = gson.fromJson(json, PlayerReply.class);
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    //Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d("THROTTLED", "FRIENDS API NAME GET: " + playerName.getFriendUUID());
                    Log.d("RESOLVE", "Retrying");
                    new GetFriendsName(mActivity, progressCircle, progressInfo).execute(playerName);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    Toast.makeText(mActivity.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
                    Log.d("UNSUCCESSFUL", "FRIENDS API NAME GET: " + playerName.getFriendUUID());
                    Log.d("RESOLVE", "Retrying");
                    new GetFriendsName(mActivity, progressCircle, progressInfo).execute(playerName);
                } else if (reply.getPlayer() == null) {
                    Toast.makeText(mActivity.getApplicationContext(), "Invalid Player " + playerName.getFriendUUID(), Toast.LENGTH_SHORT).show();
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
                        CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
                        Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
                    }
                    MainStaticVars.friendsList.add(playerName);
                    progressInfo.setText("Retrieved " + MainStaticVars.friendsList.size() + "/" + MainStaticVars.friendsListSize + " friends");
                    checkIfComplete();
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

    private void checkIfComplete(){
        boolean done = true;
        for (FriendsObject o : MainStaticVars.friendsList){
            if (!o.is_done()){
                done = false;
                break;
            }
        }

        if (done){
            //FriendsListAdapter adapter = new FriendsListAdapter(mActivity, R.layout.listview_guild_desc, MainStaticVars.friendsList);
            //playerFriendsList.setAdapter(adapter);
            MainStaticVars.friendsListAdapter.updateAdapter(MainStaticVars.friendsList);
            MainStaticVars.friendsListAdapter.notifyDataSetChanged();
        }

        if (MainStaticVars.friendsList.size() >= MainStaticVars.friendsListSize){
            //Complete. Hide progress data
            progressCircle.setVisibility(View.INVISIBLE);
            progressInfo.setVisibility(View.INVISIBLE);
        }
    }
}
