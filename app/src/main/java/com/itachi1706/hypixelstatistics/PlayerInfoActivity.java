package com.itachi1706.hypixelstatistics;

import android.media.AudioTrack;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.GetPlayerByName;

import net.hypixel.api.reply.PlayerReply;


public class PlayerInfoActivity extends ActionBarActivity {

    EditText playerName;
    TextView debug, result, generalDetails;
    Button checkPlayer;
    public static String lastGsonObtained = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        playerName = (EditText) findViewById(R.id.PlayeretName);
        debug = (TextView) findViewById(R.id.player_tvDebug);
        result = (TextView) findViewById(R.id.player_lblResult);
        checkPlayer = (Button) findViewById(R.id.PlayerBtnChk);
        playerName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        debug.setMovementMethod(new ScrollingMovementMethod());
        generalDetails = (TextView) findViewById(R.id.player_tvGeneral);
        generalDetails.setMovementMethod(new ScrollingMovementMethod());

        checkPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().equals("") || playerName.getText().toString() == null){
                    Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_SHORT).show();
                } else {
                    String name = playerName.getText().toString();
                    new GetPlayerByName(result, debug, generalDetails, getApplicationContext()).execute(name);
                }
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
