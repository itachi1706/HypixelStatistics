package com.itachi1706.hypixelstatistics.AsyncAPI.PlayerInfo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by Kenneth on 11/11/2014, 9:19 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class PlayerInfoQuerySkin extends AsyncTask<String, Void, Drawable> {

    Activity mContext;
    Exception except = null;
    String playerNamer;
    boolean retry = false;
    ImageView skin;
    ProgressBar bar;

    public PlayerInfoQuerySkin(Activity context, ImageView skin, ProgressBar mSkinLoader){
        mContext = context;
        this.skin = skin;
        this.bar = mSkinLoader;
    }

    public PlayerInfoQuerySkin(Activity context, boolean retrying, ImageView skin, ProgressBar mSkinLoader){
        mContext = context;
        retry = retrying;
        this.skin = skin;
        this.bar = mSkinLoader;
    }

    @Override
    protected Drawable doInBackground(String... playerName) {
        playerNamer = playerName[0];
        String headUrl;
        headUrl = "https://mcapi.ca/skin/3d/" + playerNamer + "/150";

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
            if (except instanceof UnknownHostException) {
                if (!retry) {
                    NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ") Retrying from different site");
                    new PlayerInfoQuerySkin(mContext, true, skin, bar).execute(playerNamer);
                    return;
                }
                bar.setVisibility(View.INVISIBLE);
                NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ")");
                return;
            }
            if (except.getCause() == null){

                if (!retry){
                    NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ") Retrying from different site");
                    new PlayerInfoQuerySkin(mContext, true, skin, bar).execute(playerNamer);
                    return;
                }
                else
                    bar.setVisibility(View.INVISIBLE);
                    NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ")");
                return;
            }
            if (except.getCause().toString().contains("SSLProtocolException")) {
                if (!retry) {
                    NotifyUserUtil.createShortToast(mContext, "Head Download Timed Out. Retrying from different site");
                    new PlayerInfoQuerySkin(mContext, true, skin, bar).execute(playerNamer);
                    return;
                }
                bar.setVisibility(View.INVISIBLE);
                NotifyUserUtil.createShortToast(mContext, "Head Download Timed Out. Please try again later.");
            } else
                bar.setVisibility(View.INVISIBLE);
                NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ")");
        } else {
            bar.setVisibility(View.INVISIBLE);
            skin.setImageDrawable(draw);
            skin.setVisibility(View.VISIBLE);
        }
    }
}
