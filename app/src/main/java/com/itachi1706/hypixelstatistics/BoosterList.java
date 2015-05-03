package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.AsyncAPI.Boosters.BoosterGet;
import com.itachi1706.hypixelstatistics.AsyncAPI.Boosters.BoosterGetHistory;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.BoosterDescListAdapter;
import com.itachi1706.hypixelstatistics.util.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import net.hypixel.api.reply.BoostersReply;
import net.hypixel.api.util.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BoosterList extends AppCompatActivity {

    ListView boostList;
    ProgressBar prog;
    TextView boosterTooltip;

    //TODO Used for filtering and dynamic update of boosters
    BoosterDescListAdapter adapter;
    //TODO Used for filtering
    final ArrayList seletedFilterItems=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Theme
        setTheme(MainStaticVars.getTheme(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(getResources().getColor(MainStaticVars.getStatusBarColor(this)));

        setContentView(R.layout.activity_booster_list);

        MainStaticVars.updateTimeout(this);
        boostList = (ListView) findViewById(R.id.BoostlvBooster);
        prog = (ProgressBar) findViewById(R.id.BoostpbProg);
        boosterTooltip = (TextView) findViewById(R.id.tvBoosterTooltip);

        boostList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoosterDescription sel = (BoosterDescription) boostList.getItemAtPosition(position);
                Intent intentE = new Intent(BoosterList.this, ExpandedPlayerInfoActivity.class);
                intentE.putExtra("player", sel.get_mcName());
                startActivity(intentE);
            }
        });

        if (!MainStaticVars.boosterUpdated){
            if (!MainStaticVars.isBriefBooster) {
                updateActiveBoosters();
            } else {
                //Parse Brief Booster
                parseBriefBoosters();
            }
        } else {
            if (MainStaticVars.boosterList.size() != 0) {
                adapter = new BoosterDescListAdapter(getApplicationContext(), R.layout.listview_booster_desc, MainStaticVars.boosterList);
                boostList.setAdapter(adapter);
                assert BoosterList.this.getSupportActionBar() != null;
                this.getSupportActionBar().setTitle(this.getResources().getString(R.string.title_activity_booster_list) + " (" + MainStaticVars.boosterList.size() + ")");
            } else {
                String[] tmp = {"No Boosters Activated"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tmp);
                boostList.setAdapter(adapter);
            }
        }
    }

    private void parseBriefBoosters(){
        final String jsonString = MainStaticVars.boosterJsonString;
        if (jsonString != null && jsonString.length() > 50){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    BoostersReply reply = gson.fromJson(jsonString, BoostersReply.class);
                    MainStaticVars.boosterList.clear();
                    MainStaticVars.boosterUpdated = false;
                    MainStaticVars.inProg = true;
                    JsonArray records = reply.getRecords().getAsJsonArray();
                    MainStaticVars.numOfBoosters = records.size();
                    MainStaticVars.tmpBooster = 0;
                    MainStaticVars.boosterProcessCounter = 0;
                    MainStaticVars.boosterMaxProcessCounter = 0;
                    prog.setVisibility(View.VISIBLE);

                    if (records.size() != 0) {
                        MainStaticVars.boosterMaxProcessCounter = records.size();
                        assert BoosterList.this.getSupportActionBar() != null;
                        BoosterList.this.getSupportActionBar().setTitle(BoosterList.this.getResources().getString(R.string.title_activity_booster_list) + " (" + MainStaticVars.boosterMaxProcessCounter + ")");
                        for (JsonElement e : records) {
                            JsonObject obj = e.getAsJsonObject();
                            String uid = obj.get("purchaserUuid").getAsString(); //Get Player UUID
                            final BoosterDescription desc;
                            if (obj.has("purchaser")) {
                                //Old Method
                                desc = new BoosterDescription(obj.get("amount").getAsInt(), obj.get("dateActivated").getAsLong(),
                                        obj.get("gameType").getAsInt(), obj.get("length").getAsInt(), obj.get("originalLength").getAsInt(),
                                        uid, obj.get("purchaser").getAsString());
                            } else {
                                //New Method
                                desc = new BoosterDescription(obj.get("amount").getAsInt(), obj.get("dateActivated").getAsLong(),
                                        obj.get("gameType").getAsInt(), obj.get("length").getAsInt(), obj.get("originalLength").getAsInt(),
                                        uid);
                            }
                            //Move to BoosterGetHistory
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boosterTooltip.setVisibility(View.VISIBLE);
                                    boosterTooltip.setText("Booster list obtained. Processing Players now...");
                                    new BoosterGetHistory(getApplicationContext(), boostList, false, prog, boosterTooltip).execute(desc);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] tmp = {"No Boosters Activated"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tmp);
                                boostList.setAdapter(adapter);
                                prog.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void updateActiveBoosters(){
        ArrayList<BoosterDescription> repop = new ArrayList<>();
        adapter = new BoosterDescListAdapter(getApplicationContext(), R.layout.listview_booster_desc, repop);
        boostList.setAdapter(adapter);
        prog.setVisibility(View.VISIBLE);
        MainStaticVars.boosterUpdated = false;
        MainStaticVars.inProg = false;
        MainStaticVars.parseRes = false;
        new BoosterGet(this.getApplicationContext(), boostList, false, prog, boosterTooltip).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_booster_list, menu);
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
            startActivity(new Intent(BoosterList.this, GeneralPrefActivity.class));
            return true;
        } else if (id == R.id.action_refresh_active_boosters){
            updateActiveBoosters();
            Toast.makeText(this.getApplicationContext(), "Updating Booster List", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_get_detailed_boosters){
            new AlertDialog.Builder(this)
                    .setTitle("Activated Boosters per Game").setMessage(parseStats(null))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String parseStats(ArrayList<BoosterDescription> incomplete){
        ArrayList<BoosterDescription> check;
        if (incomplete != null){
            check = incomplete;
        } else {
             check = MainStaticVars.boosterList;
        }
        HashMap<GameType, Integer> count = new HashMap<>();
        HashMap<GameType, Integer> time = new HashMap<>();
        int unknownGameCount = 0, unknownGameTime = 0;

        for (BoosterDescription desc : check){
            if (desc.get_gameType() != null) {
                //Not Null
                if (count.containsKey(desc.get_gameType()))
                    count.put(desc.get_gameType(), count.get(desc.get_gameType()) + 1);
                else
                    count.put(desc.get_gameType(), 1);
                if (time.containsKey(desc.get_gameType()))
                    time.put(desc.get_gameType(), time.get(desc.get_gameType()) + desc.get_timeRemaining());
                else
                    time.put(desc.get_gameType(), desc.get_timeRemaining());
            } else {
                unknownGameCount ++;
                unknownGameTime += desc.get_timeRemaining();
            }
        }

        StringBuilder boosterStatBuilder = new StringBuilder();
        boosterStatBuilder.append("Based on last booster query: \n\n");
        for (Map.Entry<GameType, Integer> cursor : count.entrySet()){
            if (time.containsKey(cursor.getKey())){
                //Can Continue
                boosterStatBuilder.append(cursor.getKey().getName()).append(": ").append(cursor.getValue()).append("\n").append(createTimeLeftString(time.get(cursor.getKey()))).append("\n");
            } else {
                //Error (No time)
                boosterStatBuilder.append(cursor.getKey().getName()).append(": ").append(cursor.getValue()).append("\n").append(createTimeLeftString(0)).append("\n");
            }
        }
        //Check for unknown games
        if (unknownGameCount != 0){
            boosterStatBuilder.append("Unknown Game: ").append(unknownGameCount).append("\n").append(createTimeLeftString(unknownGameTime)).append("\n");
            boosterStatBuilder.append("(Please Contact Dev of this)");
        }

        return boosterStatBuilder.toString();
    }

    private String createTimeLeftString(int timeRemaining){
        long days, hours, minutes, seconds;

        days = TimeUnit.SECONDS.toDays(timeRemaining);
        hours = TimeUnit.SECONDS.toHours(timeRemaining) - (days * 24);
        minutes = TimeUnit.SECONDS.toMinutes(timeRemaining) - (TimeUnit.SECONDS.toHours(timeRemaining)* 60);
        seconds = TimeUnit.SECONDS.toSeconds(timeRemaining) - (TimeUnit.SECONDS.toMinutes(timeRemaining) * 60);

        //Craft the time statement
        StringBuilder timeString = new StringBuilder();
        timeString.append("(");
        if (days != 0) {
            if (days == 1)
                timeString.append(days).append(" day ");
            else
                timeString.append(days).append(" days ");
        }
        if (hours != 0) {
            if (hours == 1)
                timeString.append(hours).append(" hour ");
            else
                timeString.append(hours).append(" hours ");
        }
        if (minutes != 0) {
            if (minutes == 1)
                timeString.append(minutes).append(" minute ");
            else
                timeString.append(minutes).append(" minutes ");
        }
        if (seconds != 0) {
            if (seconds == 1)
                timeString.append(seconds).append(" second ");
            else
                timeString.append(seconds).append(" seconds ");
        }
        timeString.append(")");
        return timeString.toString();
    }

    //TODO Work on the filtering of boosters (Wait till Dynamic Display for Boosters are done)
    private void displayFilterAlertDialog(){
        //TODO Placeholder. Replace with list of boosters gametypes
        final CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
        // arraylist to keep the selected items
        AlertDialog filterDialog;
        boolean[] isCheckedAlr = new boolean[items.length];
        for (int i = 0; i < items.length; i++){
            //TODO Check for what has been selected and add to isCheckedAlr
            CharSequence seq = items[i];
            if (seq.equals("yay"))
                isCheckedAlr[i] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //TODO Set the title of the filter
        builder.setTitle("Select The Difficulty Level");
        builder.setMultiChoiceItems(items, isCheckedAlr,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedFilterItems.add(indexSelected);
                        } else if (seletedFilterItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedFilterItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                        // TODO Do the filtering here
                    }
                })
                .setNegativeButton("Cancel", null);

        filterDialog = builder.create();//AlertDialog dialog; create like this outside onClick
        filterDialog.show();
    }
}
