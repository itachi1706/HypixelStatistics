package com.itachi1706.hypixelstatistics.AsyncAPI.Session;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.SessionReply;

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

/**
 * Created by Kenneth on 9/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.Session
 */
public class GetSessionInfoPlayerStats extends AsyncTask<String, Void, String> {

    Exception except = null;
    TextView result;

    public GetSessionInfoPlayerStats(TextView playerSession){
        this.result = playerSession;
    }

    @Override
    protected String doInBackground(String... uuidQuery) {
        String url = MainStaticVars.API_BASE_URL + "session?key=" + MainStaticVars.apikey + "&uuid=" + uuidQuery[0];
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

    protected void onPostExecute(String json){
        if (except != null){
            if (except instanceof ConnectTimeoutException){
                result.setText("Connection Timed Out. Try again later");
                result.setTextColor(Color.RED);
            } else if (except instanceof SocketTimeoutException) {
                result.setText("Socket Connection Timed Out. Try again later");
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
                    String serverName = reply.getSession().get("server").getAsString();
                    int playerCount = reply.getSession().get("players").getAsJsonArray().size();
                    result.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§aIn-Game§r (§6" + serverName +
                            "§r) <br />Player Count: §b" + playerCount + "§r")));
                }
            }
        }
    }
}
