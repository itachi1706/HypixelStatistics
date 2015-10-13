package com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.PlayerInfo;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.BaseFragmentCompat;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoBase;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoHeader;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.DonatorStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GeneralStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StaffOrYtStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.PlayerInfoExpandableRecyclerAdapter;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.StringRecyclerAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GeneralStatisticsFragment extends BaseFragmentCompat {

    public GeneralStatisticsFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_player_info_recycler;
    }

    //Fragment Elements
    private RecyclerView recyclerView;


    static String[] noStatistics = {"To start, press the Search icon!"};
    private StringRecyclerAdapter noStatAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.player_info_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        noStatAdapter = new StringRecyclerAdapter(noStatistics);

        processPlayerJson(null);
        return v;
    }

    @Override
    public void processPlayerJson(String json){
        Log.i("HypixelStatistics", "Switched to GeneralStatisticsFragment");
        if (json == null || json.equals("")) { recyclerView.setAdapter(noStatAdapter); return; }
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
        recyclerView.setVisibility(View.VISIBLE);

        //Get Local Player Name
        String localPlayerName;
        if (MinecraftColorCodes.checkDisplayName(reply))
            localPlayerName = reply.getPlayer().get("displayname").getAsString();
        else
            localPlayerName = reply.getPlayer().get("playername").getAsString();

        parse(reply, localPlayerName);
    }

    private void parse(PlayerReply reply, String localPlayerName){
        ArrayList<PlayerInfoBase> resultArray = new ArrayList<>();

        resultArray.add(new PlayerInfoHeader("<b>General Statistics</b>", GeneralStatistics.parseGeneral(reply, localPlayerName)));

        if (reply.getPlayer().has("packageRank")) {
            resultArray.add(new PlayerInfoHeader("<b>Donator Information</b>", DonatorStatistics.parseDonor(reply)));
        }

        if (MainStaticVars.isStaff || MainStaticVars.isCreator) {
            if (reply.getPlayer().has("rank")) {
                if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")) {
                    if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")) {
                        resultArray.add(new PlayerInfoHeader("<b>YouTuber Information</b>", StaffOrYtStatistics.parsePriviledged(reply)));
                    } else {
                        resultArray.add(new PlayerInfoHeader("<b>Staff Information</b>", StaffOrYtStatistics.parsePriviledged(reply)));
                    }
                }
            }
        }

        //If there is only 1 item, expand it out
        if (resultArray.size() == 1){
            PlayerInfoHeader header = (PlayerInfoHeader) resultArray.get(0);
            List<PlayerInfoStatistics> s = header.getChild();
            resultArray.clear();
            for (PlayerInfoStatistics p : s){
                resultArray.add(p);
            }
        }

        for (PlayerInfoBase base : resultArray) {
            if (!(base instanceof PlayerInfoHeader)) {
                PlayerInfoStatistics statistics = (PlayerInfoStatistics) base;
                if (statistics.getMessage() != null) statistics.setMessage(StatisticsHelper.parseColorInPlayerStats(statistics.getMessage()));
                if (statistics.getTitle() != null) statistics.setTitle(StatisticsHelper.parseColorInPlayerStats(statistics.getTitle()));
                continue;
            }

            PlayerInfoHeader e = (PlayerInfoHeader) base;
            e.setTitle(StatisticsHelper.parseColorInPlayerStats(e.getTitle()));
            if (!e.hasChild()) continue;

            List<PlayerInfoStatistics> array = e.getChild();
            for (PlayerInfoStatistics child : array){
                if (child.getMessage() != null) child.setMessage(StatisticsHelper.parseColorInPlayerStats(child.getMessage()));
                if (child.getTitle() != null) child.setTitle(StatisticsHelper.parseColorInPlayerStats(child.getTitle()));
            }
        }

        PlayerInfoExpandableRecyclerAdapter adapter = new PlayerInfoExpandableRecyclerAdapter(resultArray, getActivity());
        recyclerView.setAdapter(adapter);
    }
}
