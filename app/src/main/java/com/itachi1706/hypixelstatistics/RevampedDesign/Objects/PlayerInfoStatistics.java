package com.itachi1706.hypixelstatistics.RevampedDesign.Objects;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.Objects
 */
public class PlayerInfoStatistics extends PlayerInfoBase {

    private String title, message;

    public PlayerInfoStatistics(){}

    public PlayerInfoStatistics(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
