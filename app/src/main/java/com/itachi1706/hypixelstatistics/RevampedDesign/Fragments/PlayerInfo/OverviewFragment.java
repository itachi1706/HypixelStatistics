package com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.PlayerInfo;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
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
    private TextView session, level;
    private ImageView skin;
    private DonutProgress levelBar;
    private ProgressBar mSkinLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        session = (TextView) v.findViewById(R.id.player_tvSessionInfo);
        level = (TextView) v.findViewById(R.id.lbl_level);
        skin = (ImageView) v.findViewById(R.id.playerSkin);
        levelBar = (DonutProgress) v.findViewById(R.id.pbLevel);
        mSkinLoader = (ProgressBar) v.findViewById(R.id.pbSkinLoader);

        mSkinLoader.setVisibility(View.INVISIBLE);
        levelBar.setVisibility(View.INVISIBLE);
        level.setVisibility(View.INVISIBLE);
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

        int networkXp = reply.getPlayer().has("networkExp") ? reply.getPlayer().get("networkExp").getAsInt() : 0;
        int level = reply.getPlayer().has("networkLevel") ? reply.getPlayer().get("networkLevel").getAsInt() : 0;

        //Do the level calculation [Total XP is (((level - 1) * 2500) + 10000)]
        if (level == 0) level++;
        int totalXp = ((level - 1) * 2500) + 10000;
        float progressValue = (float) networkXp / totalXp;
        Log.d("Overview", "Progress: " + progressValue + ", Network: " + networkXp + ", Total: " + totalXp);

        levelBar.setVisibility(View.VISIBLE);
        this.level.setVisibility(View.VISIBLE);
        levelBar.setMax(totalXp);
        levelBar.setProgress(networkXp);
        levelBar.setText(level + "");

        skin.setImageDrawable(null);
        session.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§fQuerying session info...§r")));
        new PlayerInfoQuerySession(session).execute(uuidSession);
        mSkinLoader.setVisibility(View.VISIBLE);
        new PlayerInfoQuerySkin(getActivity(), skin, mSkinLoader).execute(localPlayerName);
    }
}
