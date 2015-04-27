package com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.text.Html;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

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
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetKeyInfoVerificationName extends AsyncTask<String,Void,String> {

    Activity mContext;
    Exception except = null;
    SharedPreferences sp;
    Preference key_staff, key_name, key_uuid;
    boolean isSettings = false;

    public GetKeyInfoVerificationName(Activity context, SharedPreferences sharedPrefs, Preference keyStaff, Preference keyName, Preference keyuuid, boolean isSettingss){
        mContext = context;
        sp = sharedPrefs;
        key_staff = keyStaff;
        key_name = keyName;
        key_uuid = keyuuid;
        isSettings = isSettingss;
    }

    @Override
    protected String doInBackground(String... owner) {
        String url = MainStaticVars.API_BASE_URL + "player?key=" + mContext.getResources().getString(R.string.hypixel_api_key) + "&name=" + owner[0];
        String tmp = "";
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;

    }

    protected void onPostExecute(String json) {
        if (except != null){
            if (except instanceof SocketTimeoutException)
                Toast.makeText(mContext.getApplicationContext(), "Connection Timed out. Try again later", Toast.LENGTH_SHORT).show();
            else {
                if (!mContext.isFinishing()) {
                    new AlertDialog.Builder(mContext).setTitle("An Exception Occurred")
                            .setMessage(except.getMessage()).setPositiveButton(android.R.string.ok, null).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "An Exception Occurred (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    Toast.makeText(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext.getApplicationContext(), "An error occured. (Invalid JSON String) Please Try Again", Toast.LENGTH_SHORT).show();
                return;
            }
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            if (reply == null){
                Toast.makeText(mContext.getApplicationContext(), "Unable to verify key. Are you connected to the internet?", Toast.LENGTH_SHORT).show();
                return;
            }
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                if (!mContext.isFinishing()) {
                    new AlertDialog.Builder(mContext).setTitle("Verification Throttled")
                            .setMessage("API Limit has been reached and we could not check your player rank, however key has been set.\n" +
                                    "If you are a staff member, to get access to additional info, please relaunch the application")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "API Limit Reached!", Toast.LENGTH_SHORT).show();
                }
            } else if (!reply.isSuccess()){
                //Not Successful
                if (!mContext.isFinishing()) {
                    new AlertDialog.Builder(mContext).setTitle("An Exception Occurred")
                            .setMessage(reply.getCause()).setPositiveButton(android.R.string.ok, null).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "An exception occurred (" + reply.getCause() + ")", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Succeeded
                String pRank = MinecraftColorCodes.parseHypixelRanks(reply);
                String successMessage = "New API Key Set! Welcome " + pRank + "!";
                sp.edit().putString("playerName", pRank).apply();
                sp.edit().putString("own", reply.getPlayer().get("displayname").getAsString()).apply();
                sp.edit().putString("own-uuid", reply.getPlayer().get("uuid").getAsString()).apply();
                if (MinecraftColorCodes.isStaff(reply)) {
                    successMessage += " <br /><br />As a Staff Member (" + reply.getPlayer().get("rank").getAsString() + "), you now have access to additional information!";
                    sp.edit().putString("rank", reply.getPlayer().get("rank").getAsString()).apply();
                }
                if (mContext.getResources().getString(R.string.hypixel_api_key).equals(sp.getString("api-key", "lel"))){
                    successMessage += " <br /><br />As the creator of the application, you get access to additional information too!";
                }
                if (isSettings) {
                    if (!mContext.isFinishing()) {
                        new AlertDialog.Builder(mContext).setTitle("Success!")
                                .setMessage(Html.fromHtml(successMessage))
                                .setPositiveButton(android.R.string.ok, null).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    }
                    GeneralPrefActivity.GeneralPreferenceFragment pref = new GeneralPrefActivity.GeneralPreferenceFragment();
                    pref.updateApiKeyOwnerInfo(sp, key_staff, key_name, key_uuid);
                }
            }
        }
    }
}
