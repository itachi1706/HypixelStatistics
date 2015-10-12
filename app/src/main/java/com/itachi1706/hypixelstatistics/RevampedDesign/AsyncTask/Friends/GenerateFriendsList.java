package com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.StringRecyclerAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.FriendsReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Kenneth on 11/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.Friends
 */
public class GenerateFriendsList extends AsyncTask<String, Void, String> {

    private Exception except;
    private Activity mActivity;
    private RecyclerView playerRecyclerView;
    private Handler handler;

    private static final int FRIEND_LIST_GET = 1112;

    public GenerateFriendsList(Activity mActivity, RecyclerView playerRecyclerView, Handler handler){
        this.mActivity = mActivity;
        this.playerRecyclerView = playerRecyclerView;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... uuid) {
        String url = MainStaticVars.API_BASE_URL + "?type=friends&uuid=" + uuid[0];
        url = MainStaticVars.updateURLWithApiKeyIfExists(url);
        String tmp = "";
        Log.i("FRIENDS-UUID", "Getting Friends List Data for " + uuid[0]);
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
            while ((line = reader.readLine()) != null) {
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
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Connection Timed Out. Try again later");
            } else {
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), except.getMessage());
            }
            return;
        }
        Gson gson = new Gson();
        if (!MainStaticVars.checkIfYouGotJsonString(json)) {
            if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "CloudFlare timeout 524 occurred");
            else
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "An error occurred (Invalid JSON String)");
            return;
        }
        FriendsReply reply = gson.fromJson(json, FriendsReply.class);
        if (reply.isThrottle()) {
            NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Query Throttled (Limit reached)");
            return;
        } else if (!reply.isSuccess()) {
            NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Invalid UUID");
            return;
        }
        if (reply.getRecords().size() == 0) {
            String[] noFriendsSadFace = {"No Friends Found :("};
            StringRecyclerAdapter noFriendsAdapter = new StringRecyclerAdapter(noFriendsSadFace);

            playerRecyclerView.setAdapter(noFriendsAdapter);
            return;
        }

        //Process Friends Requests
        Message msg = Message.obtain();
        msg.what = FRIEND_LIST_GET;
        Bundle bundle = new Bundle();
        bundle.putString("friendJson", json);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
