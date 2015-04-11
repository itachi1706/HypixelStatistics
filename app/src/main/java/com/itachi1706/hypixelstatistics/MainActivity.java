package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.AppUpdateCheck;
import com.itachi1706.hypixelstatistics.AsyncAPI.Boosters.BoosterGet;
import com.itachi1706.hypixelstatistics.AsyncAPI.Boosters.BoosterGetBrief;
import com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck.GetKeyInfoVerificationName;
import com.itachi1706.hypixelstatistics.ServerPinging.InitServerPing;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.CustomExceptionHandler;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import java.io.File;
import java.util.ArrayList;


@SuppressWarnings("ConstantConditions")
public class MainActivity extends ActionBarActivity {

    ListView mainMenu, boosterMenu;
    TextView customWelcome, boosterTooltip, playerCount;
    ProgressBar boostProg;
    String[] mainMenuItems = {"Search Player", "View Activated Boosters", "Search Guild"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init error handling
        File crashReportFolder = new File(this.getExternalFilesDir(null) + File.separator + "crash-report");
        if (!crashReportFolder.exists())
            if (crashReportFolder.mkdir())
                Log.d("CRASH-REPORT HANDLER", "Directory Created!");
        CustomExceptionHandler crashHandler = new CustomExceptionHandler(this.getExternalFilesDir(null) + File.separator +
                "crash-report", this);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        }
        crashHandler.checkCrash();

        MainStaticVars.updateBriefBoosterPref(getApplicationContext());

        //Check for legacy strings
        CharHistory.verifyNoLegacy(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        if (this.getIntent().hasExtra("EXIT"))
            if (getIntent().getBooleanExtra("EXIT", false))
                finish();

        mainMenu = (ListView) findViewById(R.id.lvMainMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuItems);
        mainMenu.setAdapter(adapter);
        boostProg = (ProgressBar) findViewById(R.id.pbABoost);
        boosterTooltip = (TextView) findViewById(R.id.tvBoosterTooltip);
        playerCount = (TextView) findViewById(R.id.tvPlayerCount);

        mainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) mainMenu.getItemAtPosition(position);
                checkMainMenuSelection(selected);
            }});

        boosterMenu = (ListView) findViewById(R.id.lvBoostersActive);
        boosterMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoosterDescription sel = (BoosterDescription) boosterMenu.getItemAtPosition(position);
                if (MainStaticVars.isUsingDetailedActiveBooster) {
                    Intent intentE = new Intent(MainActivity.this, ExpandedPlayerInfoActivity.class);
                    intentE.putExtra("player", sel.get_mcName());
                    startActivity(intentE);
                }
            }
        });

        MainStaticVars.updateAPIKey(getApplicationContext());
        if (!MainStaticVars.boosterUpdated) {
            updateActiveBoosters();
        }

        new AppUpdateCheck(this, PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()), true).execute();
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshServerCount();

        if (this.getIntent().hasExtra("EXIT"))
            if (getIntent().getBooleanExtra("EXIT", false))
                finish();

        MainStaticVars.updateAPIKey(getApplicationContext());
        MainStaticVars.updateBriefBoosterPref(getApplicationContext());
        customWelcome = (TextView) findViewById(R.id.tvCustWelcome);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getString("own", "n").equals("n"))
            new GetKeyInfoVerificationName(this, prefs, null, null, null, false).execute(prefs.getString("own", "Steve"));
        String playerName = prefs.getString("playerName", "NOPE");
        //Log.d("DEBUG", playerName);
        if (playerName.equals("NOPE")){
            customWelcome.setVisibility(View.INVISIBLE);
        } else {
            customWelcome.setVisibility(View.VISIBLE);
            customWelcome.setText(Html.fromHtml("Welcome " + playerName + "!"));
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.d("Button", "Back button pressed. killing app");
        MainStaticVars.boosterUpdated = false;
        finish();
    }

    private void updateActiveBoosters(){
        ArrayList<BoosterDescription> repop = new ArrayList<>();
        BoosterDescListAdapter adapter = new BoosterDescListAdapter(getApplicationContext(), R.layout.listview_booster_desc, repop);
        boosterMenu.setAdapter(adapter);
        boostProg.setVisibility(View.VISIBLE);
        if (MainStaticVars.isUsingDetailedActiveBooster)
            new BoosterGet(this.getApplicationContext(), boosterMenu, true, boostProg, boosterTooltip).execute();
        else
            new BoosterGetBrief(this.getApplicationContext(), boosterMenu, boostProg, boosterTooltip).execute();
    }

    private void checkMainMenuSelection(String selection){
        switch (selection){
            case "Search Player":
                startActivity(new Intent(MainActivity.this, ExpandedPlayerInfoActivity.class));
                break;
            case "View Activated Boosters":
                if (MainStaticVars.inProg){
                    new AlertDialog.Builder(this).setMessage("The app is still processing the booster list. Please wait a while before selecting this option.")
                            .setTitle("Notice").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
                }
                startActivity(new Intent(MainActivity.this, BoosterList.class));
                break;
            case "Search Guild":
                startActivity(new Intent(MainActivity.this, GuildActivity.class));
                break;
        }
    }

    private void refreshServerCount(){
        //Refresh server...
        playerCount.setText("Querying Server Player Count...");
        new InitServerPing(getApplicationContext(), playerCount).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(MainActivity.this, GeneralPrefActivity.class));
            return true;
        } else if (id == R.id.action_refresh_active_boosters){
            updateActiveBoosters();
            Toast.makeText(this.getApplicationContext(), "Updating Active Booster List", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_refresh_server_info){
            refreshServerCount();
            Toast.makeText(this.getApplicationContext(), "Updating Server Info...", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_view_server_motd){
            new AlertDialog.Builder(this).setTitle("Hypixel MOTD")
                    .setMessage(Html.fromHtml(MainStaticVars.serverMOTD)).setPositiveButton(android.R.string.ok, null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
