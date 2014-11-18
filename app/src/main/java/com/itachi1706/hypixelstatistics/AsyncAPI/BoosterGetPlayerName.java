package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

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
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class BoosterGetPlayerName extends AsyncTask<BoosterDescription, Void, String> {

    Exception except = null;
    Context mContext;
    BoosterDescription playerName;
    ListView list;
    boolean isActive;

    public BoosterGetPlayerName(Context context, ListView listView, boolean isActiveOnly){
        mContext = context;
        list = listView;
        isActive = isActiveOnly;
    }

    @Override
    protected String doInBackground(BoosterDescription... playerData) {
        playerName = playerData[0];
        String url = MainStaticVars.API_BASE_URL + "player?key=" + mContext.getResources().getString(R.string.hypixel_api_key) + "&name=" + playerName.get_purchaser();
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
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
            } else if (!reply.isSuccess()){
                //Not Successful
                Toast.makeText(mContext.getApplicationContext(), "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
            } else if (reply.getPlayer() == null) {
                Toast.makeText(mContext.getApplicationContext(), "Invalid Player", Toast.LENGTH_SHORT).show();
            } else {
                //Succeeded
                playerName.set_mcName(reply.getPlayer().get("displayname").getAsString());
                playerName.set_done(true);
                new BoosterGetPlayerHead(mContext, list, isActive).execute(playerName);
            }
        }
    }
}
