package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kenneth on 16/2/2015, 6:59 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class AppUpdateCheck extends AsyncTask<Void, Void, String> {

    Activity mActivity;
    Exception except = null;
    SharedPreferences sp;
    ArrayList<String> changelogStrings = new ArrayList<>();
    boolean main = false;

    public AppUpdateCheck(Activity activity, SharedPreferences sharedPrefs){
        mActivity = activity;
        sp = sharedPrefs;
    }

    public AppUpdateCheck(Activity activity, SharedPreferences sharedPrefs, boolean isMain){
        mActivity = activity;
        sp = sharedPrefs;
        main = isMain;
    }


    @Override
    protected String doInBackground(Void... params) {
        String url = "http://www.itachi1706.com/android/hypstatistics.html";
        String tmp = "";
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setConnectTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line;
            changelogStrings.clear();
            while((line = reader.readLine()) != null)
            {
                str.append(line).append("\n");
                changelogStrings.add(line);
            }
            in.close();
            tmp = str.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    protected void onPostExecute(String changelog){
        if (except != null){
            Toast.makeText(mActivity.getApplicationContext(), "Unable to contact update server to check for updates", Toast.LENGTH_SHORT).show();
            return;
        }
         /* Legend of Stuff
        1st Line - Current Version Number
        2nd Line - Link to New Version
        # - Changelog Version Number (Bold this)
        * - Points
         */
        if (changelogStrings.size() <= 0){
            Toast.makeText(mActivity.getApplicationContext(), "Unable to do app update check", Toast.LENGTH_SHORT).show();
            return;
        }
        sp.edit().putString("version-changelog", changelog).apply();
        String currentVersionNumber = changelogStrings.get(0);
        String currentAppVersion = "";
        final String newVersionURL = changelogStrings.get(1);
        PackageInfo pInfo;
        try {
            pInfo = mActivity.getApplicationContext().getPackageManager().getPackageInfo(mActivity.getApplicationContext().getPackageName(), 0);
            currentAppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("VERSION-SERVER", "Version on Server: " + currentVersionNumber);
        Log.d("VERSION-LOCAL", "Current Version: " + currentAppVersion);
        int comparisions = currentVersionNumber.compareTo(currentAppVersion);
        if (comparisions == 1){
            Log.d("UPDATE NEEDED", "An Update is needed");
            //Outdated Version. Prompt Update
            String bodyMsg = MainStaticVars.getChangelogStringFromArrayList(changelogStrings);
            String title = "A New Update is Available!";
            if (!mActivity.isFinishing()) {
                new AlertDialog.Builder(mActivity).setTitle(title).setMessage(Html.fromHtml(bodyMsg))
                        .setNegativeButton("Don't Update", null).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Old Method
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVersionURL));
                        mActivity.startActivity(intent); */
                        NotificationManager manager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mActivity);
                        mBuilder.setContentTitle("Downloading new update").setContentText("Downloading new update...")
                                .setProgress(0,0,true).setSmallIcon(R.drawable.ic_launcher).setAutoCancel(false)
                        .setOngoing(true).setTicker("Downloading new update to the app");
                        Random random = new Random();
                        int notificationId = random.nextInt();
                        manager.notify(notificationId, mBuilder.build());
                        new DownloadAndInstallApp(mActivity, mBuilder, manager, notificationId).execute(newVersionURL);
                    }
                }).show();
            }
            return;
        }
        if (!main){
            Log.d("UPDATE CHECK", "No Update Needed");
            if (!mActivity.isFinishing()) {
                new AlertDialog.Builder(mActivity).setTitle("Check for New Update").setMessage("You are on the latest release! No update is required.")
                        .setNegativeButton("Close", null).show();
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "No update is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
