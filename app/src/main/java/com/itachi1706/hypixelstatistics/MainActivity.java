package com.itachi1706.hypixelstatistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.BoosterGet;
import com.itachi1706.hypixelstatistics.util.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView mainMenu, boosterMenu;
    ProgressBar boostProg;
    String[] mainMenuItems = {"View Player", "View Activated Boosters"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        if (!MainStaticVars.boosterUpdated) {
            updateActiveBoosters();
        }
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
            case "View Player":
                startActivity(new Intent(MainActivity.this, PlayerInfoActivity.class));
                break;
            case "View Activated Boosters":
                startActivity(new Intent(MainActivity.this, BoosterList.class));
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
