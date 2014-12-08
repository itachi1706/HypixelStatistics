package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.KeyReply;

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
public class GetKeyInfoVerification extends AsyncTask<UUID,Void,String> {

    Context mContext;
    Exception except = null;
    SharedPreferences sp;
    Preference key_info, key_string, key_staff, key_name;

    public GetKeyInfoVerification(Context context, SharedPreferences sharedPrefs, Preference keyString, Preference keyInfoAct, Preference keyStaff, Preference keyName){
        mContext = context;
        sp = sharedPrefs;
        key_string = keyString;
        key_info = keyInfoAct;
        key_staff = keyStaff;
        key_name = keyName;
    }

    @Override
    protected String doInBackground(UUID... key) {
        String url = MainStaticVars.API_BASE_URL + "key?key=" + key[0].toString();
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
        GeneralPrefActivity.GeneralPreferenceFragment pref = new GeneralPrefActivity.GeneralPreferenceFragment();
        if (except != null){
            new AlertDialog.Builder(mContext).setTitle("An Exception Occurred")
                    .setMessage(except.getMessage()).setPositiveButton(android.R.string.ok, null).show();
            pref.updateKeyString(sp, key_string, key_info, mContext);
        } else {
            Gson gson = new Gson();
            KeyReply reply = gson.fromJson(json, KeyReply.class);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                new AlertDialog.Builder(mContext).setTitle("Verification Throttled")
                        .setMessage("API Limit has been reached and we could not verify this key. Please try again later")
                        .setPositiveButton(android.R.string.ok, null).show();
                pref.updateKeyString(sp, key_string, key_info, mContext);
            } else if (!reply.isSuccess()){

                //Not Successful
                //debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
                new AlertDialog.Builder(mContext).setTitle("Invalid Key")
                        .setMessage(reply.getCause()).setPositiveButton(android.R.string.ok, null).show();
                pref.updateKeyString(sp, key_string, key_info, mContext);
            } else {
                //Succeeded
                //Set SharedPref to new key and update general prefs
                sp.edit().remove("playerName").apply();
                sp.edit().remove("rank").apply();
                sp.edit().remove("own").apply();
                sp.edit().putString("api-key",reply.getRecord().getKey().toString()).apply();
                pref.updateKeyString(sp, key_string, key_info, mContext);
                new GetKeyInfoVerificationName(mContext,sp,key_staff,key_name,true).execute(reply.getRecord().getOwner());
            }
        }
    }
}
