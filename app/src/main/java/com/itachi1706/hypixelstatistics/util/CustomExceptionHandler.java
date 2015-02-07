package com.itachi1706.hypixelstatistics.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 6/2/2015, 10:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String localPath;

    private Context mContext;

    private String url;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    @SuppressWarnings("unused")
    public CustomExceptionHandler(String localPath, Context context, String url) {
        this.localPath = localPath;
        this.url = url;
        this.mContext = context;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public CustomExceptionHandler(String localPath, Context context) {
        this.localPath = localPath;
        this.mContext = context;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        Log.e("CRASH-HANDLER", "Unexpected Exception occured! Executing crash report handler!");
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

        Log.e("CRASH-HANDLER", "Writing to file...");
        if (localPath != null) {
            writeToFile(stacktrace, filename);
            replaceIndex(filename);
        }

        if (url != null) {
            sendToServer(stacktrace, filename);
        }

        defaultUEH.uncaughtException(t, e);
    }

    public void replaceIndex(String filename){
        File f = new File(localPath + "/index.txt");
        if (f.exists())
            if (!f.delete())
                return;
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/index.txt"));
            bos.write(filename);
            bos.flush();
            bos.close();
            Log.e("CRASH-HANDLER", "Updated index!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkCrash(){
        if (localPath != null){
            //Get folder and check if anything is inside
            File folder = new File(localPath);
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles.length == 0)
                return;

            //There is a crash report
            Log.e("CRASH-HANDLER", "Found " + listOfFiles.length + " crash reports!");

            //Get Index
            File indexFile = new File(localPath + File.separator + "index.txt");
            if (!indexFile.exists()) {
                Log.e("CRASH-HANDLER", "WHY INDEX NO EXIST! D:");
                Log.e("CRASH-HANDLER", "Probably no crash reports. whew. :D");
                return;
            }

            String crashReportFileName = "";

            BufferedReader br;
            try {
                String sCurrentLine;
                br = new BufferedReader(new FileReader(indexFile));
                while ((sCurrentLine = br.readLine()) != null) {
                    crashReportFileName = sCurrentLine;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            File crashReport = new File(localPath + File.separator + crashReportFileName);

            String stacktrace = "";

            BufferedReader brs;
            try {
                String sCurrentLine;
                brs = new BufferedReader(new FileReader(indexFile));
                while ((sCurrentLine = brs.readLine()) != null) {
                    stacktrace += sCurrentLine + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            notifyUser(stacktrace, crashReport);
            //Remove index to not call again
            if (!indexFile.delete()){
                Log.e("CRASH-REPORT", "REMOVED INDEX!");
            }
        }
    }

    private void notifyUser(final String stacktrace, File crashReport){
        Log.e("CRASH-HANDLER", "Notifying user of action needed");
        new AlertDialog.Builder(mContext).setTitle("Unexpected Crash!")
                .setMessage("We have found a previous crash report! Crash Report is located at: " + localPath + "/" + crashReport.getName() + "\n\n" +
                        "If you wish, you can also send the stacktrace to the developer to help improve the application!").setPositiveButton("Close", null)
                .setNegativeButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = "itachi1706@outlook.com";
                        String subject = "APP CRASH REPORT - com.itachi1706.hypixelstatistics";
                        String emailUri = "";
                        try {
                            emailUri = "mailto:" + email + "?subject=" + URLEncoder.encode(subject, "UTF-8")
                                    + "&body=" + URLEncoder.encode(stacktrace, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Uri endUri = Uri.parse(emailUri);
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(endUri);

                        mContext.startActivity(Intent.createChooser(intent, "Send Crash Report"));
                    }
                }).show();
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
            Log.e("CRASH-HANDLER", "Written to file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(String stacktrace, String filename) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("filename", filename));
        nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
        try {
            httpPost.setEntity(
                    new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
