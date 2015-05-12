package com.itachi1706.hypixelstatistics.AsyncAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kenneth on 12/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class DownloadAndInstallApp extends AsyncTask<String, Void, Boolean> {

    private Activity activity;
    Exception except = null;
    private Uri link;
    private String filePATH;

    public DownloadAndInstallApp(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(String... updateLink) {
        try {
            link = Uri.parse(updateLink[0]);
            URL url = new URL(updateLink[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.connect();

            filePATH = activity.getApplicationContext().getExternalFilesDir(null) + File.separator + "download" + File.separator;
            File folder = new File(filePATH);
            if (!folder.exists()) {
                if (!tryAndCreateFolder(folder)) {
                    Log.d("Fail", "Cannot Create Folder. Not Downloading");
                    conn.disconnect();
                    return false;
                }
            }
            File file = new File(folder, "app-update.apk");
            FileOutputStream fos = new FileOutputStream(file);

            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();//till here, it works fine - .apk is download to my sdcard in download file
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            except = e;
            return false;
        }
    }

    protected void onPostExecute(boolean passed){
        if (!passed){
            if (except != null){
                //Print Exception
                new AlertDialog.Builder(activity).setTitle("Exception Occured (Download)")
                        .setMessage("An exception occurred while downloading the update file. (" + except.getLocalizedMessage() + ")")
                .setNeutralButton("Manually Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, link);
                        activity.startActivity(intent);
                    }
                }).setPositiveButton("Close", null);
            } else {
                new AlertDialog.Builder(activity).setTitle("Exception Occured (Download)")
                        .setMessage("An exception occurred while downloading the update file. (Cannot Automatically Download)")
                        .setNeutralButton("Manually Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                                activity.startActivity(intent);
                            }
                        }).setPositiveButton("Close", null);
            }
        }

        //Invoke the Package Manager
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePATH + "app-update.apk")), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private boolean tryAndCreateFolder(File folder){
        if (!folder.exists() || !folder.isDirectory()) {
            if (folder.isFile()) {
                //Rename it to something else
                int rename = 0;
                boolean check;
                do {
                    rename++;
                    check = folder.renameTo(new File(filePATH + "_" + rename));
                } while (!check);
                folder = new File(filePATH);
            }
            return folder.mkdir();
        }
        return false;
    }
}
