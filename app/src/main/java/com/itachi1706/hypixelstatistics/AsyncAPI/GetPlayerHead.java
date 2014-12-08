package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.util.HeadHistory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Kenneth on 11/11/2014, 9:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetPlayerHead extends AsyncTask<String, Void, Drawable> {

    ProgressBar progress;
    ImageView imageViewhead;
    Context mContext;
    Exception except = null;
    String playerNamer;

    public GetPlayerHead(ProgressBar prog, ImageView head, Context context){
        progress = prog;
        imageViewhead = head;
        mContext = context;
    }

    @Override
    protected Drawable doInBackground(String... playerName) {
        int density = 500;
        float i = mContext.getResources().getDisplayMetrics().density;
        if (i == 0.75f) {   //LDPI
            density = 20;
        } else if (i == 1.0f) { //MDPI
            density = 50;
        } else if (i == 1.5f) { //HDPI
            density = 100;
        } else if (i == 2.0f) { //XHDPI   (720p)
            density = 150;
        } else if (i == 3.0f) { //XXHDPI (1080p)
            density = 500;
        } else if (i == 4.0f) { //XXXHDPI (Unsupported)
            density = 500;
        }
        playerNamer = playerName[0];
        //String headUrl = "https://minotar.net/avatar/" + playerNamer + "/" + density + ".png";
        String headUrl = "http://cravatar.eu/avatar/" + playerNamer + "/" + density + ".png";

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
        progress.setVisibility(View.GONE);
        if (except != null){
            if (except.getCause() == null){
                Toast.makeText(mContext, "An Exception Occurred (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
                return;
            }
            if (except.getCause().toString().contains("SSLProtocolException"))
                Toast.makeText(mContext, "Head Download Timed Out. Please try again later.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "An Exception Occurred (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
        } else {
            imageViewhead.setImageDrawable(draw);

            //Check if image is already on device
            if (HeadHistory.checkIfHeadExists(mContext, playerNamer)){
                //Update image (Delete and Reinsert)
                if (!HeadHistory.updateHead(mContext, playerNamer)){
                    Toast.makeText(mContext, "An error occured updating of heads. Skipping...", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            HeadHistory.saveHead(mContext, draw, playerNamer);
        }
    }
}
