package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck.GetKeyInfoVerificationName;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Booster.GetBriefBoosters;
import com.itachi1706.hypixelstatistics.RevampedDesign.BoosterActivity;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerInfoActivity;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.BriefBoosterRecyclerAdapter;
import com.itachi1706.hypixelstatistics.ServerPinging.InitServerPing;
import com.itachi1706.hypixelstatistics.Updater.AppUpdateChecker;
import com.itachi1706.hypixelstatistics.Updater.Util.UpdaterHelper;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    ListView mainMenu;
    RecyclerView boosterMenu;
    TextView customWelcome, boosterTooltip, playerCount;
    ProgressBar boostProg;
    ArrayAdapter<String> adapter;
    String[] mainMenuItems = {"Player Info", "View Activated Boosters", "Search Guild"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics crashlyticsKit = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build();
        Fabric.with(this, crashlyticsKit);

        //Set Theme
        MainStaticVars.setLayoutAccordingToPrefs(this);

        setContentView(R.layout.activity_main);

        //Check for legacy strings
        CharHistory.verifyNoLegacy(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        //TODO: Removed for now as it isn't working as intended (Heads are being removed even though they are just obtained)
        //new LegacyHistoryRemoveCheck(this.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (this.getIntent().hasExtra("EXIT"))
            if (getIntent().getBooleanExtra("EXIT", false))
                finish();

        mainMenu = (ListView) findViewById(R.id.lvMainMenu);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuItems);
        mainMenu.setAdapter(adapter);
        boostProg = (ProgressBar) findViewById(R.id.pbABoost);
        boosterTooltip = (TextView) findViewById(R.id.tvBoosterTooltip);
        playerCount = (TextView) findViewById(R.id.tvPlayerCount);

        mainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) mainMenu.getItemAtPosition(position);
                checkMainMenuSelection(selected);
            }
        });

        boosterMenu = (RecyclerView) findViewById(R.id.rvBoostersActive);
        boosterMenu.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        boosterMenu.setLayoutManager(linearLayoutManager);
        boosterMenu.setItemAnimator(new DefaultItemAnimator());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (UpdaterHelper.canCheckUpdate(sp, this)) {
            Log.i("Updater", "Checking for new updates...");
            new AppUpdateChecker(this, sp, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        MainStaticVars.updateTimeout(this);

        refreshServerCount();

        if (this.getIntent().hasExtra("EXIT"))
            if (getIntent().getBooleanExtra("EXIT", false))
                finish();

        MainStaticVars.updateAPIKey(getApplicationContext());
        customWelcome = (TextView) findViewById(R.id.tvCustWelcome);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getString("own", "n").equals("n"))
            new GetKeyInfoVerificationName(this, prefs, null, null, null, false).execute(prefs.getString("own", "Steve"));
        String playerName = prefs.getString("playerName", "NOPE");
        //Log.d("DEBUG", playerName);
        if (playerName.equals("NOPE")){
            customWelcome.setVisibility(View.INVISIBLE);

            //Remove the view your friend's list stuff
            ArrayList<String> mainMenuWithoutFriends = new ArrayList<>(Arrays.asList(mainMenuItems));
            mainMenuWithoutFriends.remove("View your Friend's List (BETA)");
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuWithoutFriends);
            mainMenu.setAdapter(adapter);
        } else {
            customWelcome.setVisibility(View.VISIBLE);
            customWelcome.setText(Html.fromHtml("Welcome " + playerName + "!"));

            //Update main menu again
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuItems);
            mainMenu.setAdapter(adapter);
        }

        if (!MainStaticVars.boosterUpdated) {
            updateActiveBoosters();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.d("Button", "Back button pressed. killing app");
        MainStaticVars.boosterUpdated = false;
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }

    private void updateActiveBoosters(){
        ArrayList<BoosterDescription> repop = new ArrayList<>();
        BriefBoosterRecyclerAdapter adapter = new BriefBoosterRecyclerAdapter(repop);
        boosterMenu.setAdapter(adapter);
        boostProg.setVisibility(View.VISIBLE);
        new GetBriefBoosters(this, boosterMenu, boostProg, boosterTooltip).execute();
    }

    private void checkMainMenuSelection(String selection){
        switch (selection){
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
                startActivity(new Intent(MainActivity.this, BoosterActivity.class));
                break;
            case "Search Guild":
                startActivity(new Intent(MainActivity.this, GuildActivity.class));
                break;
            case "Player Info":
                startActivity(new Intent(MainActivity.this, PlayerInfoActivity.class));
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, GeneralPrefActivity.class));
            return true;
        } else if (id == R.id.action_refresh_active_boosters){
            updateActiveBoosters();
            NotifyUserUtil.createShortToast(this.getApplicationContext(), "Updating Active Booster List");
        } else if (id == R.id.action_refresh_server_info){
            refreshServerCount();
            NotifyUserUtil.createShortToast(this.getApplicationContext(), "Updating Server Info...");
        } else if (id == R.id.action_view_server_motd){
            new AlertDialog.Builder(this).setTitle("Hypixel MOTD")
                    .setMessage(Html.fromHtml(MainStaticVars.serverMOTD)).setPositiveButton(android.R.string.ok, null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
