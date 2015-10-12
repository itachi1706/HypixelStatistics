package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MiddleActivityBetweenSingleTopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String className = getIntent().hasExtra("class") ? getIntent().getStringExtra("class").toLowerCase() : "-";

        Intent newIntent;

        switch (className){
            case "playerinfo": newIntent = new Intent(this, PlayerInfoActivity.class); break;
            default: newIntent = new Intent(this, PlayerInfoActivity.class); break;
        }

        if (getIntent().hasExtra("playerUuid")) newIntent.putExtra("playerUuid", getIntent().getStringExtra("playerUuid"));
        if (getIntent().hasExtra("player")) newIntent.putExtra("player", getIntent().getStringExtra("player"));

        startActivity(newIntent);
        finish();

    }

}
