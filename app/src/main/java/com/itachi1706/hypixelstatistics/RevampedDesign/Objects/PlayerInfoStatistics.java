package com.itachi1706.hypixelstatistics.RevampedDesign.Objects;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.Objects
 */
public class PlayerInfoStatistics extends PlayerInfoBase {

    private String title, message;
    private String action;

    public PlayerInfoStatistics(){super();}

    public PlayerInfoStatistics(String title, String message) {
        super();
        this.title = title;
        this.message = message;
    }

    public PlayerInfoStatistics(String title, String message, String action) {
        super();
        this.title = title;
        this.message = message;
        this.action = action;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean hasAction() {
        return action != null;
    }
}
