package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.BoostersReply;
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
                for (JsonElement e : records){
                    JsonObject obj = e.getAsJsonObject();
                    BoosterDescription desc = new BoosterDescription(obj.get("amount").getAsInt(), obj.get("dateActivated").getAsLong(),
                            obj.get("gameType").getAsInt(), obj.get("length").getAsInt(), obj.get("originalLength").getAsInt(),
                            obj.get("purchaser").getAsString());
                    new BoosterGetPlayerName(mContext, list, isActiveOnly, bar).execute(desc);
                }
            }
        }
    }
}
