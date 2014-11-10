package com.itachi1706.hypixelstatistics;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.GetKeyInfo;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.KeyReply;
import net.hypixel.api.util.Callback;

import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    EditText editKey;
    TextView result;
    Button btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheck = (Button) findViewById(R.id.btnCheck);
        result = (TextView) findViewById(R.id.tvResults);
        editKey = (EditText) findViewById(R.id.etKey);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = getResources().getString(R.string.hypixel_api_key);
                UUID uid = UUID.fromString(key);

                new GetKeyInfo(result, getApplicationContext()).execute(uid);

                //From Hypixel API
                /*
                HypixelAPI.getInstance().setApiKey(UUID.fromString(key));
                HypixelAPI.getInstance().getKeyInfo(new Callback<KeyReply>(KeyReply.class) {
                    @Override
                    public void callback(Throwable failCause, KeyReply result) {
                        if(failCause!=null) {
                            failCause.printStackTrace();
                        } else {
                            MainActivity.this.result.setText(result.toString());
                        }
                        HypixelAPI.getInstance().finish();
                    }
                });*/
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
