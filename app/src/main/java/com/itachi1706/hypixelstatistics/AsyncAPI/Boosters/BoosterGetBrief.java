package com.itachi1706.hypixelstatistics.AsyncAPI.Boosters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.BriefBoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.Objects.BoosterDescription;

import net.hypixel.api.reply.BoostersReply;
import net.hypixel.api.util.GameType;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
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
        String url = MainStaticVars.API_BASE_URL + "boosters?key=" + MainStaticVars.apikey;
        String tmp = "";
        //Get Statistics
        try {
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, MainStaticVars.HTTP_QUERY_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, MainStaticVars.HTTP_QUERY_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
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

    @SuppressWarnings("SuspiciousMethodCalls")
    protected void onPostExecute(String json) {
        if (except != null){
            if (except instanceof ConnectTimeoutException)
                Toast.makeText(mContext, "Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
            else if (except instanceof SocketTimeoutException)
                Toast.makeText(mContext, "Socket Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.GONE);
        } else {
            Gson gson = new Gson();
            Log.d("JSON STRING", json);
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    Toast.makeText(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext.getApplicationContext(), "An Exception Occured (No JSON String Obtained). Refresh Boosters to try again", Toast.LENGTH_SHORT).show();
                bar.setVisibility(View.GONE);
            } else {
                BoostersReply reply = gson.fromJson(json, BoostersReply.class);
                Log.d("BOOSTER", reply.toString());
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    Toast.makeText(mContext.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
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
                        bar.setVisibility(View.GONE);
                        tooltip.setVisibility(View.INVISIBLE);
                        MainStaticVars.inProg = false;
                        MainStaticVars.isBriefBooster = true;
                    } else {
                        String[] tmp = {"No Boosters Activated"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, tmp);
                        list.setAdapter(adapter);
                        bar.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
