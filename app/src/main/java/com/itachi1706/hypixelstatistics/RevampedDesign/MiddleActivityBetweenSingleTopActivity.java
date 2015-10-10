package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MiddleActivityBetweenSingleTopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newIntent = new Intent(this, PlayerInfoActivity.class);
        if (getIntent().hasExtra("playerUuid")) newIntent.putExtra("playerUuid", getIntent().getStringExtra("playerUuid"));

        startActivity(newIntent);
        finish();

    }

}
