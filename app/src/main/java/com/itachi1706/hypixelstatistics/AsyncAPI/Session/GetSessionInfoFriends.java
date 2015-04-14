package com.itachi1706.hypixelstatistics.AsyncAPI.Session;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

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
public class GetSessionInfoFriends extends AsyncTask<String, Void, String> {

    Exception except = null;
    TextView result;
    String uuidValue;

    public GetSessionInfoFriends(TextView playerSession){
        this.result = playerSession;
    }

    @Override
    protected String doInBackground(String... uuidQuery) {
        String url = MainStaticVars.API_BASE_URL + "session?key=" + MainStaticVars.apikey + "&uuid=" + uuidQuery[0];
        String tmp = "";
        uuidValue = uuidQuery[0];
        Log.i("SESSION-GUILD", "Getting Session Data for " + uuidQuery[0]);
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

    /**
     * Unknown Level Status (Refer to logcat for details)
     * 1 - Connection Timed Out
     * 2 - Socket Timed Out
     * 3 - Exception Occurred
     * 4 - Cloudflare Timeout
     * 5 - Invalid JSON
     */

    protected void onPostExecute(String json){
        String resultString = "";
        if (except != null){
            if (except instanceof ConnectTimeoutException){
                resultString += "§4Unknown [1]§r";
                Log.e("SESSION-ERR", "Connection Timed Out. Try again later");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else if (except instanceof SocketTimeoutException) {
                resultString += "§4Unknown [2]§r";
                Log.e("SESSION-ERR", "Socket Connection Timed Out. Try again later");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else {
                resultString += "§4Unknown [3]§r";
                Log.e("SESSION-ERR", except.getMessage());
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            }
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare")){
                    resultString += "§4Unknown [4]§r";
                    Log.e("SESSION-ERR", "CloudFlare timeout 524 occurred");
                } else {
                    resultString += "§4Unknown [5]§r";
                    Log.e("SESSION-ERR", "An error occured (Invalid JSON String)");
                }
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
                return;
            }
            SessionReply reply = gson.fromJson(json, SessionReply.class);
            if (reply.isThrottle()){
                resultString += "§4Throttled§r";
                Log.e("SESSION-ERR", "Unknown Status (Query limit reached)");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else if (!reply.isSuccess()){
                resultString += "§4Unknown UUID§r";
                Log.e("SESSION-ERR", "Invalid UUID");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else {
                if (reply.getSession() == null){
                    //Not in game
                    resultString += "§cNot In-Game§r";
                    result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
                } else {
                    String serverName = reply.getSession().get("server").getAsString();
                    int playerCount = reply.getSession().get("players").getAsJsonArray().size();
                    resultString += "§aIn-Game §r[§b" + serverName + " §r-§d " + playerCount + "§r players§r]";
                    result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
                }
                MainStaticVars.friends_session_data.put(uuidValue, resultString);
            }
        }
    }
}
