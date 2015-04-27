package com.itachi1706.hypixelstatistics.AsyncAPI.Guilds;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.FindGuildReply;

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
 * Created by Kenneth on 20/12/2014, 4:41 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetGuildId extends AsyncTask<String, Void, String> {

    Exception except = null;
    boolean _isName;
    Activity mContext;
    ListView _generalInfo, _memberInfo;

    public GetGuildId(boolean isName, Activity activity, ListView generalInfo, ListView memberInfo){
        _isName = isName;
        mContext = activity;
        _generalInfo = generalInfo;
        _memberInfo = memberInfo;
    }

    @Override
    protected String doInBackground(String... nameOrPlayer){
        String url = MainStaticVars.API_BASE_URL + "findGuild?key=" + MainStaticVars.apikey + "&";
        String gName = nameOrPlayer[0];
        gName = gName.replace(" ", "%20");
        if (_isName) {//Is Guild Name
            url += "byName=" + gName;
            Log.d("Getting Guild Info", "by Name");
        } else { //Guild Player
            url += "byPlayer=" + gName;
            Log.d("Getting Guild Info", "by Member");
        }
        Log.d("findGuild URL", url);
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


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            except = e;
        }

        return tmp;
    }

    protected void onPostExecute(String json){
        if (except != null){
            //Theres an exception
            if (except instanceof SocketTimeoutException)
                Toast.makeText(mContext, "Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "An error occured. (" + except.getLocalizedMessage() + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        if (!MainStaticVars.checkIfYouGotJsonString(json)){
            if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                Toast.makeText(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "An error occured. (Invalid JSON String) Please Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        FindGuildReply reply = gson.fromJson(json, FindGuildReply.class);
        if (reply.isThrottle()) {
            //Throttled (API Exceeded Limit)
            Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!reply.isSuccess()){
            //Not Successful
            Toast.makeText(mContext, "Unsuccessful Query!\n Reason: " + reply.getCause(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (reply.getGuild() == null) {
            if (_isName)
                Toast.makeText(mContext, "Unable to find a guild by that name. Please note that searching by Guild Name is case sensitive", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "This player does not have a guild", Toast.LENGTH_SHORT).show();
            return;
        }
        //If doesnt cover all, succeeded
        //Call the getting guild
        new GetGuildInfo(mContext, _generalInfo, _memberInfo).execute(reply.getGuild());
    }
}
