package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.itachi1706.hypixelstatistics.FriendListActivity;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.GuildActivity;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.GetPlayerByNameExpandedNew;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerInfoActivityFragment extends Fragment {

    public PlayerInfoActivityFragment() {
    }

    private View v;
    private SharedPreferences sp;

    //Fragment Elements
    private AutoCompleteTextView playerName;
    private TextView debug, result, session;
    private ExpandableListView generalDetails;
    private Button checkPlayer;
    private ImageView pHead;
    private ProgressDialog checkProgress;
    private ProgressBar headBar;
    private boolean usingUUID = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_player_info, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        playerName = (AutoCompleteTextView) v.findViewById(R.id.PlayersetName);
        debug = (TextView) v.findViewById(R.id.players_tvDebug);
        result = (TextView) v.findViewById(R.id.players_lblResult);
        checkPlayer = (Button) v.findViewById(R.id.PlayersBtnChk);
        playerName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        pHead = (ImageView) v.findViewById(R.id.playersIvPlayerHead);
        debug.setMovementMethod(new ScrollingMovementMethod());
        generalDetails = (ExpandableListView) v.findViewById(R.id.players_lvGeneral);
        headBar = (ProgressBar) v.findViewById(R.id.PlayerspbHead);
        session = (TextView) v.findViewById(R.id.player_tvSessionInfo);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getHistory());
        playerName.setAdapter(adapter);

        final android.support.v7.app.ActionBar supportBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (getActivity().getIntent().hasExtra("player")){
            String intentPlayer = getActivity().getIntent().getStringExtra("player");
            playerName.setText(intentPlayer);
            checkProgress = new ProgressDialog(getActivity());
            checkProgress.setCancelable(false);
            checkProgress.setIndeterminate(true);
            checkProgress.setTitle("Querying Server...");
            checkProgress.setMessage("Getting Player Statistics from the Hypixel API");
            checkProgress.show();
            session.setVisibility(View.INVISIBLE);
            new GetPlayerByNameExpandedNew(result, debug, generalDetails, pHead, checkProgress, headBar, getActivity(), usingUUID, supportBar, session).execute(intentPlayer);
        } else if (getActivity().getIntent().hasExtra("playerUuid")){
            String intentPlayerUid = getActivity().getIntent().getStringExtra("playerUuid");
            playerName.setText(intentPlayerUid);
            checkProgress = new ProgressDialog(getActivity());
            checkProgress.setCancelable(false);
            checkProgress.setIndeterminate(true);
            checkProgress.setTitle("Querying Server...");
            checkProgress.setMessage("Getting Player Statistics from the Hypixel API");
            checkProgress.show();
            usingUUID = true;
            session.setVisibility(View.INVISIBLE);
            new GetPlayerByNameExpandedNew(result, debug, generalDetails, pHead, checkProgress, headBar, getActivity(), true, supportBar, session).execute(intentPlayerUid);
        }

        //Check if we should hide the debug window
        if (!(sp.getBoolean("debugMode", true))){
            debug.setVisibility(View.INVISIBLE);
        } else {
            debug.setVisibility(View.VISIBLE);
        }

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        playerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    playerName.clearFocus();
                    imm.hideSoftInputFromWindow(playerName.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    checkPlayer.performClick();
                }
                return true;
            }
        });

        checkPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName.clearFocus();
                imm.hideSoftInputFromWindow(playerName.getWindowToken(), 0);
                if (playerName.getText().toString().equals("")) {
                    if (playerName.getHint().toString().contains("UUID")) {
                        playerName.setError("Please enter a UUID!");
                    } else {
                        playerName.setError("Please enter a name!");
                    }
                } else {
                    String name = playerName.getText().toString();
                    checkProgress = new ProgressDialog(getActivity());
                    checkProgress.setCancelable(false);
                    checkProgress.setIndeterminate(true);
                    checkProgress.setTitle("Querying Server...");
                    checkProgress.setMessage("Getting Player Statistics from the Hypixel API");
                    checkProgress.show();
                    session.setVisibility(View.INVISIBLE);
                    new GetPlayerByNameExpandedNew(result, debug, generalDetails, pHead, checkProgress, headBar, getActivity(), usingUUID, supportBar, session).execute(name);
                }
            }
        });

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

        setHasOptionsMenu(true);
        
        return v;
    }

    private String[] getHistory(){
        String hist = CharHistory.getListOfHistory(sp);
        ArrayList<String> tmp = new ArrayList<>();
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            for (HistoryArrayObject histCheckName : histCheck) {
                tmp.add(histCheckName.getDisplayname());
            }
        }

        String[] results = new String[tmp.size()];
        for (int i = 0; i < results.length; i++){
            results[i] = tmp.get(i);
        }
        return results;
    }

    @Override
    public void onResume(){
        super.onResume();
        //Check if we should hide the debug window
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getHistory());
        MainStaticVars.resetKnownAliases();
        MainStaticVars.updateTimeout(getActivity());
        playerName.setAdapter(adapter);
        if (!(sp.getBoolean("debugMode", true))){
            debug.setVisibility(View.INVISIBLE);
        } else {
            debug.setVisibility(View.VISIBLE);
        }

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
            TextView jsonDebug = new TextView(getActivity());
            jsonDebug.setText(debug.getText());
            jsonDebug.setGravity(Gravity.CENTER);
            LinearLayout dialogLayout = new LinearLayout(getActivity());

            dialogLayout.addView(jsonDebug);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            AlertDialog.Builder debugAlert = new AlertDialog.Builder(getActivity());
            ScrollView scrollPane = new ScrollView(getActivity());
            scrollPane.addView(dialogLayout);
            debugAlert.setView(scrollPane);
            debugAlert.setTitle("JSON Information");
            debugAlert.setPositiveButton(android.R.string.ok, null);
            debugAlert.show();
            return true;
        } else if (id == R.id.use_uuid_instead_of_name){
            if (usingUUID){
                usingUUID = false;
                item.setTitle("Search with UUID");
                playerName.setHint("Player Name");
            } else {
                usingUUID = true;
                item.setTitle("Search with Name");
                playerName.setHint("Player UUID");
            }
        } else if (id == R.id.view_known_alias){
            String message;
            if (debug.getText().length() > 200) {
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(debug.getText().toString(), PlayerReply.class);
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
            }else
                message = "\nPlease query for a player to view his aliases";
            AlertDialog.Builder debugAlert = new AlertDialog.Builder(getActivity());
            debugAlert.setTitle("Known Aliases");
            debugAlert.setMessage(message);
            debugAlert.setPositiveButton(android.R.string.ok, null);
            debugAlert.show();
            return true;
        } else if (id == R.id.view_guild){
            if (debug.getText().length() > 200) {
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(debug.getText().toString(), PlayerReply.class);
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
            if (debug.getText().length() > 200) {
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(debug.getText().toString(), PlayerReply.class);
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
}
