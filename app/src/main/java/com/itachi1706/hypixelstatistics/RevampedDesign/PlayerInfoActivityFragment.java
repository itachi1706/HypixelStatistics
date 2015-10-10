package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.itachi1706.hypixelstatistics.FriendListActivity;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.GuildActivity;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.DonatorStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatisticsHandler;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GeneralStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.OngoingAchievementStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.ParkourStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.QuestStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StaffOrYtStatistics;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoBase;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoHeader;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.PlayerInfoExpandableRecyclerAdapter;
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

        //session.setText("To start, press the Search icon!");
        //session.setVisibility(View.VISIBLE);

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_player_info_expanded, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), GeneralPrefActivity.class));
            return true;
        } else if (id == R.id.view_debug) {
            new AlertDialog.Builder(getActivity()).setTitle("JSON Information")
                    .setMessage(MainStaticVars.playerJsonString)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else if (id == R.id.view_known_alias){
            String message;
            if (!MainStaticVars.playerJsonString.equals("")){
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(MainStaticVars.playerJsonString, PlayerReply.class);
                if (reply.getPlayer().has("knownAliases")){
                    JsonArray arr = reply.getPlayer().getAsJsonArray("knownAliases");
                    StringBuilder listOfAliases = new StringBuilder();
                    for (JsonElement e : arr){
                        listOfAliases.append(e.getAsString()).append("\n");
                    }
                    MainStaticVars.knownAliases = listOfAliases.toString();
                    message = "\n" + MainStaticVars.knownAliases;
                } else {
                    message = "\nPlayer has no known aliases";
                }
            } else
                message = "\nPlease query for a player to view his aliases";
            AlertDialog.Builder debugAlert = new AlertDialog.Builder(getActivity());
            debugAlert.setTitle("Known Aliases");
            debugAlert.setMessage(message);
            debugAlert.setPositiveButton(android.R.string.ok, null);
            debugAlert.show();
            return true;
        } else if (id == R.id.view_guild){
            if (MainStaticVars.playerJsonString.length() > 200) {
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(MainStaticVars.playerJsonString, PlayerReply.class);
                if (reply.getPlayer().has("displayname")) {
                    Intent intent = new Intent(getActivity(), GuildActivity.class);
                    intent.putExtra("playername", reply.getPlayer().get("displayname").getAsString());
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(getActivity()).setTitle("Player not found")
                            .setMessage("Please search a player to view guild")
                            .setPositiveButton(android.R.string.ok, null).show();
                }
            } else {
                new AlertDialog.Builder(getActivity()).setTitle("Player not found")
                        .setMessage("Please search a player to view guild")
                        .setPositiveButton(android.R.string.ok, null).show();
            }

        } else if (id == R.id.view_friends){
            if (MainStaticVars.playerJsonString.length() > 200) {
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(MainStaticVars.playerJsonString, PlayerReply.class);
                if (reply.getPlayer().has("uuid")) {
                    Intent intent = new Intent(getActivity(), FriendListActivity.class);
                    intent.putExtra("playeruuid", reply.getPlayer().get("uuid").getAsString());
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(getActivity()).setTitle("Player not found")
                            .setMessage("Please search a player to view his/her friend's list!")
                            .setPositiveButton(android.R.string.ok, null).show();
                }
            } else {
                new AlertDialog.Builder(getActivity()).setTitle("Player not found")
                        .setMessage("Please search a player to view his/her friend's list!")
                        .setPositiveButton(android.R.string.ok, null).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processPlayerJson(String json){
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
                if (statistics.getMessage() != null) statistics.setMessage(parseColorInPlayerStats(statistics.getMessage()));
                if (statistics.getTitle() != null) statistics.setTitle(parseColorInPlayerStats(statistics.getTitle()));
                continue;
            }

            PlayerInfoHeader e = (PlayerInfoHeader) base;
            e.setTitle(parseColorInPlayerStats(e.getTitle()));
            if (!e.hasChild()) continue;

            List<PlayerInfoStatistics> array = e.getChild();
            for (PlayerInfoStatistics child : array){
                if (child.getMessage() != null) child.setMessage(parseColorInPlayerStats(child.getMessage()));
                if (child.getTitle() != null) child.setTitle(parseColorInPlayerStats(child.getTitle()));
            }
        }

        PlayerInfoExpandableRecyclerAdapter adapter = new PlayerInfoExpandableRecyclerAdapter(resultArray, getActivity());
        generalDetails.setAdapter(adapter);
    }

    private String parseColorInPlayerStats(String message){
        if (message.equalsIgnoreCase("true") )

        if (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("enabled")) {
            return MinecraftColorCodes.parseColors("§a" + message + "§r");
        }
        if (message.equalsIgnoreCase("false") || message.equalsIgnoreCase("disabled")) {
            return MinecraftColorCodes.parseColors("§c" + message + "§r");
        }
        if((message.equalsIgnoreCase("null"))){
            return MinecraftColorCodes.parseColors("§c" + "NONE" + "§r");
        }
        return message;
    }
}
