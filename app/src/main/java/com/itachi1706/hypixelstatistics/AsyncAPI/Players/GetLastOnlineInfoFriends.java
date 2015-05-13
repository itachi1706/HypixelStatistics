package com.itachi1706.hypixelstatistics.AsyncAPI.Players;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kenneth on 9/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.Session
 */
public class GetLastOnlineInfoFriends extends AsyncTask<String, Void, String> {

    Exception except = null;
    TextView result;
    String uuidValue;

    public GetLastOnlineInfoFriends(TextView playerLastOnline){
        this.result = playerLastOnline;
    }

    @Override
    protected String doInBackground(String... uuidQuery) {
        String url = MainStaticVars.API_BASE_URL + "player?key=" + MainStaticVars.apikey + "&uuid=" + uuidQuery[0];
        String tmp = "";
        uuidValue = uuidQuery[0];
        Log.i("LASTONLINE-FRIEND", "Getting last online Data for " + uuidQuery[0]);
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

    /**
     * Unknown Level Status (Refer to logcat for details)
     * 2 - Socket Timed Out (Read/Connect)
     * 3 - Exception Occurred
     * 4 - Cloudflare Timeout
     * 5 - Invalid JSON
     */

    protected void onPostExecute(String json){
        String resultString = "";
        Log.i("LASTONLINE-FRIEND", "Last Online Data received for " + uuidValue + ". Parsing...");
        if (except != null){
            if (except instanceof SocketTimeoutException) {
                resultString += "§4Unknown [2]§r";
                Log.e("LASTONLINE-ERR", "Connection Timed Out. Try again later");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else {
                resultString += "§4Unknown [3]§r";
                Log.e("LASTONLINE-ERR", except.getMessage());
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            }
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare")){
                    resultString += "§4Unknown [4]§r";
                    Log.e("LASTONLINE-ERR", "CloudFlare timeout 524 occurred");
                } else {
                    resultString += "§4Unknown [5]§r";
                    Log.e("LASTONLINE-ERR", "An error occured (Invalid JSON String)");
                }
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
                return;
            }
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            if (reply.isThrottle()){
                resultString += "§4Throttled§r";
                Log.e("LASTONLINE-ERR", "Unknown Status (Query limit reached)");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else if (!reply.isSuccess()){
                resultString += "§4Unknown UUID§r";
                Log.e("LASTONLINE-ERR", "Invalid UUID");
                result.setText(Html.fromHtml(MinecraftColorCodes.parseColors(resultString)));
            } else {
                if (reply.getPlayer().has("lastLogin")) {
                    long dateLong = reply.getPlayer().get("lastLogin").getAsLong(); //In millis
                    long currentDate = System.currentTimeMillis();
                    long difference = currentDate - dateLong;
                    String date;
                    if (difference < 3600000){
                        //Less than an hour, say it
                        long seconds = difference / 1000;
                        long minutes = seconds / 60;
                        date = "Last Online: " + minutes + " mins ago";
                    } else
                        date = "Last Online: " + new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(dateLong));
                    result.setText(date);
                } else {
                    result.setText("Last Online: §4Unknown§r");
                }
                MainStaticVars.friends_last_online_data.put(uuidValue, resultString);
            }
        }
    }
}
