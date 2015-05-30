package com.itachi1706.hypixelstatistics.util.HistoryHandling;

import android.content.Context;
import android.util.Log;

import com.itachi1706.hypixelstatistics.util.Objects.ExceptionObject;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util.HistoryHandling
 */
public class ExceptionHistory {

    private String filePath;
    private Context context;
    private HashMap<String, ExceptionObject> exceptionStorage = new HashMap<>();

    public ExceptionHistory(Context context){
        filePath = context.getExternalFilesDir(null) + File.separator + "crash-report";
        this.context = context;
    }

    public HashMap<String, ExceptionObject> getAllExceptions(){
        parseFilesToObject();
        return exceptionStorage;
    }

    public boolean deleteException(File file) {
        return file.exists() && file.delete();
    }

    private boolean checkIfFolderExist(File folder){
        return folder.exists() && folder.isDirectory();
    }

    private void parseFilesToObject(){
        String storageLocation = filePath + File.separator;
        File folder = new File(storageLocation);
        if (!checkIfFolderExist(folder))
            return;

        exceptionStorage.clear();
        File[] listOfFiles = folder.listFiles();
        int count = 1;
        for (File file : listOfFiles){
            if (file.getName().equals("index.txt"))
                continue;
            try {
                boolean firstLine = true;
                String title = "Exception #" + count + " ";
                BufferedReader br = new BufferedReader(new FileReader(file));
                String currentLine;
                StringBuilder builder = new StringBuilder();

                while ((currentLine = br.readLine()) != null){
                    if (firstLine){
                        title += currentLine;
                        firstLine = false;
                    }
                    builder.append(currentLine).append("\n");
                }

                //Create new object
                long timeStamp = Long.parseLong(FilenameUtils.removeExtension(file.getName()));
                exceptionStorage.put(FilenameUtils.removeExtension(file.getName()), new ExceptionObject(file, title, builder.toString(), timeStamp, count));
                Log.d("EXCEPT-RETRIVE", "Saved " + FilenameUtils.removeExtension(file.getName()) + " to hashmap for reference");
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
