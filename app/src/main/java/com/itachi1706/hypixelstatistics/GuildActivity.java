package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.AsyncAPI.GetGuildId;
import com.itachi1706.hypixelstatistics.util.CharHistory;
import com.itachi1706.hypixelstatistics.util.GuildMemberDesc;
import com.itachi1706.hypixelstatistics.util.HistoryObject;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.ResultDescription;

import java.util.ArrayList;


public class GuildActivity extends ActionBarActivity {

    AutoCompleteTextView guildSearch;
    ToggleButton searchFilter;
    Button searchBtn;
    ListView generalInfo, memberInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild);

        guildSearch = (AutoCompleteTextView) findViewById(R.id.inputGuild);
        searchFilter = (ToggleButton) findViewById(R.id.tbGuild);
        searchBtn = (Button) findViewById(R.id.btnSearch);
        generalInfo = (ListView) findViewById(R.id.lvGeneral);
        memberInfo = (ListView) findViewById(R.id.lvMember);

        //Settings
        guildSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getHistory());
        guildSearch.setAdapter(adapter);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        guildSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    guildSearch.clearFocus();
                    imm.hideSoftInputFromWindow(guildSearch.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    searchBtn.performClick();
                }
                return true;
            }
        });

        //On Click Listeners
        generalInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (generalInfo.getItemAtPosition(position) instanceof ResultDescription) {
                    ResultDescription desc = (ResultDescription) generalInfo.getItemAtPosition(position);
                    if (desc.get_alert() != null){
                        new AlertDialog.Builder(GuildActivity.this).setTitle(Html.fromHtml(desc.get_title()))
                                .setMessage(Html.fromHtml(MinecraftColorCodes.parseColors(desc.get_alert()))).setPositiveButton(android.R.string.ok, null).show();
                    }
                }
            }
        });

        memberInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (memberInfo.getItemAtPosition(position) instanceof GuildMemberDesc) {
                    GuildMemberDesc desc = (GuildMemberDesc) memberInfo.getItemAtPosition(position);
                    if (desc.get_dailyCoins() != null){
                        new AlertDialog.Builder(GuildActivity.this).setTitle(Html.fromHtml("Recent Daily Coin Contributions by " + desc.get_mcNameWithRank()))
                                .setMessage(Html.fromHtml(MinecraftColorCodes.parseColors(desc.get_dailyCoins()))).setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton("View Player Info", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GuildMemberDesc sel = (GuildMemberDesc) memberInfo.getItemAtPosition(position);
                                        Intent intentE = new Intent(GuildActivity.this, ExpandedPlayerInfoActivity.class);
                                        intentE.putExtra("player", sel.get_mcName());
                                        startActivity(intentE);
                                    }
                                }).show();
                    } else {
                        new AlertDialog.Builder(GuildActivity.this).setTitle(Html.fromHtml("Recent Daily Coins Contributions by " + desc.get_mcNameWithRank()))
                                .setMessage(Html.fromHtml(MinecraftColorCodes.parseColors("This member did not make any recent contributions to the guild"))).setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton("View Player Info", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GuildMemberDesc sel = (GuildMemberDesc) memberInfo.getItemAtPosition(position);
                                        Intent intentE = new Intent(GuildActivity.this, ExpandedPlayerInfoActivity.class);
                                        intentE.putExtra("player", sel.get_mcName());
                                        startActivity(intentE);
                                    }
                                }).show();
                    }
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guildSearch.clearFocus();
                imm.hideSoftInputFromWindow(guildSearch.getWindowToken(), 0);
                if (guildSearch.getText().toString().equals("") || guildSearch.getText().toString() == null){
                    Toast.makeText(getApplicationContext(), "Please enter a guild name or player!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean state = searchFilter.isChecked();
                    String searchText = guildSearch.getText().toString();
                    Toast.makeText(getApplicationContext(), "Querying Guild Information", Toast.LENGTH_SHORT).show();
                    new GetGuildId(state, GuildActivity.this, generalInfo, memberInfo).execute(searchText);
                }
            }
        });

    }

    private String[] getHistory(){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        ArrayList<String> tmp = new ArrayList<>();
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            JsonArray histCheck = check.getHistory();
            for (JsonElement el : histCheck) {
                JsonObject histCheckName = el.getAsJsonObject();
                tmp.add(histCheckName.get("displayname").getAsString());
            }
        }

        String[] results = new String[tmp.size()];
        for (int i = 0; i < results.length; i++){
            results[i] = tmp.get(i);
        }
        return results;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guild, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(GuildActivity.this, GeneralPrefActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
