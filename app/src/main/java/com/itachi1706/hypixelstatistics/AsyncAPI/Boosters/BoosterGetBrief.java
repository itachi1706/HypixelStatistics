package com.itachi1706.hypixelstatistics.AsyncAPI.Boosters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.ListViewAdapters.BriefBoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;

import net.hypixel.api.reply.BoostersReply;
import net.hypixel.api.util.GameType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kenneth on 18/11/2014, 9:24 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
@SuppressWarnings("ConstantConditions")
public class BoosterGetBrief extends AsyncTask<Void, Void, String> {

    Context mContext;
    Exception except = null;
    ListView list;
    ProgressBar bar;
    TextView tooltip;

    public BoosterGetBrief(Context context, ListView listView, ProgressBar bars, TextView tooltips){
        mContext = context;
        list = listView;
        bar = bars;
        tooltip = tooltips;
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

    @SuppressWarnings("SuspiciousMethodCalls")
    protected void onPostExecute(String json) {
        if (except != null){
            if (except instanceof SocketTimeoutException)
                NotifyUserUtil.createShortToast(mContext, "Socket Connection Timed Out. Try again later");
            else
                NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")");
            bar.setVisibility(View.INVISIBLE);
        } else {
            Gson gson = new Gson();
            Log.d("JSON STRING", json);
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again");
                else
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "An Exception Occured (No JSON String Obtained). Refresh Boosters to try again");
                bar.setVisibility(View.INVISIBLE);
            } else {
                BoostersReply reply = gson.fromJson(json, BoostersReply.class);
                Log.d("BOOSTER", reply.toString());
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    NotifyUserUtil.createShortToast(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
                    bar.setVisibility(View.INVISIBLE);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause());
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    //Succeeded
                    MainStaticVars.boosterList.clear();
                    MainStaticVars.boosterUpdated = false;
                    MainStaticVars.inProg = true;
                    MainStaticVars.isBriefBooster = true;
                    JsonArray records = reply.getRecords().getAsJsonArray();
                    MainStaticVars.boosterJsonString = json;

                    HashMap<String, Integer> countPerGame = new HashMap<>();
                    HashMap<String, Integer> totalTimePerGame = new HashMap<>();
                    ArrayList<BoosterDescription> descArray = new ArrayList<>();

                    if (records.size() != 0) {
                        //Move to BoosterGetHistory
                        tooltip.setVisibility(View.VISIBLE);
                        tooltip.setText("Booster list obtained. Processing...");
                        for (JsonElement e : records) {
                            JsonObject obj = e.getAsJsonObject();
                            GameType gameType = GameType.fromId(obj.get("gameType").getAsInt());

                            //Adding Count
                            if (gameType == null){
                                //Unknown game
                                int tmpVal = 0;
                                if (countPerGame.containsKey("unknown")) //Value already present so lets take it out
                                    tmpVal = countPerGame.get("unknown");
                                tmpVal++;
                                countPerGame.put("unknown", tmpVal);
                            } else {
                                int tmpVal = 0;
                                if (countPerGame.containsKey(gameType.getId() + "")) //Value already present so lets take it out
                                    tmpVal = countPerGame.get(gameType.getId() + "");
                                tmpVal++;
                                countPerGame.put(gameType.getId() + "", tmpVal);
                            }

                            //Adding Time
                            int timeToAdd = obj.get("length").getAsInt();
                            if (gameType == null){
                                //Unknown game
                                int tmpVal = 0;
                                if (totalTimePerGame.containsKey("unknown")) //Value already present so lets take it out
                                    tmpVal = totalTimePerGame.get("unknown");
                                tmpVal+=timeToAdd;
                                totalTimePerGame.put("unknown", tmpVal);
                            } else {
                                int tmpVal = 0;
                                if (totalTimePerGame.containsKey(gameType.getId() + "")) //Value already present so lets take it out
                                    tmpVal = totalTimePerGame.get(gameType.getId() + "");
                                tmpVal+=timeToAdd;
                                totalTimePerGame.put(gameType.getId() + "", tmpVal);
                            }
                        }

                        for (Object o : countPerGame.entrySet()) {
                            Map.Entry pair = (Map.Entry) o;
                            BoosterDescription desc;
                            int totalTime = totalTimePerGame.get(pair.getKey());
                            if (pair.getKey().toString().equals("unknown")){
                                desc = new BoosterDescription("Unknown Game Mode", totalTime, (int) pair.getValue());
                            } else {
                                desc = new BoosterDescription(GameType.fromId(Integer.parseInt(pair.getKey().toString())).getName(), totalTime, (int) pair.getValue());
                            }
                            desc.set_done(true);
                            descArray.add(desc);
                        }
                        BriefBoosterDescListAdapter adapter = new BriefBoosterDescListAdapter(mContext, R.layout.listview_booster_desc, descArray);
                        list.setAdapter(adapter);
                        bar.setVisibility(View.INVISIBLE);
                        tooltip.setVisibility(View.INVISIBLE);
                        MainStaticVars.inProg = false;
                        MainStaticVars.isBriefBooster = true;
                    } else {
                        String[] tmp = {"No Boosters Activated"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, tmp);
                        list.setAdapter(adapter);
                        bar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }
}
