package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
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
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQuery;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.PlayerReply;

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
    private boolean usingUUID = false;
    private ActionBar supportBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        generalDetails = (ExpandableListView) v.findViewById(R.id.players_lvGeneral);
        session = (TextView) v.findViewById(R.id.player_tvSessionInfo);

        supportBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (getActivity().getIntent().hasExtra("player")){
            String intentPlayer = getActivity().getIntent().getStringExtra("player");
            queryPlayerInfo(intentPlayer);
        } else if (getActivity().getIntent().hasExtra("playerUuid")){
            String intentPlayerUid = getActivity().getIntent().getStringExtra("playerUuid");
            usingUUID = true;
            queryPlayerInfo(intentPlayerUid);
        }

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

        MainStaticVars.playerJsonString = "";
        setHasOptionsMenu(true);
        
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        //Check if we should hide the debug window
        Log.d("Player Info", "Resuming App");

        MainStaticVars.resetKnownAliases();
        MainStaticVars.updateTimeout(getActivity());
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
    public void queryPlayerInfo(String query) {
        if (!usingUUID) { //Not confirmed UUID, go and check
            if (query.length() == 32) usingUUID = true; //Might be UUID
        }
        ProgressDialog checkProgress = new ProgressDialog(getActivity());
        checkProgress.setCancelable(false);
        checkProgress.setIndeterminate(true);
        checkProgress.setTitle("Querying Server...");
        checkProgress.setMessage("Getting Player Statistics from the Hypixel API");
        checkProgress.show();
        session.setVisibility(View.INVISIBLE);
        new PlayerInfoQuery(generalDetails, checkProgress, getActivity(), usingUUID, supportBar, session).execute(query);
        usingUUID = false;
    }
}
