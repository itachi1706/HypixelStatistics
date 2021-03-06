package com.itachi1706.hypixelstatistics.AsyncAPI.PlayerInfo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.SessionReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Kenneth on 9/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.Session
 */
public class PlayerInfoQuerySession extends AsyncTask<String, Void, String> {

    Exception except = null;
    TextView result;

    public PlayerInfoQuerySession(TextView playerSession){
        this.result = playerSession;
    }

    @Override
    protected String doInBackground(String... uuidQuery) {
        String url = MainStaticVars.API_BASE_URL + "?type=session&uuid=" + uuidQuery[0];
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

    protected void onPostExecute(String json){
        if (except != null){
            if (except instanceof SocketTimeoutException) {
                result.setText("Connection Timed Out. Try again later");
                result.setTextColor(Color.RED);
            } else {
                result.setText(except.getMessage());
                result.setTextColor(Color.RED);
            }
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    result.setText("CloudFlare timeout 524 occurred");
                else
                    result.setText("An error occured (Invalid JSON String)");
                result.setTextColor(Color.RED);
                return;
            }
            SessionReply reply = gson.fromJson(json, SessionReply.class);
            if (reply.isThrottle()){
                result.setText("Unknown Status (Query limit reached)");
                result.setTextColor(Color.RED);
            } else if (!reply.isSuccess()){
                result.setText("Invalid UUID");
                result.setTextColor(Color.RED);
            } else {
                if (reply.getSession() == null){
                    //Not in game
                    result.setText("Not In-Game");
                    result.setTextColor(Color.RED);
                } else {
                    result.setTextColor(Color.WHITE);
                    String serverName = reply.getSession().get("server").getAsString();
                    int playerCount = reply.getSession().get("players").getAsJsonArray().size();
                    result.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§aIn-Game§r (§6" + serverName +
                            "§r) <br />Player Count: §b" + playerCount + "§r")));
                }
            }
        }
    }
}
