package com.itachi1706.hypixelstatistics.AsyncAPI.Boosters;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by Kenneth on 16/2/2015, 5:31 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class BoosterGetHistory extends AsyncTask<BoosterDescription, Void, Boolean> {
    BoosterDescription desc;
    Context mContext;
    //Exception except = null;
    ListView list;
    boolean isActiveOnly;
    ProgressBar bar;
    TextView tooltip;

    public BoosterGetHistory(Context context, ListView listView, boolean isActive, ProgressBar bars, TextView tooltips){
        mContext = context;
        list = listView;
        isActiveOnly = isActive;
        bar = bars;
        tooltip = tooltips;
    }

    @Override
    protected Boolean doInBackground(BoosterDescription... boosters) {
        Gson gson = new Gson();
        desc = boosters[0];
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mContext));
        //boolean hasHist = false;
        if (hist != null) {
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            JsonArray histCheck = check.getHistory();
            for (JsonElement el : histCheck) {
                JsonObject histCheckName = el.getAsJsonObject();
                if (histCheckName.get("uuid").getAsString().equals(desc.get_purchaseruuid())) {
                    //Check if history expired
                    if (CharHistory.checkHistoryExpired(histCheckName)){
                        //Expired, reobtain
                        histCheck.remove(histCheckName);
                        CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mContext), histCheck);
                        Log.d("HISTORY", "History Expired");
                        return false;
                    } else {
                        desc.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                        desc.set_mcName(histCheckName.get("displayname").getAsString());
                        desc.set_purchaseruuid(histCheckName.get("uuid").getAsString());
                        desc.set_done(true);
                        MainStaticVars.boosterList.add(desc);
                        MainStaticVars.tmpBooster++;
                        MainStaticVars.boosterProcessCounter++;
                        Log.d("Player", "Found player " + desc.get_mcName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean hasHist){
        if (!hasHist)
            new BoosterGetPlayerName(mContext, list, isActiveOnly, bar, tooltip).execute(desc);
        checkIfComplete();
    }

    private void checkIfComplete(){
        boolean done = true;
        for (BoosterDescription desc : MainStaticVars.boosterList){
            if (!desc.is_done()) {
                done = false;
                break;
            }
        }

        if (done){
            if (!isActiveOnly) {
                if (MainStaticVars.boosterList != null && MainStaticVars.boosterList.size() != 0) {
                    MainStaticVars.boosterListAdapter.updateAdapter(MainStaticVars.boosterList);
                    MainStaticVars.boosterListAdapter.notifyDataSetChanged();
                }
            }
        }

        if (MainStaticVars.boosterList.size() >= MainStaticVars.numOfBoosters && !MainStaticVars.parseRes){
            tooltip.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.INVISIBLE);
            MainStaticVars.inProg = false;
            MainStaticVars.parseRes = true;
            MainStaticVars.boosterUpdated = true;

            //Active Only
            if (isActiveOnly){
                ArrayList<BoosterDescription> tmp = new ArrayList<>();
                for (BoosterDescription desc : MainStaticVars.boosterList) {
                    tmp.add(desc);
                }
                Iterator<BoosterDescription> iter = tmp.iterator();
                while (iter.hasNext()) {
                    BoosterDescription desc = iter.next();
                    if (!desc.checkIfBoosterActive())
                        iter.remove();
                }
                BoosterDescListAdapter adapter = new BoosterDescListAdapter(mContext, R.layout.listview_booster_desc, tmp);
                list.setAdapter(adapter);
            } else {
                //Filter based on filter
                String filterString = MainStaticVars.boosterListAdapter.getFilteredStringForBooster();
                MainStaticVars.backupBooster();
                if (!filterString.equals(""))
                    MainStaticVars.boosterListAdapter.getFilter().filter(filterString);
            }
            MainStaticVars.parseRes = false;
        }

        if (MainStaticVars.inProg) {
            tooltip.setVisibility(View.VISIBLE);
            tooltip.setText("Processed Player " + MainStaticVars.boosterProcessCounter + "/" + MainStaticVars.boosterMaxProcessCounter);
        }
    }
}
