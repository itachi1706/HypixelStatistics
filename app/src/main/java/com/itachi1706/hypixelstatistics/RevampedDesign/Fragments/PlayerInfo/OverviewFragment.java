package com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.PlayerInfo;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQuerySession;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQuerySkin;
import com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.BaseFragmentCompat;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

/**
 * A placeholder fragment containing a simple view.
 */
public class OverviewFragment extends BaseFragmentCompat {

    public OverviewFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_player_info_general;
    }

    //Fragment Elements
    private TextView session;
    private ImageView skin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        session = (TextView) v.findViewById(R.id.player_tvSessionInfo);
        skin = (ImageView) v.findViewById(R.id.playerSkin);
        skin.setVisibility(View.INVISIBLE);
        processPlayerJson(null);

        return v;
    }

    @Override
    public void processPlayerJson(String json){
        Log.i("HypixelStatistics", "Switched to OverviewFragment");
        if (json == null || json.equals("")) {
            String noStats = "To start, press the Search icon!";
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
        //Get Local Player Name
        String localPlayerName;
        if (MinecraftColorCodes.checkDisplayName(reply))
            localPlayerName = reply.getPlayer().get("displayname").getAsString();
        else
            localPlayerName = reply.getPlayer().get("playername").getAsString();

        session.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§fQuerying session info...§r")));
        new PlayerInfoQuerySession(session).execute(uuidSession);
        new PlayerInfoQuerySkin(getActivity(), skin).execute(localPlayerName);
    }
}
