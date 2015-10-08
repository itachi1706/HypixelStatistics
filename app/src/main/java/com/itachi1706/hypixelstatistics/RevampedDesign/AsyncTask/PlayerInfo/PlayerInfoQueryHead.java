package com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerInfoActivity;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;
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
public class PlayerInfoQueryHead extends AsyncTask<String, Void, Drawable> {

    Activity mContext;
    Exception except = null;
    String playerNamer;
    boolean retry = false;
    android.support.v7.app.ActionBar actionBar;

    public PlayerInfoQueryHead(Activity context, android.support.v7.app.ActionBar actBar){
        mContext = context;
        actionBar = actBar;
    }

    public PlayerInfoQueryHead(Activity context, boolean retrying, android.support.v7.app.ActionBar actBar){
        mContext = context;
        retry = retrying;
        actionBar = actBar;
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
            density = 300;
            //density = 500;
        } else if (i == 4.0f) { //XXXHDPI (Unsupported)
            density = 300;
            //density = 500;
        }
        playerNamer = playerName[0];
        String headUrl;
        if (retry)
            headUrl = "https://minotar.net/avatar/" + playerNamer + "/" + density + ".png";
        else
            headUrl = "http://cravatar.eu/avatar/" + playerNamer + "/" + density + ".png";

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
                    new PlayerInfoQueryHead(mContext, true, actionBar).execute(playerNamer);
                }
                NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ")");
                return;
            }
            if (except.getCause() == null){

                if (!retry){
                    NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ") Retrying from different site");
                    new PlayerInfoQueryHead(mContext, true, actionBar).execute(playerNamer);
                }
                else
                    NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ")");
                return;
            }
            if (except.getCause().toString().contains("SSLProtocolException")) {
                if (!retry) {
                    NotifyUserUtil.createShortToast(mContext, "Head Download Timed Out. Retrying from different site");
                    new PlayerInfoQueryHead(mContext, true, actionBar).execute(playerNamer);
                }
                NotifyUserUtil.createShortToast(mContext, "Head Download Timed Out. Please try again later.");
            } else
                NotifyUserUtil.createShortToast(mContext, "An Exception Occurred (" + except.getMessage() + ")");
        } else {
            actionBar.setLogo(draw);

            //Palette API generate and update activity primary and primary dark colors
            Bitmap toUseForPalette = ((BitmapDrawable) draw).getBitmap();
            Palette.Builder build = new Palette.Builder(toUseForPalette);
            build.maximumColorCount(32);
            build.generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int primaryColor = palette.getVibrantColor(ContextCompat.getColor(mContext, R.color.blue_500));
                    int primaryDarkColor = palette.getDarkVibrantColor(ContextCompat.getColor(mContext, R.color.blue_700));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //Lollipop and after so yay status bar color changes :D
                        //Animate Status Bar
                        Integer colorFromStatus = ContextCompat.getColor(mContext, R.color.blue_700), colorToStatus = primaryDarkColor;
                        ValueAnimator statusBarAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromStatus, colorToStatus);
                        statusBarAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override @TargetApi(21)
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mContext.getWindow().setStatusBarColor((Integer) animation.getAnimatedValue());
                                mContext.getWindow().setNavigationBarColor((Integer) animation.getAnimatedValue());
                            }
                        });
                        statusBarAnimation.start();
                    }
                    //This supports all version of android from SDK 11 so dont have the legendary color changes :(
                    //Animate action bar
                    Integer colorFromAction = ContextCompat.getColor(mContext, R.color.blue_500), colorToAction = primaryColor;
                    ValueAnimator actionBarAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromAction, colorToAction);
                    actionBarAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            actionBar.setBackgroundDrawable(new ColorDrawable((Integer) animation.getAnimatedValue()));
                            if (mContext instanceof PlayerInfoActivity){
                                PlayerInfoActivity act = (PlayerInfoActivity) mContext;
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                                    //noinspection deprecation
                                    act.findViewById(R.id.activity_tablayout).setBackgroundDrawable(new ColorDrawable((Integer) animation.getAnimatedValue()));
                                } else {
                                    act.findViewById(R.id.activity_tablayout).setBackground(new ColorDrawable((Integer) animation.getAnimatedValue()));
                                }
                            }
                        }
                    });
                    actionBarAnimation.start();
                }
            });

            //Check if image is already on device
            if (HeadHistory.checkIfHeadExists(mContext, playerNamer)){
                //Update image (Delete and Reinsert)
                if (!HeadHistory.updateHead(mContext, playerNamer)){
                    NotifyUserUtil.createShortToast(mContext, "An error occured updating of heads. Skipping...");
                    return;
                }
            }
            HeadHistory.saveHead(mContext, draw, playerNamer);
        }
    }
}
