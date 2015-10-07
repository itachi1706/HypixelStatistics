package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

public class PlayerInfoActivity extends AppCompatActivity {

    //CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Theme
        MainStaticVars.setLayoutAccordingToPrefs(this);

        setContentView(R.layout.activity_player_info);
    }

}
