package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQuerySession;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

/**
 * A placeholder fragment containing a simple view.
 */
public class GeneralInfoFragment extends BaseFragmentCompat {

    public GeneralInfoFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_player_info_general;
    }

    //Fragment Elements
    private TextView session;

    private String noStats = "To start, press the Search icon!";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        session = (TextView) v.findViewById(R.id.player_tvSessionInfo);
        session.setText(noStats);

        return v;
    }

    @Override
    public void processPlayerJson(String json){
        Log.i("HypixelStatistics", "Switched to GeneralInfoFragment");
        if (json == null || json.equals("")) {
            session.setText(noStats);
            return;
        }
        Gson gson = new Gson();
        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        process(reply);
    }

    @Override
    public void processPlayerObject(PlayerReply object){
        process(object);
    }

    // PROCESS RESULT METHODS (GRABBLED FROM ASYNC TASK)

    private void process(PlayerReply reply){
        //Get Session Info
        String uuidSession = reply.getPlayer().get("uuid").getAsString();
        session.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§fQuerying session info...§r")));
        new PlayerInfoQuerySession(session).execute(uuidSession);
    }
}
