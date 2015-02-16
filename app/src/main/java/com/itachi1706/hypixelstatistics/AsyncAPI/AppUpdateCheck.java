package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.util.MainStaticVars;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Kenneth on 16/2/2015, 6:59 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class AppUpdateCheck extends AsyncTask<Void, Void, String> {

    Activity mActivity;
    Exception except = null;
    SharedPreferences sp;
    ArrayList<String> changelogStrings = new ArrayList<>();

    public AppUpdateCheck(Activity activity, SharedPreferences sharedPrefs){
        mActivity = activity;
        sp = sharedPrefs;
    }


    @Override
    protected String doInBackground(Void... params) {
        String url = "http://www.itachi1706.com/android/hypstatistics.html";
        String tmp = "";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            InputStream in = response.getEntity().getContent();
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
        sp.edit().putString("version-changelog", changelog).apply();
        String currentVersionNumber = changelogStrings.get(0);
        String currentAppVersion = "";
        PackageInfo pInfo;
        try {
            pInfo = mActivity.getApplicationContext().getPackageManager().getPackageInfo(mActivity.getApplicationContext().getPackageName(), 0);
            currentAppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("VERSION-SERVER", "Version on Server: " + currentVersionNumber);
        Log.d("VERSION-LOCAL", "Current VersionL " + currentAppVersion);
        int comparisions = currentVersionNumber.compareTo(currentAppVersion);
        if (comparisions == 1){
            Log.d("UPDATE NEEDED", "An Update is needed");
            //Outdated Version. Prompt Update
            String bodyMsg = MainStaticVars.getChangelogStringFromArrayList(changelogStrings);
            String title = "A New Update is Available!";
            new AlertDialog.Builder(mActivity).setTitle(title).setMessage(Html.fromHtml(bodyMsg))
                    .setNegativeButton("Don't Update", null).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/itachi1706/HypixelStatistics/releases/latest"));
                    mActivity.startActivity(intent);
                }
            }).show();
        }
    }
}
