package com.itachi1706.hypixelstatistics.util.HistoryHandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Kenneth on 07/12/2014, 8:53 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class HeadHistory {
    //Storage Location: /sdcard/Android/data/com.itachi1706.hypixelstatistics/heads

    private static boolean checkFolderExists(Context context){
        File folder = new File(context.getExternalFilesDir(null) + File.separator + "heads");
        return folder.exists() || folder.mkdir();
    }

    public static boolean saveHead(Context context, Drawable imageToSave, String playerName){
        //Prepare image and storage location
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "heads" + File.separator;
        BitmapDrawable bd = (BitmapDrawable) imageToSave;
        Bitmap bm = bd.getBitmap();
        File image = new File(storageLocation, playerName + ".png");
        FileOutputStream out;
        if (!checkFolderExists(context)){
            NotifyUserUtil.createShortToast(context, "An error occurred making folder to store head data");
            return false;
        }
        try {
            Log.d("FILE PATH",image.getAbsolutePath());
            if (!image.createNewFile()){
                return false;
            }
            out = new FileOutputStream(image);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.d("HEAD RETRIEVAL", "Cached " + playerName + "'s Head onto device");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkIfHeadExists(Context context, String playerName){
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "heads" + File.separator;
        File file = new File(storageLocation, playerName + ".png");
        final long expiryDay = 864000000;
        if (file.exists()){
            //Check if expired (10 days)
            if (System.currentTimeMillis() - file.lastModified() > expiryDay){
                boolean result = file.delete();
                if (result)
                    return false;
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean updateHead(Context context, String playerName){
        if (!checkIfHeadExists(context, playerName)){
            return false;
        }
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "heads" + File.separator;
        File file = new File(storageLocation, playerName + ".png");
        return file.exists() && file.delete();
    }

    public static Drawable getHead(Context context, String playerName){
        if (!checkIfHeadExists(context, playerName)){
            return null;
        }
        String storageLocation = context.getExternalFilesDir(null) + File.separator + "heads" + File.separator + playerName + ".png";
        return new BitmapDrawable(context.getResources(), storageLocation);
    }

    public static void removeExpiredHeads(Context context, List<HistoryArrayObject> existingList){
        if (!checkFolderExists(context)) return;
        String folderLocation = context.getExternalFilesDir(null) + File.separator + "heads";
        File folder = new File(folderLocation);
        File[] heads = folder.listFiles();

        if (existingList == null || existingList.size() == 0) {
            //Delete all heads in folder
            for (File head : heads){
                String playerName = head.getName().split("\\.")[0];
                if (deleteHead(context, playerName))
                    Log.i("HEAD-EXPIRY", playerName + "'s head expired and has been deleted");
                else
                    Log.e("HEAD-EXPIRY", "Unable to delete head");
            }
            return;
        }

        for (File head : heads){
            String playerName = head.getName().split("\\.")[0];

            boolean toDelete = true;
            for (HistoryArrayObject object : existingList){
                if (object.getPlayername().equals(playerName)){
                    toDelete = false;
                    break;
                }
            }

            if (toDelete) {
                if (deleteHead(context, playerName))
                    Log.i("HEAD-EXPIRY", playerName + "'s head expired and has been deleted");
                else
                    Log.e("HEAD-EXPIRY", "Unable to delete head");
            }
        }

    }

    public static boolean deleteHead(Context context, String playerName) {
        if (!checkIfHeadExists(context, playerName)) return true;

        String storageLocation = context.getExternalFilesDir(null) + File.separator + "heads" + File.separator;
        File file = new File(storageLocation, playerName + ".png");
        return !file.exists() || file.delete();
    }
}
