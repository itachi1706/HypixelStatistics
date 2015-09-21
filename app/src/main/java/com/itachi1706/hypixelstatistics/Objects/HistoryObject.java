package com.itachi1706.hypixelstatistics.Objects;

/**
 * Created by Kenneth on 19/11/2014, 8:52 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class HistoryObject {

    private HistoryArrayObject[] history;

    public HistoryObject(HistoryArrayObject[] history) {
        this.history = history;
    }

    public HistoryObject(){}

    public HistoryArrayObject[] getHistory() {
        return history;
    }

    public void setHistory(HistoryArrayObject[] history) {
        this.history = history;
    }

    public boolean hasHistory() {
        return history != null && history.length != 0;
    }

    @Override
    public String toString(){
        String concat = "HistoryObject{[";
        for (HistoryArrayObject o : history){
            concat += o.toString();
        }
        concat += "]}";
        return concat;
    }
}
