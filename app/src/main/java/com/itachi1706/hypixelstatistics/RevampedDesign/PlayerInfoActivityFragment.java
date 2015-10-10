package com.itachi1706.hypixelstatistics.RevampedDesign;

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
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoBase;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoHeader;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.DonatorStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatisticsHandler;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GeneralStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.OngoingAchievementStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.ParkourStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.QuestStatistics;
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
public class PlayerInfoActivityFragment extends BaseFragmentCompat {

    public PlayerInfoActivityFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_player_info_recycler;
    }

    //Fragment Elements
    //private TextView session;
    private RecyclerView generalDetails;


    static String[] noStatistics = {"To start, press the Search icon!"};
    private StringRecyclerAdapter noStatAdapter;

    private String playerJsonString = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        generalDetails = (RecyclerView) v.findViewById(R.id.rvStatistics);
        generalDetails.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        generalDetails.setLayoutManager(linearLayoutManager);
        generalDetails.setItemAnimator(new DefaultItemAnimator());

        noStatAdapter = new StringRecyclerAdapter(noStatistics);
        
        
        //session = (TextView) v.findViewById(R.id.player_tvSessionInfo);

        
        /*generalDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (generalDetails.getItemAtPosition(position) instanceof ResultDescription) {
                    ResultDescription desc = (ResultDescription) generalDetails.getItemAtPosition(position);
                    if (desc.get_alert() != null) {
                        new AlertDialog.Builder(getActivity()).setTitle(desc.get_title())
                                .setMessage(Html.fromHtml(desc.get_alert())).show();
                    }
                }
            }
        });*/

        generalDetails.setAdapter(noStatAdapter);
        return v;
    }

    @Override
    public void processPlayerJson(String json){
        Log.i("HypixelStatistics", "Switched to PlayerInfoActivityFragment");
        if (json == null || json.equals("")) { generalDetails.setAdapter(noStatAdapter); return; }
        playerJsonString = json;
        Gson gson = new Gson();
        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        process(reply);
    }

    @Override
    public void processPlayerObject(PlayerReply object, String json){
        playerJsonString = json;
        process(object);
    }

    // PROCESS RESULT METHODS (GRABBLED FROM ASYNC TASK)

    private void process(PlayerReply reply){
        generalDetails.setVisibility(View.VISIBLE);

        //Get Session Info
        //TODO: Session Info elsewhere
        //String uuidSession = reply.getPlayer().get("uuid").getAsString();
        //session.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§fQuerying session info...§r")));
        //new PlayerInfoQuerySession(session).execute(uuidSession);

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

        if (reply.getPlayer().has("achievements")){
            resultArray.add(new PlayerInfoHeader("<b>Ongoing Achievements</b>", OngoingAchievementStatistics.parseOngoingAchievements(reply)));
        }

        if (reply.getPlayer().has("quests")){
            resultArray.add(new PlayerInfoHeader("<b>Quest Stats</b>", QuestStatistics.parseQuests(reply)));
        }
        if (reply.getPlayer().has("parkourCompletions")) {
            resultArray.add(new PlayerInfoHeader("<b>Parkour Stats</b>", ParkourStatistics.parseParkourCounts(reply)));
        }

        if (reply.getPlayer().has("stats")){
            ArrayList<PlayerInfoHeader> tmp = GameStatisticsHandler.parseStats(reply, localPlayerName);
            for (PlayerInfoHeader t : tmp){
                resultArray.add(t);
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
        generalDetails.setAdapter(adapter);
    }
}
