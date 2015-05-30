package com.itachi1706.hypixelstatistics.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Kenneth on 30/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util
 */
public class SnackbarUtil {

    public static void showDismissSnackbar(View currentLayout, String message, int duration){
        Snackbar.make(currentLayout, message, duration)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }
}