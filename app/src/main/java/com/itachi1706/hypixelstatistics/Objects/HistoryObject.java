package com.itachi1706.hypixelstatistics.Objects;

import com.google.gson.JsonArray;

/**
 * Created by Kenneth on 19/11/2014, 8:52 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class HistoryObject {

    private JsonArray history;

    public JsonArray getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "HistoryObject{" +
                "history=" + history + "}";
    }
}
