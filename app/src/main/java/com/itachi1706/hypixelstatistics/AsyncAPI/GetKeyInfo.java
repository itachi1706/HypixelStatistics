package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.gson.Gson;

import net.hypixel.api.HypixelAPI;
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

    TextView resultView;
    Context mContext;
    Exception except = null;

    public GetKeyInfo(TextView keyInfoView, Context context){
        resultView = keyInfoView;
        mContext = context;
    }

    @Override
    protected String doInBackground(UUID... key) {
        String url = HypixelAPI.API_BASE_URL + "key?key=" + key[0].toString();
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
            resultView.setText(except.getMessage());
        } else {
            Gson gson = new Gson();
            KeyReply reply = gson.fromJson(json, KeyReply.class);
            if (reply.isThrottle()) {
                //Throttled (API Exceeded Limit)
                resultView.setText(reply.getCause() +
                        "\n\nDue to the limitation of the public Hypixel API, requests are throttled at 60 queries per minute." +
                "\nHence please wait a while before trying again!");
            } else if (!reply.isSuccess()){
                //Not Successful
                resultView.setText("Unsuccessful Query!\n Reason: " + reply.getCause());
            } else {
                //Succeeded
                resultView.setText("Owner: " + reply.getRecord().getOwner() + "\nQueries last 60 seconds: " +
                        reply.getRecord().getQueriesInPastMin() + "\nKey: " + reply.getRecord().getKey() + "\nThrottled: " +
                        reply.isThrottle() + "\nSuccess: " + reply.isSuccess() + "\nCause: " + reply.getCause());
            }
        }
    }
}
