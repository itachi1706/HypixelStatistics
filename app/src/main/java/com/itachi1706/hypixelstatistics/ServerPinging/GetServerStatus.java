package com.itachi1706.hypixelstatistics.ServerPinging;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.io.IOException;

/**
 * Created by Kenneth on 7/2/2015, 5:35 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.ServerPinging
 */
public class GetServerStatus extends AsyncTask<PingServerObject, Void, PingServerObject.StatusResponse> {

    Context mContex;
    TextView resultView;
    private Exception e=null;

    public  GetServerStatus(Context contex, TextView result)
    {
        this.mContex=contex;
        this.resultView=result;
    }

    @Override
    protected PingServerObject.StatusResponse doInBackground(PingServerObject... server) {
        try {
            return server[0].fetchData();
        } catch (IOException e) {
            this.e=e;
            return null;
        }
    }

    public void onPostExecute(PingServerObject.StatusResponse response) {
        if (e==null){
            MainStaticVars.serverMOTD = "<font>" + PingTools.parseFormatting(response.getDescription()) + "</font> <br /><br /> MC Version: " + response.getVersion().getName();
            MainStaticVars.playerCount = response.getPlayers().getOnline();
            MainStaticVars.maxPlayerCount = response.getPlayers().getMax();
            resultView.setText(MainStaticVars.playerCount + "/" + MainStaticVars.maxPlayerCount + " players online");
        } else {
            NotifyUserUtil.createShortToast(mContex, "An error occured! (" + e.toString() + ")");
        }
    }
}
