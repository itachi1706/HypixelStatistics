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

import com.itachi1706.hypixelstatistics.AsyncAPI.BoosterGet;
import com.itachi1706.hypixelstatistics.AsyncAPI.GetKeyInfoVerificationName;
import com.itachi1706.hypixelstatistics.util.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView mainMenu, boosterMenu;
    TextView customWelcome;
    ProgressBar boostProg;
    String[] mainMenuItems = {"Search Player", "View Activated Boosters", "Search Guild"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (this.getIntent().hasExtra("EXIT"))
            if (getIntent().getBooleanExtra("EXIT", false))
                finish();

        mainMenu = (ListView) findViewById(R.id.lvMainMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuItems);
        mainMenu.setAdapter(adapter);
        boostProg = (ProgressBar) findViewById(R.id.pbABoost);

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
                Intent intentE = new Intent(MainActivity.this, ExpandedPlayerInfoActivity.class);
                intentE.putExtra("player", sel.get_mcName());
                startActivity(intentE);
            }
        });
        MainStaticVars.updateAPIKey(getApplicationContext());
        if (!MainStaticVars.boosterUpdated) {
            updateActiveBoosters();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        MainStaticVars.updateAPIKey(getApplicationContext());
        customWelcome = (TextView) findViewById(R.id.tvCustWelcome);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getString("own", "n").equals("n"))
            new GetKeyInfoVerificationName(getApplicationContext(), prefs, null, null, false).execute(prefs.getString("own", "Steve"));
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
        new BoosterGet(this.getApplicationContext(), boosterMenu, true, boostProg).execute();
    }

    private void checkMainMenuSelection(String selection){
        switch (selection){
            case "Search Player":
                startActivity(new Intent(MainActivity.this, ExpandedPlayerInfoActivity.class));
                break;
            case "View Activated Boosters":
                if (MainStaticVars.inProg){
                    new AlertDialog.Builder(this).setMessage("The app is still getting a list of boosters. Please wait a while before trying to view boosters")
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
        }

        return super.onOptionsItemSelected(item);
    }
}
