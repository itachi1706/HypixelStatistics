package com.itachi1706.hypixelstatistics.AsyncAPI.PlayerInfo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.PlayerReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.PlayerInfo
 */
public class PlayerInfoQuery extends AsyncTask<String,Void,String> {

    Activity mContext;
    Exception except = null;
    boolean isUUID;

    private Handler mHandler;
    private String queriedString;

    private static final int SUCCESS_QUERY = 1000;
    private static final int FAIL_QUERY = 1001;

    public PlayerInfoQuery(Activity context, boolean uuidState, Handler mHandler){
        mContext = context;
        isUUID = uuidState;
        this.mHandler = mHandler;
    }


    @Override
    protected String doInBackground(String... playerName) {
        String url = MainStaticVars.API_BASE_URL + "?type=player";
        queriedString = playerName[0];
        if (!isUUID) {
            url += "&name=" + queriedString;
        } else {
            url += "&uuid=" + queriedString;
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
            //Update Status Bar
            sendHandleBack("errorMsg", except instanceof SocketTimeoutException ? "Connection Timed Out" : except.getMessage(), FAIL_QUERY);
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
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                NotifyUserUtil.createShortToast(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
                sendHandleBack("errorMsg", reply.getCause(), FAIL_QUERY);
                return;
            }
            if (!reply.isSuccess()){
                //Not Successful
                sendHandleBack("errorMsg", reply.getCause(), FAIL_QUERY);
                return;
            }
            if (reply.getPlayer() == null) {
                String errorMsg;
                if (isUUID){
                    //Could be a name, try again
                    new PlayerInfoQuery(mContext, false, mHandler).execute(queriedString);
                    return;
                } else {
                    errorMsg = "Invalid Player";
                    NotifyUserUtil.createShortToast(mContext, "Unable to find a player with this name");
                }

                sendHandleBack("errorMsg", errorMsg, FAIL_QUERY);
                return;
            }

            //Notify user back to start processing
            sendHandleBack("playerJson", json, SUCCESS_QUERY);

            //History handling
            if (!checkHistory(reply)) {
                CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(mContext));
                Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
            }
        }
    }

    private void sendHandleBack(String key, String message, int resultCode){
        Message msg = Message.obtain();
        msg.what = resultCode;
        Bundle bundle = new Bundle();
        bundle.putString(key, message);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private boolean checkHistory(PlayerReply reply){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mContext));
        Log.d("HISTORY STRING", hist == null ? "No History" : hist);
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            Log.d("HISTORY ORIGINAL", histCheck.toString());
            for (Iterator<HistoryArrayObject> iterator = histCheck.iterator(); iterator.hasNext();){
                HistoryArrayObject histCheckName = iterator.next();
                if (histCheckName.getPlayername().equals(reply.getPlayer().get("playername").getAsString())) {
                    //Remove and let it reupdate
                    iterator.remove();
                    Log.d("HISTORY AFTER REMOVAL", histCheck.toString());
                    CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mContext), histCheck);
                }
            }
        }
        return false;
    }
}
