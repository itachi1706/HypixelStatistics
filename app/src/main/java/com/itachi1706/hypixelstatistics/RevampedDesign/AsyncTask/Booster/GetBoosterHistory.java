package com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Booster;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.BoosterRecyclerAdapter;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kenneth on 16/2/2015, 5:31 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetBoosterHistory extends AsyncTask<BoosterDescription, Void, Boolean> {
    BoosterDescription desc;
    Activity mActivity;
    //Exception except = null;
    RecyclerView list;
    boolean isActiveOnly;
    ProgressBar bar;
    TextView tooltip;
    Handler handler;

    public GetBoosterHistory(Activity activity, RecyclerView recyclerView, boolean isActive, ProgressBar bars, TextView tooltips, Handler handler){
        mActivity = activity;
        list = recyclerView;
        isActiveOnly = isActive;
        bar = bars;
        tooltip = tooltips;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(BoosterDescription... boosters) {
        Gson gson = new Gson();
        desc = boosters[0];
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity));
        //boolean hasHist = false;
        if (hist != null) {
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            for (HistoryArrayObject histCheckName : histCheck) {
                if (histCheckName.getUuid().equals(desc.get_purchaseruuid())) {
                    //Check if history expired
                    if (CharHistory.checkHistoryExpired(histCheckName)){
                        //Expired, reobtain
                        histCheck.remove(histCheckName);
                        CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mActivity), histCheck);
                        Log.d("HISTORY", "History Expired");
                        return false;
                    } else {
                        desc.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                        desc.set_mcName(histCheckName.getDisplayname());
                        desc.set_purchaseruuid(histCheckName.getUuid());
                        desc.set_done(true);
                        MainStaticVars.addBoosterObject(desc);
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
            new GetBoosterPlayerName(mActivity, list, isActiveOnly, bar, tooltip, handler).execute(desc);
        checkIfComplete();
    }

    private void checkIfComplete(){
        if (MainStaticVars.boosterHashMap.size() >= MainStaticVars.numOfBoosters && !MainStaticVars.parseRes){
            MainStaticVars.updateBoosterList();
            tooltip.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.INVISIBLE);
            MainStaticVars.inProg = false;
            MainStaticVars.parseRes = true;
            MainStaticVars.boosterUpdated = true;

            //Active Only
            if (isActiveOnly){
                ArrayList<BoosterDescription> tmp = new ArrayList<>();
                for (BoosterDescription desc : MainStaticVars.boosterHashMap.values()) {
                    tmp.add(desc);
                }
                Iterator<BoosterDescription> iter = tmp.iterator();
                while (iter.hasNext()) {
                    BoosterDescription desc = iter.next();
                    if (!desc.checkIfBoosterActive())
                        iter.remove();
                }
                BoosterRecyclerAdapter adapter = new BoosterRecyclerAdapter(tmp, mActivity, handler);
                list.setAdapter(adapter);
            } else {
                //Filter based on filter
                String filterString = MainStaticVars.boosterRecyclerAdapter.getFilteredStringForBooster();
                MainStaticVars.backupBooster();
                if (!filterString.equals(""))
                    MainStaticVars.boosterRecyclerAdapter.getFilter().filter(filterString);
            }
            MainStaticVars.parseRes = false;
        }

        if (MainStaticVars.inProg) {
            tooltip.setVisibility(View.VISIBLE);
            tooltip.setText("Processed Player " + MainStaticVars.boosterProcessCounter + "/" + MainStaticVars.boosterMaxProcessCounter);
        }
    }
}
