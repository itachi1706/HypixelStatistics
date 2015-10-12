package com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.Objects.FriendsObject;
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

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class RetriveFriendsName extends AsyncTask<FriendsObject, Void, String> {

    Exception except = null;
    Activity mActivity;
    FriendsObject playerName;
    int retry = 0;
    Handler mHandler;

    private static final int FRIENDS_NAME_GET = 1113;

    public RetriveFriendsName(Activity activity, Handler handler){
        this.mActivity = activity;
        this.mHandler = handler;
    }

    public RetriveFriendsName(Activity activity, Handler handler, int retry){
        this.mActivity = activity;
        this.retry = retry;
        this.mHandler = handler;
    }

    @Override
    protected String doInBackground(FriendsObject... playerData) {
        playerName = playerData[0];
        String url = MainStaticVars.API_BASE_URL + "?type=player&uuid=" + playerName.getFriendUUID();
        url = MainStaticVars.updateURLWithApiKeyIfExists(url);
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
            if (except instanceof SocketTimeoutException) {
                if (retry > 10)
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Connection Timed Out. Try again later");
                else {
                    Log.d("RESOLVE", "Retrying");
                    new RetriveFriendsName(mActivity, mHandler, retry + 1).execute(playerName);
                }
            } else
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")");
            return;
        }

        Gson gson = new Gson();
        if (!MainStaticVars.checkIfYouGotJsonString(json)) {
            Log.d("Invalid JSON", json + " is invalid");
            Log.d("RESOLVE", "Retrying");
            new RetriveFriendsName(mActivity, mHandler).execute(playerName);
            return;
        }

        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        if (reply.isThrottle()) {
            //Throttled (API Exceeded Limit)
            //NotifyUserUtil.createShortToast(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
            Log.d("THROTTLED", "FRIENDS API NAME GET: " + playerName.getFriendUUID());
            Log.d("RESOLVE", "Retrying");
            new RetriveFriendsName(mActivity, mHandler).execute(playerName);
        } else if (!reply.isSuccess()) {
            //Not Successful
            NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause());
            Log.d("UNSUCCESSFUL", "FRIENDS API NAME GET: " + playerName.getFriendUUID());
            Log.d("RESOLVE", "Retrying");
            new RetriveFriendsName(mActivity, mHandler).execute(playerName);
        } else if (reply.getPlayer() == null) {
            NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Invalid Player " + playerName.getFriendUUID());
            return;
        }

        //Process Friends Requests
        Message msg = Message.obtain();
        msg.what = FRIENDS_NAME_GET;
        Bundle bundle = new Bundle();
        bundle.putString("friendJson", json);
        msg.obj = playerName;
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }
}
