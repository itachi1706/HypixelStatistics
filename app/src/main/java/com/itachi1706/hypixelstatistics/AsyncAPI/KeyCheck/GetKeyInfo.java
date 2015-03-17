package com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.R;
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
public class GetKeyInfo extends AsyncTask<UUID,Void,String> {

    TextView debug,key,owner,query,result;
    Context mContext;
    Exception except = null;

    public GetKeyInfo(TextView keyView, TextView ownerView, TextView queryView, TextView resultView, TextView debugView, Context context){
        debug = debugView;
        key = keyView;
        owner = ownerView;
        query = queryView;
        result = resultView;
        mContext = context;
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
        if (except != null){
            debug.setText(except.getMessage());
        } else {
            Gson gson = new Gson();
            if (!MainStaticVars.checkIfYouGotJsonString(json)){
                if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                    Toast.makeText(mContext.getApplicationContext(), "A CloudFlare timeout has occurred. Please wait a while before trying again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "An error occured. (Invalid JSON String) Please Try Again", Toast.LENGTH_SHORT).show();
                return;
            }
            KeyReply reply = gson.fromJson(json, KeyReply.class);
            debug.setText(reply.toString());
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                result.setText(reply.getCause());
                resetTextFields();
                Toast.makeText(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later", Toast.LENGTH_SHORT).show();
                result.setTextColor(Color.RED);
            } else if (!reply.isSuccess()){
                //Not Successful
                result.setText(reply.getCause());
                result.setTextColor(Color.RED);
                resetTextFields();
                debug.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
            } else {
                //Succeeded
                result.setText("Success!");
                result.setTextColor(Color.GREEN);
                owner.setText(reply.getRecord().getOwner());
                query.setText(reply.getRecord().getQueriesInPastMin() + "");
                key.setText(reply.getRecord().getKey().toString());
            }
        }
    }

    private void resetTextFields(){
        owner.setText(mContext.getResources().getString(R.string.keyinfo_fields));
        query.setText(mContext.getResources().getString(R.string.keyinfo_fields));
        key.setText(mContext.getResources().getString(R.string.keyinfo_fields));

    }
}
