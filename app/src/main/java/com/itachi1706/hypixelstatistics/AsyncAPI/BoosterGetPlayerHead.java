package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.ResultDescription;

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

    public BoosterGetPlayerHead(Context context, ListView listView, boolean isActive, ProgressBar bars){
        mContext = context;
        list = listView;
        isActiveOnly = isActive;
        bar = bars;
    }

    @Override
    protected Drawable doInBackground(BoosterDescription... playerData) {
        data = playerData[0];
        String headUrl = "https://minotar.net/avatar/" + data.get_mcName() + "/500.png";
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
            return;
        } else {
            data.setMcHead(draw);
            MainStaticVars.boosterList.add(data);
            MainStaticVars.tmpBooster ++;
            if (MainStaticVars.tmpBooster == MainStaticVars.numOfBoosters){
                MainStaticVars.boosterUpdated = true;
                if (!isActiveOnly) {
                    BoosterDescListAdapter adapter = new BoosterDescListAdapter(mContext, R.layout.listview_booster_desc, MainStaticVars.boosterList);
                    list.setAdapter(adapter);
                } else {
                    ArrayList<BoosterDescription> tmp = new ArrayList<>();
                    for (BoosterDescription desc : MainStaticVars.boosterList){
                        tmp.add(desc);
                    }
                    Iterator<BoosterDescription> iter = tmp.iterator();
                    while (iter.hasNext()){
                        BoosterDescription desc = iter.next();
                        if (!desc.checkIfBoosterActive())
                            iter.remove();
                    }
                    BoosterDescListAdapter adapter = new BoosterDescListAdapter(mContext, R.layout.listview_booster_desc, tmp);
                    list.setAdapter(adapter);
                    bar.setVisibility(View.GONE);
                }
            }
        }
    }
}
