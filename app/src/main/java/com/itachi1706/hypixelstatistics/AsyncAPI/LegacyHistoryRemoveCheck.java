package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Kenneth on 21/9/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class LegacyHistoryRemoveCheck extends AsyncTask<Void, Void, Void> {

    private Context context;

    public LegacyHistoryRemoveCheck(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Clear expired history
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(context));
        if (hist != null){
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            for (Iterator<HistoryArrayObject> iterator = histCheck.iterator(); iterator.hasNext();){
                HistoryArrayObject historyArrayObject = iterator.next();
                if (CharHistory.checkHistoryExpired(historyArrayObject)){
                    //Expired, remove
                    iterator.remove();
                    CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(context), histCheck);
                }
            }

            HeadHistory.removeExpiredHeads(context, histCheck);
        } else
            HeadHistory.removeExpiredHeads(context, null);
        return null;
    }
}
