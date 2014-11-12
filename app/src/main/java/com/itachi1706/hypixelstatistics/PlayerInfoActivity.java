package com.itachi1706.hypixelstatistics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioTrack;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.GetPlayerByName;

import net.hypixel.api.reply.PlayerReply;


public class PlayerInfoActivity extends ActionBarActivity {

    EditText playerName;
    TextView debug, result, generalDetails;
    Button checkPlayer;
    public static String lastGsonObtained = "";
    ImageView pHead;
    ProgressDialog checkProgress;
    ProgressBar headBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        playerName = (EditText) findViewById(R.id.PlayeretName);
        debug = (TextView) findViewById(R.id.player_tvDebug);
        result = (TextView) findViewById(R.id.player_lblResult);
        checkPlayer = (Button) findViewById(R.id.PlayerBtnChk);
        playerName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        pHead = (ImageView) findViewById(R.id.playerIvPlayerHead);
        debug.setMovementMethod(new ScrollingMovementMethod());
        generalDetails = (TextView) findViewById(R.id.player_tvGeneral);
        generalDetails.setMovementMethod(new ScrollingMovementMethod());
        headBar = (ProgressBar) findViewById(R.id.PlayerpbHead);

        //Check if we should hide the debug window
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(myPref.getBoolean("debugMode", true))){
            debug.setVisibility(View.INVISIBLE);
        } else {
            debug.setVisibility(View.VISIBLE);
        }

        checkPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().equals("") || playerName.getText().toString() == null){
                    Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_SHORT).show();
                } else {
                    String name = playerName.getText().toString();
                    checkProgress = new ProgressDialog(PlayerInfoActivity.this);
                    checkProgress.setCancelable(false);
                    checkProgress.setIndeterminate(true);
                    checkProgress.setTitle("Querying Server...");
                    checkProgress.setMessage("Getting Player Statistics from the Hypixel API");
                    checkProgress.show();
                    new GetPlayerByName(result, debug, generalDetails, pHead, checkProgress, headBar, getApplicationContext()).execute(name);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Check if we should hide the debug window
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(myPref.getBoolean("debugMode", true))){
            debug.setVisibility(View.INVISIBLE);
        } else {
            debug.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_info, menu);
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
            startActivity(new Intent(PlayerInfoActivity.this, GeneralPrefActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
