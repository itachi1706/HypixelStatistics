package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class BoosterList extends ActionBarActivity {

    ListView boostList;
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booster_list);

        boostList = (ListView) findViewById(R.id.BoostlvBooster);
        prog = (ProgressBar) findViewById(R.id.BoostpbProg);

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
            updateActiveBoosters();
        } else {
            if (MainStaticVars.boosterList.size() != 0) {
                BoosterDescListAdapter adapter = new BoosterDescListAdapter(getApplicationContext(), R.layout.listview_booster_desc, MainStaticVars.boosterList);
                boostList.setAdapter(adapter);
                this.getSupportActionBar().setTitle(this.getResources().getString(R.string.title_activity_booster_list) + " (" + MainStaticVars.boosterList.size() + ")");
            } else {
                String[] tmp = {"No Boosters Activated"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tmp);
                boostList.setAdapter(adapter);
            }
        }
    }

    private void updateActiveBoosters(){
        ArrayList<BoosterDescription> repop = new ArrayList<>();
        BoosterDescListAdapter adapter = new BoosterDescListAdapter(getApplicationContext(), R.layout.listview_booster_desc, repop);
        boostList.setAdapter(adapter);
        prog.setVisibility(View.VISIBLE);
        new BoosterGet(this.getApplicationContext(), boostList, false, prog).execute();
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
                    .setTitle("Active Boosters per Game").setMessage(parseStats())
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String parseStats(){
        ArrayList<BoosterDescription> check = MainStaticVars.boosterList;
        int quake = 0,walls = 0,pb = 0,bsg = 0,tnt = 0,vz = 0,mw = 0,arcade = 0,arena = 0,cac = 0,unknown = 0, uhc = 0;
        for (BoosterDescription desc : check){
            switch (desc.get_gameType().getId()){
                case 2: quake++; break;
                case 3: walls++; break;
                case 4: pb++; break;
                case 5: bsg++; break;
                case 6: tnt++; break;
                case 7: vz++; break;
                case 13: mw++; break;
                case 14: arcade++; break;
                case 17: arena++; break;
                case 21: cac++; break;
                case 20: uhc++; break;
                default: unknown++; break;
            }
        }
        //Check if present then parse
        StringBuilder bu = new StringBuilder();
        if (quake != 0){
            bu.append("QuakeCraft: ").append(quake).append("\n");
        }
        if (walls != 0){
            bu.append("Walls: ").append(walls).append("\n");
        }
        if (pb != 0){
            bu.append("Paintball: ").append(pb).append("\n");
        }
        if (bsg != 0){
            bu.append("Blitz Survival Games: ").append(bsg).append("\n");
        }
        if (tnt != 0){
            bu.append("TNTGames: ").append(tnt).append("\n");
        }
        if (vz != 0){
            bu.append("VampireZ: ").append(vz).append("\n");
        }
        if (mw != 0){
            bu.append("MegaWalls: ").append(mw).append("\n");
        }
        if (arcade != 0){
            bu.append("Arcade: ").append(arcade).append("\n");
        }
        if (arena != 0){
            bu.append("Arena: ").append(arena).append("\n");
        }
        if (cac != 0){
            bu.append("Cops and Crims: ").append(cac).append("\n");
        }
        if (uhc != 0){
            bu.append("Ultra HardCore Champions: ").append(uhc).append("\n");
        }
        if (unknown != 0){
            bu.append("Unknown Game: ").append(unknown).append("\n");
            bu.append("(Please Contact Dev of this)");
        }
        return bu.toString();
    }
}
