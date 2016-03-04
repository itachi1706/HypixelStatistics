package com.itachi1706.hypixelstatistics.AsyncAPI.Boosters;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.RecyclerViewAdapters.BoosterRecyclerAdapter;
import com.itachi1706.hypixelstatistics.RecyclerViewAdapters.StringRecyclerAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.BoostersReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Kenneth on 18/11/2014, 9:24 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetBoosters extends AsyncTask<Void, Void, String> {

    Activity mActivity;
    Exception except = null;
    RecyclerView list;
    boolean isActiveOnly;
    ProgressBar bar;
    TextView tooltip;
    SwipeRefreshLayout swipeToRefresh = null;
    Handler handler;

    public GetBoosters(Activity activity, RecyclerView recyclerView, boolean isActive, ProgressBar bars, TextView tooltips, Handler handler){
        mActivity = activity;
        list = recyclerView;
        isActiveOnly = isActive;
        bar = bars;
        tooltip = tooltips;
        this.handler = handler;
    }

    public GetBoosters(Activity activity, RecyclerView recyclerView, boolean isActive, ProgressBar bars, TextView tooltips, SwipeRefreshLayout swipeRefresh, Handler handler){
        mActivity = activity;
        list = recyclerView;
        isActiveOnly = isActive;
        bar = bars;
        tooltip = tooltips;
        swipeToRefresh = swipeRefresh;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(Void... params) {
        String url = MainStaticVars.API_BASE_URL + "?type=boosters";
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
        if (swipeToRefresh != null) {
            if (swipeToRefresh.isRefreshing())
                swipeToRefresh.setRefreshing(false);
        }
        if (except != null){
            if (except instanceof SocketTimeoutException)
                NotifyUserUtil.createShortToast(mActivity, "Connection Timed Out. Try again later");
            else
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")");
            bar.setVisibility(View.INVISIBLE);
        } else {
            Gson gson = new Gson();
            Log.d("JSON STRING", json);
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again");
                else
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "An Exception Occured (No JSON String Obtained). Refresh Boosters to try again");
                bar.setVisibility(View.INVISIBLE);
            } else {
                BoostersReply reply = gson.fromJson(json, BoostersReply.class);
                Log.d("BOOSTER", reply.toString());
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    NotifyUserUtil.createShortToast(mActivity, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
                    bar.setVisibility(View.INVISIBLE);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause());
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    //Succeeded
                    MainStaticVars.boosterList.clear();
                    MainStaticVars.boosterHashMap.clear();
                    MainStaticVars.boosterUpdated = false;
                    MainStaticVars.inProg = true;
                    JsonArray records = reply.getRecords().getAsJsonArray();
                    MainStaticVars.numOfBoosters = records.size();
                    MainStaticVars.tmpBooster = 0;
                    MainStaticVars.boosterProcessCounter = 0;
                    MainStaticVars.boosterMaxProcessCounter = 0;
                    MainStaticVars.boosterJsonString = json;

                    if (!isActiveOnly) {
                        MainStaticVars.boosterRecyclerAdapter = new BoosterRecyclerAdapter(MainStaticVars.boosterList, mActivity, handler);
                        list.setAdapter(MainStaticVars.boosterRecyclerAdapter);
                    }

                    if (records.size() != 0) {
                        MainStaticVars.boosterMaxProcessCounter = records.size();
                        for (JsonElement e : records) {
                            JsonObject obj = e.getAsJsonObject();
                            String uid = obj.get("purchaserUuid").getAsString(); //Get Player UUID
                            BoosterDescription desc;
                            if (obj.has("purchaser")) {
                                //Old Method (Default original is 3600 = 1hr)
                                desc = new BoosterDescription(obj.get("amount").getAsInt(), obj.get("dateActivated").getAsLong(),
                                        obj.get("gameType").getAsInt(), obj.get("length").getAsInt(), 3600,
                                        uid, obj.get("purchaser").getAsString());
                            } else {
                                //New Method
                                desc = new BoosterDescription(obj.get("amount").getAsInt(), obj.get("dateActivated").getAsLong(),
                                        obj.get("gameType").getAsInt(), obj.get("length").getAsInt(), obj.get("originalLength").getAsInt(),
                                        uid);
                            }
                            //Move to BoosterGetHistory
                            tooltip.setVisibility(View.VISIBLE);
                            tooltip.setText("Booster list obtained. Processing Players now...");
                            new GetBoosterHistory(mActivity, list, isActiveOnly, bar, tooltip, handler).execute(desc);
                        }
                    } else {
                        String[] tmp = {"No Boosters Activated"};
                        StringRecyclerAdapter adapter = new StringRecyclerAdapter(tmp);
                        list.setAdapter(adapter);
                        bar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }
}
