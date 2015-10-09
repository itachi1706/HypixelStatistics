package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.itachi1706.hypixelstatistics.FriendListActivity;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.GuildActivity;
import com.itachi1706.hypixelstatistics.ListViewAdapters.ExpandedResultDescListAdapter;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.PlayerStatistics.DonatorStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.GameStatisticsHandler;
import com.itachi1706.hypixelstatistics.PlayerStatistics.GeneralStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.OngoingAchievementStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.ParkourStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.QuestStatistics;
import com.itachi1706.hypixelstatistics.PlayerStatistics.StaffOrYtStatistics;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQuerySession;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerInfoActivityFragment extends BaseFragmentCompat {

    public PlayerInfoActivityFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_player_info;
    }

    //Fragment Elements
    private TextView session;
    private ExpandableListView generalDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        generalDetails = (ExpandableListView) v.findViewById(R.id.players_lvGeneral);
        session = (TextView) v.findViewById(R.id.player_tvSessionInfo);

        generalDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });

        session.setText("To start, press the Search icon!");
        session.setVisibility(View.VISIBLE);

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
        String uuidSession = reply.getPlayer().get("uuid").getAsString();
        session.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§fQuerying session info...§r")));
        new PlayerInfoQuerySession(session).execute(uuidSession);

        //Get Local Player Name
        String localPlayerName;
        if (MinecraftColorCodes.checkDisplayName(reply))
            localPlayerName = reply.getPlayer().get("displayname").getAsString();
        else
            localPlayerName = reply.getPlayer().get("playername").getAsString();

        parse(reply, localPlayerName);
    }

    private void parse(PlayerReply reply, String localPlayerName){
        ArrayList<ResultDescription> resultArray = new ArrayList<>();
        resultArray.add(new ResultDescription("<b>General Statistics</b>", null, false, GeneralStatistics.parseGeneral(reply, localPlayerName), null));

        if (reply.getPlayer().has("packageRank")) {
            resultArray.add(new ResultDescription("<b>Donator Information</b>", null, false, DonatorStatistics.parseDonor(reply), null));
        }

        if (MainStaticVars.isStaff || MainStaticVars.isCreator) {
            if (reply.getPlayer().has("rank")) {
                if (!reply.getPlayer().get("rank").getAsString().equals("NORMAL")) {
                    if (reply.getPlayer().get("rank").getAsString().equals("YOUTUBER")) {
                        resultArray.add(new ResultDescription("<b>YouTuber Information</b>", null, false, StaffOrYtStatistics.parsePriviledged(reply), null));
                    } else {
                        resultArray.add(new ResultDescription("<b>Staff Information</b>", null, false, StaffOrYtStatistics.parsePriviledged(reply), null));
                    }
                }
            }
        }

        if (reply.getPlayer().has("achievements")){
            resultArray.add(new ResultDescription("<b>Ongoing Achievements</b>", null, false, OngoingAchievementStatistics.parseOngoingAchievements(reply), null));
        }

        if (reply.getPlayer().has("quests")){
            resultArray.add(new ResultDescription("<b>Quest Stats</b>", null, false, QuestStatistics.parseQuests(reply), null));
        }
        if (reply.getPlayer().has("parkourCompletions")) {
            resultArray.add(new ResultDescription("<b>Parkour Stats</b>", null, false, ParkourStatistics.parseParkourCounts(reply), null));
        }

        if (reply.getPlayer().has("stats")){
            ArrayList<ResultDescription> tmp = GameStatisticsHandler.parseStats(reply, localPlayerName);
            for (ResultDescription t : tmp){
                resultArray.add(t);
            }
        }

        for (ResultDescription e : resultArray) {
            if (e.get_result() != null) {
                e.set_result(parseColorsInResults(e));
            }
            if (e.get_childItems() != null){
                for (ResultDescription ex : e.get_childItems()){
                    if (ex.get_result() != null){
                        ex.set_result(parseColorsInResults(ex));
                    }
                }
            }
        }

        ExpandedResultDescListAdapter adapter = new ExpandedResultDescListAdapter(getActivity(), resultArray);
        generalDetails.setAdapter(adapter);
    }

    private String parseColorsInResults(ResultDescription e){
        String r = e.get_result();
        if (e.get_result().equalsIgnoreCase("true") || e.get_result().equalsIgnoreCase("enabled")) {
            return MinecraftColorCodes.parseColors("§a" + r + "§r");
        }
        if (e.get_result().equalsIgnoreCase("false") || e.get_result().equalsIgnoreCase("disabled")) {
            return MinecraftColorCodes.parseColors("§c" + r + "§r");
        }
        if((e.get_result().equalsIgnoreCase("null") || e.get_result() == null) && e.is_hasDescription()){
            return MinecraftColorCodes.parseColors("§c" + "NONE" + "§r");
        }
        return r;
    }
}
