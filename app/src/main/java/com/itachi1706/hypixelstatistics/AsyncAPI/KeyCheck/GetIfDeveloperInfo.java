package com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.util.MainStaticVars;

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
public class GetIfDeveloperInfo extends AsyncTask<String,Void,String> {

    private boolean keyVerify = false;
    private Context mContext;
    Exception except = null;

    public GetIfDeveloperInfo(){
        this.keyVerify = false;
    }

    public GetIfDeveloperInfo(boolean keyVerify, Context mContext){
        this.keyVerify = keyVerify;
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... key) {
        String url = MainStaticVars.API_BASE_URL + "?type=checkIfDeveloper&key=" + key[0];
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

    protected void onPostExecute(String response) {
        if (except != null){
            if (except instanceof SocketTimeoutException)
                Log.e("CHECK-DEV", "Connection Timed Out. Try again later");
            else
                Log.e("CHECK-DEV", except.getMessage());
            return;
        }

        Log.d("CHECK-DEV", "Response: " + response);
        Log.d("CHECK-DEV", "Key Verify: " + keyVerify);

        MainStaticVars.isCreator = response.equals("true");

        if (keyVerify){
            Toast.makeText(mContext, "As the creator of the application, you get access to additional information too!", Toast.LENGTH_SHORT).show();
        }

    }
}
