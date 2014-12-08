package com.itachi1706.hypixelstatistics;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.AsyncAPI.GetKeyInfo;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import java.util.UUID;


public class KeyInfoActivity extends ActionBarActivity {

    TextView resultV, debugV, ownerV, keyV, queryV;
    Button btnCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_info);

        btnCheck = (Button) findViewById(R.id.btnCheck);
        debugV = (TextView) findViewById(R.id.KeytvDebug);
        ownerV = (TextView) findViewById(R.id.KeytvOwner);
        keyV = (TextView) findViewById(R.id.KeytvAPI);
        queryV = (TextView) findViewById(R.id.KeytvQuery);
        resultV = (TextView) findViewById(R.id.lblResults);

        //Check if we should hide the debug window
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(myPref.getBoolean("debugMode", true))){
            debugV.setVisibility(View.INVISIBLE);
        } else {
            debugV.setVisibility(View.VISIBLE);
        }

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = MainStaticVars.apikey;
                UUID uid = UUID.fromString(key);

                new GetKeyInfo(keyV, ownerV, queryV, resultV, debugV, getApplicationContext()).execute(uid);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Check if we should hide the debug window
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(myPref.getBoolean("debugMode", true))){
            debugV.setVisibility(View.INVISIBLE);
        } else {
            debugV.setVisibility(View.VISIBLE);
        }
    }
}
