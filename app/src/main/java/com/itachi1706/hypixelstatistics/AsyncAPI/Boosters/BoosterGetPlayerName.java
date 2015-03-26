package com.itachi1706.hypixelstatistics.AsyncAPI.Boosters;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

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
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class BoosterGetPlayerName extends AsyncTask<BoosterDescription, Void, String> {

    Exception except = null;
    Context mContext;
    BoosterDescription playerName;
    ListView list;
    boolean isActive;
    ProgressBar bar;
    TextView tooltip;

    public BoosterGetPlayerName(Context context, ListView listView, boolean isActiveOnly, ProgressBar bars, TextView tooltips){
        mContext = context;
        list = listView;
        isActive = isActiveOnly;
        bar = bars;
        tooltip = tooltips;
    }

    @Override
    protected String doInBackground(BoosterDescription... playerData) {
        playerName = playerData[0];
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey + "&uuid=" + playerName.get_purchaseruuid();
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
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                Log.d("Invalid JSON", json + " is invalid");
                Log.d("RESOLVE", "Retrying");
                new BoosterGetPlayerName(mContext, list, isActive, bar, tooltip).execute(playerName);
            } else {
                PlayerReply reply = gson.fromJson(json, PlayerReply.class);
                if (reply.isThrottle()) {
                    //Throttled (API Exceeded Limit)
                    //Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d("THROTTLED", "BOOSTER API NAME GET: " + playerName.get_purchaseruuid());
                    Log.d("RESOLVE", "Retrying");
                    new BoosterGetPlayerName(mContext, list, isActive, bar, tooltip).execute(playerName);
                } else if (!reply.isSuccess()) {
                    //Not Successful
                    Toast.makeText(mContext.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
                    Log.d("UNSUCCESSFUL", "BOOSTER API NAME GET: " + playerName.get_purchaseruuid());
                    Log.d("RESOLVE", "Retrying");
                    new BoosterGetPlayerName(mContext, list, isActive, bar, tooltip).execute(playerName);
                } else if (reply.getPlayer() == null) {
                    Toast.makeText(mContext.getApplicationContext(), "Invalid Player " + playerName.get_purchaseruuid(), Toast.LENGTH_SHORT).show();
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
                    MainStaticVars.boosterList.add(playerName);
                    MainStaticVars.tmpBooster++;
                    MainStaticVars.boosterProcessCounter++;
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
        if (MainStaticVars.tmpBooster == MainStaticVars.numOfBoosters && !MainStaticVars.parseRes){
            tooltip.setVisibility(View.INVISIBLE);
            MainStaticVars.parseRes = true;
            MainStaticVars.boosterUpdated = true;
            MainStaticVars.inProg = false;
            if (!isActive) {
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
        if (MainStaticVars.inProg) {
            tooltip.setVisibility(View.VISIBLE);
            tooltip.setText("Processed Player " + MainStaticVars.boosterProcessCounter + "/" + MainStaticVars.boosterMaxProcessCounter);
        }
    }
}