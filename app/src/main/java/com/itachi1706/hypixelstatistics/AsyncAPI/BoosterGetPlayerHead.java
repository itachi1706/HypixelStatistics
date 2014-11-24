package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kenneth on 11/11/2014, 9:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class BoosterGetPlayerHead extends AsyncTask<BoosterDescription, Void, Drawable> {

    Context mContext;
    Exception except = null;
    BoosterDescription data;
    ListView list;
    boolean isActiveOnly;
    ProgressBar bar;
    ImageView image;

    /*
    public BoosterGetPlayerHead(Context context, ListView listView, boolean isActive, ProgressBar bars){
        mContext = context;
        list = listView;
        isActiveOnly = isActive;
        bar = bars;
    }*/

    public BoosterGetPlayerHead(Context context, ImageView view, ProgressBar progress){
        mContext = context;
        image = view;
        bar = progress;
    }

    @Override
    protected Drawable doInBackground(BoosterDescription... playerData) {
        data = playerData[0];
        int density = 500;
        Log.d("SCREEN DENSITY", mContext.getResources().getDisplayMetrics().density + "");
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
        String headUrl = "https://minotar.net/avatar/" + data.get_mcName() + "/" + density + ".png";
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
            //data.setMcHead(draw);
            image.setImageDrawable(draw);

            bar.setVisibility(View.GONE);
        }
    }
}
