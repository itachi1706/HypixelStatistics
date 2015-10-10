package com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
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
import java.util.List;

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class RetriveFriendOwner extends AsyncTask<String, Void, String> {

    Exception except = null;
    Activity mActivity;
    String uuid;
    int retry = 0, friendListSize = 0;
    private Handler handler;

    private static final int FRIEND_OWNER_RETRIVED = 1111;

    public RetriveFriendOwner(Activity activity, int friendListSize, Handler handler){
        mActivity = activity;
        this.friendListSize = friendListSize;
        this.handler = handler;
    }

    public RetriveFriendOwner(Activity activity, int friendListSize, Handler handler, int retry){
        mActivity = activity;
        this.retry = retry;
        this.friendListSize = friendListSize;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... ownerUUID) {
        uuid = ownerUUID[0];
        String url = MainStaticVars.API_BASE_URL + "?type=player&uuid=" + uuid;
        url = MainStaticVars.updateURLWithApiKeyIfExists(url);
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
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Connection Timed Out. Try again later");
                else {
                    Log.d("RESOLVE", "Retrying");
                    new RetriveFriendOwner(mActivity, friendListSize, handler, retry + 1).execute(uuid);
                }
            } else
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")");
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                Log.d("Invalid JSON", json + " is invalid");
                Log.d("RESOLVE", "Retrying");
                new RetriveFriendOwner(mActivity, friendListSize, handler).execute(uuid);
            } else {
                PlayerReply reply = gson.fromJson(json, PlayerReply.class);
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    //NotifyUserUtil.createShortToast(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
                    Log.d("THROTTLED", "FRIENDS API OWNER GET: " + uuid);
                    Log.d("RESOLVE", "Retrying");
                    new RetriveFriendOwner(mActivity, friendListSize, handler).execute(uuid);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause());
                    Log.d("UNSUCCESSFUL", "FRIENDS API OWNER GET: " + uuid);
                    Log.d("RESOLVE", "Retrying");
                    new RetriveFriendOwner(mActivity, friendListSize, handler).execute(uuid);
                } else if (reply.getPlayer() == null) {
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Invalid Player " + uuid);
                } else {
                    //Succeeded
                    String playername;
                    if (!MinecraftColorCodes.checkDisplayName(reply)) {
                        playername = reply.getPlayer().get("playername").getAsString();
                    } else {
                        playername = MinecraftColorCodes.parseHypixelRanks(reply);
                    }
                    if (!checkHistory(reply)) {
                        CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
                        Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
                    }
                    MainStaticVars.friendOwner = playername;

                    //TODO: Throw handler back to handle message
                    Message msg = Message.obtain();
                    msg.what = FRIEND_OWNER_RETRIVED;
                    Bundle bundle = new Bundle();
                    bundle.putString("friendOwner", playername);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private boolean checkHistory(PlayerReply reply){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            for (HistoryArrayObject histCheckName : histCheck) {
                if (histCheckName.getPlayername().equals(reply.getPlayer().get("playername").getAsString())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
