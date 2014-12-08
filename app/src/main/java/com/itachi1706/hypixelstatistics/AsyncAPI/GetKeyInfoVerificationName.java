package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.AlertDialog;
import android.content.Context;
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

import net.hypixel.api.reply.KeyReply;
import net.hypixel.api.reply.PlayerReply;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetKeyInfoVerificationName extends AsyncTask<String,Void,String> {

    Context mContext;
    Exception except = null;
    SharedPreferences sp;
    Preference key_info, key_string;

    public GetKeyInfoVerificationName(Context context, SharedPreferences sharedPrefs, Preference keyString, Preference keyInfoAct){
        mContext = context;
        sp = sharedPrefs;
        key_string = keyString;
        key_info = keyInfoAct;
    }

    @Override
    protected String doInBackground(String... owner) {
        String url = MainStaticVars.API_BASE_URL + "player?key=" + mContext.getResources().getString(R.string.hypixel_api_key) + "&name=" + owner[0];
        String tmp = "";
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;

    }

    protected void onPostExecute(String json) {
        if (except != null){
            new AlertDialog.Builder(mContext).setTitle("An Exception Occurred")
                    .setMessage(except.getMessage()).setPositiveButton(android.R.string.ok, null).show();
        } else {
            Gson gson = new Gson();
            PlayerReply reply = gson.fromJson(json, PlayerReply.class);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                new AlertDialog.Builder(mContext).setTitle("Verification Throttled")
                        .setMessage("API Limit has been reached and we could not check your player rank, however key has been set.\n" +
                                "If you are a staff member, to get access to additional info, please relaunch the application")
                        .setPositiveButton(android.R.string.ok, null).show();
            } else if (!reply.isSuccess()){
                //Not Successful
                new AlertDialog.Builder(mContext).setTitle("An Exception Occurred")
                        .setMessage(reply.getCause()).setPositiveButton(android.R.string.ok, null).show();
            } else {
                //Succeeded
                //TODO Alert Dialog of success
                String pRank = MinecraftColorCodes.parseHypixelRanks(reply);
                new AlertDialog.Builder(mContext).setTitle("Success!")
                        .setMessage(Html.fromHtml("New API Key Set! Welcome " + pRank + "!"))
                        .setPositiveButton(android.R.string.ok, null).show();

            }
        }
    }
}
