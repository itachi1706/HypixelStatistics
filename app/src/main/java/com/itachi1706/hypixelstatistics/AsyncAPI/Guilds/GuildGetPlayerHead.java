package com.itachi1706.hypixelstatistics.AsyncAPI.Guilds;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.Objects.GuildMemberDesc;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Kenneth on 11/11/2014, 9:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GuildGetPlayerHead extends AsyncTask<GuildMemberDesc, Void, Drawable> {

    Context mContext;
    Exception except = null;
    GuildMemberDesc data;
    ProgressBar bar;
    ImageView image;
    boolean retry = false;

    /*
    public BoosterGetPlayerHead(Context context, ListView listView, boolean isActive, ProgressBar bars){
        mContext = context;
        list = listView;
        isActiveOnly = isActive;
        bar = bars;
    }*/

    public GuildGetPlayerHead(Context context, ImageView view, ProgressBar progress){
        mContext = context;
        image = view;
        bar = progress;
    }

    public GuildGetPlayerHead(Context context, ImageView view, ProgressBar progress, boolean retrying){
        mContext = context;
        image = view;
        bar = progress;
        retry = retrying;
    }

    @Override
    protected Drawable doInBackground(GuildMemberDesc... playerData) {
        data = playerData[0];
        int density = 500;
        //Log.d("SCREEN DENSITY", mContext.getResources().getDisplayMetrics().density + "");
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
            density = 300;
            //density = 500;
        } else if (i == 4.0f) { //XXXHDPI (Unsupported)
            density = 300;
            //density = 500;
        }
        String headUrl;
        if (retry)
            headUrl = "https://minotar.net/avatar/" + data.get_mcName() + "/" + density + ".png";
        else
            headUrl = "http://cravatar.eu/avatar/" + data.get_mcName() + "/" + density + ".png";
        Drawable d = null;
        try {
            //Get Player Head
            URL url = new URL(headUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            InputStream is = (InputStream) conn.getContent();
            d = Drawable.createFromStream(is, "src name");
        } catch (IOException e) {
            e.printStackTrace();
            except = e;
        }
        return d;
    }

    protected void onPostExecute(Drawable draw) {
        if (except != null){
            if (except.getCause() == null){
                //Toast.makeText(mContext, "An Exception Occurred (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
                Log.d("BOOSTER HEAD EXCEPTION", "An Exception Occurred (" + except.getMessage() + ")");
                if (!retry) {
                    Log.d("BOOSTER HEAD EXCEPTION", "Retrying 1 more time from a different site");
                    new GuildGetPlayerHead(mContext, image, bar, true).execute(data);
                } else {
                    bar.setVisibility(View.GONE);
                }
                return;
            }
            if (except.getCause().toString().contains("SSLProtocolException")) {
                //Toast.makeText(mContext, "Head Download Timed Out. Please try again later.", Toast.LENGTH_SHORT).show();
                if (!retry) {
                    Log.d("TIMED OUT", "BOOSTER HEAD. Retrying 1 more time from a different site...");
                    new GuildGetPlayerHead(mContext, image, bar, true).execute(data);
                } else {
                    Log.d("TIMED OUT", "BOOSTER HEAD. Unable to get head!");
                    bar.setVisibility(View.GONE);
                }
                return;
            } else
                //Toast.makeText(mContext, "An Exception Occurred (" + except.getMessage() + ")", Toast.LENGTH_SHORT).show();
                Log.d("EXCEPTION", "BOOSTER HEAD (" + except.getMessage() + ")");
                bar.setVisibility(View.GONE);
        } else {
            //data.setMcHead(draw);
            image.setImageDrawable(draw);

            //Save it into the device
            if (HeadHistory.checkIfHeadExists(mContext, data.get_mcName())){
                //Update image (Delete and Reinsert)
                if (!HeadHistory.updateHead(mContext, data.get_mcName())){
                    Log.d("ERROR", "An error occurred updating " + data.get_mcName() + "'s head. Skipping...");
                }
            } else {
                if (!HeadHistory.saveHead(mContext, draw, data.get_mcName())) {
                    Log.d("ERROR", "An error occurred saving " + data.get_mcName() + "'s head onto device!");
                }
            }
            bar.setVisibility(View.GONE);
        }
    }
}
