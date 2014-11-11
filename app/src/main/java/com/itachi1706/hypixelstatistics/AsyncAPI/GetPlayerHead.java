package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Kenneth on 11/11/2014, 9:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetPlayerHead extends AsyncTask<String, Void, Drawable> {

    ProgressDialog progress;
    ImageView imageViewhead;
    Context mContext;
    Exception except = null;

    public GetPlayerHead(ProgressDialog prog, ImageView head, Context context){
        progress = prog;
        imageViewhead = head;
        mContext = context;
    }

    @Override
    protected Drawable doInBackground(String... playerName) {
        String headUrl = "https://minotar.net/avatar/" + playerName[0] + "/500.png";
        Drawable d = null;
        try {
            //Get Player Head
            URL url = new URL(headUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(5000);
            InputStream is = (InputStream) conn.getContent();
            d = Drawable.createFromStream(is, "src name");
        } catch (IOException e) {
            e.printStackTrace();
            except = e;
        }
        return d;
    }

    protected void onPostExecute(Drawable draw) {
        progress.dismiss();
        if (except != null){
            if (except.getCause().toString().contains("SSLProtocolException"))
                Toast.makeText(mContext, "Head Download Timed Out. Please try again later.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "An Exception Occurred (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
        } else {
            imageViewhead.setImageDrawable(draw);
        }
    }
}
