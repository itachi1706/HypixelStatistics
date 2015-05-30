package com.itachi1706.hypixelstatistics.ServerPinging;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by Kenneth on 7/2/2015, 6:05 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.ServerPinging
 */
public class InitServerPing extends AsyncTask<Void, Void, InetAddress> {

    private Exception except = null;
    Context mContext;
    TextView playerCount;

    public InitServerPing(Context appContext, TextView playerCount){
        this.mContext = appContext;
        this.playerCount = playerCount;
    }

    @Override
    protected InetAddress doInBackground(Void... params) {
        InetAddress address;
        try {
            address = InetAddress.getByName("mc.hypixel.net");
        } catch (UnknownHostException e) {
            except = e;
            return null;
        }
        return address;
    }

    public void onPostExecute(InetAddress address){
        PingServerObject server = new PingServerObject();
        if (except != null){
            NotifyUserUtil.createShortToast(mContext, "An error occured parsing mc.hypixel.net");
            this.playerCount.setText("Unknown number of players online");
        }
        InetSocketAddress actualAddr = new InetSocketAddress(address, 25565);
        server.setAddress(actualAddr);
        new GetServerStatus(mContext, playerCount).execute(server);
    }
}
