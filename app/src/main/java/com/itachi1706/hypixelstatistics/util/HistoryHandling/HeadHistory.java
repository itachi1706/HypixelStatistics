package com.itachi1706.hypixelstatistics.util.HistoryHandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
