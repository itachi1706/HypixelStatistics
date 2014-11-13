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


public class MainActivity extends ActionBarActivity {

    ListView mainMenu;
    String[] mainMenuItems = {"View API Key Info", "View Player (Legacy)", "View Player"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainMenu = (ListView) findViewById(R.id.lvMainMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainMenuItems);
        mainMenu.setAdapter(adapter);

        mainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) mainMenu.getItemAtPosition(position);
                checkMainMenuSelection(selected);
            }});
    }

    private void checkMainMenuSelection(String selection){
        switch (selection){
            case "View API Key Info":
                Intent intentAPI = new Intent(MainActivity.this, KeyInfoActivity.class);
                startActivity(intentAPI);
                break;
            case "View Player (Legacy)":
                Intent intentOldPlayer = new Intent(MainActivity.this, OldPlayerInfoActivity.class);
                startActivity(intentOldPlayer);
                break;
            case "View Player":
                Intent intentPlayer = new Intent(MainActivity.this, PlayerInfoActivity.class);
                startActivity(intentPlayer);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
