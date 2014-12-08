package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.CharHistory;
import com.itachi1706.hypixelstatistics.util.HistoryObject;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.BoostersReply;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kenneth on 18/11/2014, 9:24 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class BoosterGet extends AsyncTask<Void, Void, String> {

    Context mContext;
    Exception except = null;
    ListView list;
    boolean isActiveOnly;
    ProgressBar bar;

    public BoosterGet(Context context, ListView listView, boolean isActive, ProgressBar bars){
        mContext = context;
        list = listView;
        isActiveOnly = isActive;
        bar = bars;
    }

    @Override
    protected String doInBackground(Void... params) {
        String url = MainStaticVars.API_BASE_URL + "boosters?key=" + mContext.getResources().getString(R.string.hypixel_api_key);
        String tmp = "";
        //Get Statistics
        try {
            HttpClient client = new DefaultHttpClient();
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

    protected void onPostExecute(String json) {
        if (except != null){
            Toast.makeText(mContext.getApplicationContext(), "An Exception Occured (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.GONE);
        } else {
            Gson gson = new Gson();
            Log.d("JSON STRING", json);
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
                JsonArray records = reply.getRecords().getAsJsonArray();
                MainStaticVars.numOfBoosters = records.size();
                MainStaticVars.tmpBooster = 0;
                if (records.size() != 0) {
                    for (JsonElement e : records) {
                        JsonObject obj = e.getAsJsonObject();
                        BoosterDescription desc = new BoosterDescription(obj.get("amount").getAsInt(), obj.get("dateActivated").getAsLong(),
                                obj.get("gameType").getAsInt(), obj.get("length").getAsInt(), obj.get("originalLength").getAsInt(),
                                obj.get("purchaser").getAsString());
                        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mContext));
                        boolean hasHist = false;
                        if (hist != null) {
                            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
                            JsonArray histCheck = check.getHistory();
                            for (JsonElement el : histCheck) {
                                JsonObject histCheckName = el.getAsJsonObject();
                                if (histCheckName.get("playername").getAsString().equals(desc.get_purchaser())) {
                                    //Check if history expired
                                    if (CharHistory.checkHistoryExpired(histCheckName)){
                                        //Expired, reobtain
                                        histCheck.remove(histCheckName);
                                        CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mContext), histCheck);
                                        break;
                                    } else {
                                        desc.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                                        desc.set_mcName(histCheckName.get("displayname").getAsString());
                                        desc.set_done(true);
                                        hasHist = true;
                                        MainStaticVars.boosterList.add(desc);
                                        MainStaticVars.tmpBooster++;
                                        Log.d("Player", "Found player " + desc.get_purchaser());
                                        break;
                                    }
                                }
                            }
                        }
                        if (!hasHist)
                            new BoosterGetPlayerName(mContext, list, isActiveOnly, bar).execute(desc);
                        checkIfComplete();

                    }
                } else {
                    String[] tmp = {"No Boosters Activated"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, tmp);
                    list.setAdapter(adapter);
                    bar.setVisibility(View.GONE);
                }
            }
        }
    }

    private void checkIfComplete(){
        if (MainStaticVars.tmpBooster == MainStaticVars.numOfBoosters && !MainStaticVars.parseRes){
            MainStaticVars.parseRes = true;
            MainStaticVars.boosterUpdated = true;
            MainStaticVars.inProg = false;
            if (!isActiveOnly) {
                BoosterDescListAdapter adapter = new BoosterDescListAdapter(mContext, R.layout.listview_booster_desc, MainStaticVars.boosterList);
                list.setAdapter(adapter);
                bar.setVisibility(View.GONE);
            } else {
                ArrayList<BoosterDescription> tmp = new ArrayList<>();
                for (BoosterDescription desc : MainStaticVars.boosterList){
                    tmp.add(desc);
                }
                Iterator<BoosterDescription> iter = tmp.iterator();
                while (iter.hasNext()){
                    BoosterDescription desc = iter.next();
                    if (!desc.checkIfBoosterActive())
                        iter.remove();
                }
                BoosterDescListAdapter adapter = new BoosterDescListAdapter(mContext, R.layout.listview_booster_desc, tmp);
                list.setAdapter(adapter);
                bar.setVisibility(View.GONE);
                MainStaticVars.parseRes = false;
            }
        }
    }
}
