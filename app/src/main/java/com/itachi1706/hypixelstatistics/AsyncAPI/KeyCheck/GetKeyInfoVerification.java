package com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.KeyReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Kenneth on 10/11/2014, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetKeyInfoVerification extends AsyncTask<UUID,Void,String> {

    Activity mContext;
    Exception except = null;
    SharedPreferences sp;
    Preference key_info, key_string, key_staff, key_name, key_uuid;

    public GetKeyInfoVerification(Activity context, SharedPreferences sharedPrefs, Preference keyString, Preference keyInfoAct, Preference keyStaff, Preference keyName, Preference keyuuid){
        mContext = context;
        sp = sharedPrefs;
        key_string = keyString;
        key_info = keyInfoAct;
        key_staff = keyStaff;
        key_name = keyName;
        key_uuid = keyuuid;
    }

    @Override
    protected String doInBackground(UUID... key) {
        String url = MainStaticVars.API_BASE_URL + "key?key=" + key[0].toString();
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
            except = e;
        }
        return tmp;

    }

    protected void onPostExecute(String json) {
        GeneralPrefActivity.GeneralPreferenceFragment pref = new GeneralPrefActivity.GeneralPreferenceFragment();
        if (except != null){
            if (except instanceof SocketTimeoutException)
                NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "Connection Timed out. Try again later");
            else
                NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "An exception occurred (" + except.getMessage() + ")");
            pref.updateKeyString(sp, key_string, key_info, mContext.getApplicationContext());
        } else {
            Gson gson = new Gson();
            KeyReply reply = gson.fromJson(json, KeyReply.class);
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again");
                else
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "An error occured. (Invalid JSON String) Please Try Again later");
                return;
            }
            if (reply == null){
                if (!mContext.isFinishing()){
                    new AlertDialog.Builder(mContext).setTitle("An error occured")
                            .setMessage("No Key Reply found. Please contact dev")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "An error occured");
                return;
            }
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                if (!mContext.isFinishing()) {
                    new AlertDialog.Builder(mContext).setTitle("Verification Throttled")
                            .setMessage("API Limit has been reached and we could not verify this key. Please try again later")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else {
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "API Limit Reached. Unable to verify key, try again later");
                }
                pref.updateKeyString(sp, key_string, key_info, mContext.getApplicationContext());
            } else if (!reply.isSuccess()){

                //Not Successful
                //debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
                if (!mContext.isFinishing()) {
                    new AlertDialog.Builder(mContext).setTitle("Invalid Key")
                            .setMessage(reply.getCause()).setPositiveButton(android.R.string.ok, null).show();
                } else {
                    NotifyUserUtil.createShortToast(mContext.getApplicationContext(), "Invalid Key (" + reply.getCause() + ")");
                }
                pref.updateKeyString(sp, key_string, key_info, mContext.getApplicationContext());
            } else {
                //Succeeded
                //Set SharedPref to new key and update general prefs
                sp.edit().remove("playerName").apply();
                sp.edit().remove("rank").apply();
                sp.edit().remove("own").apply();
                sp.edit().putString("api-key",reply.getRecord().getKey().toString()).apply();
                pref.updateKeyString(sp, key_string, key_info, mContext.getApplicationContext());
                new GetKeyInfoVerificationName(mContext,sp,key_staff,key_name,key_uuid,true).execute(reply.getRecord().getOwner());
            }
        }
    }
}
