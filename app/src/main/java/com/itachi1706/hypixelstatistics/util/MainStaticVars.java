package com.itachi1706.hypixelstatistics.util;

import java.util.ArrayList;

/**
 * Created by Kenneth on 18/11/2014, 9:12 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class MainStaticVars {
    public static final String API_BASE_URL = "https://api.hypixel.net/";
    public static ArrayList<BoosterDescription> boosterList = new ArrayList<>();
    public static boolean boosterUpdated = false, inProg = false, parseRes = false;
    public static int numOfBoosters = 0, tmpBooster = 0;
}
