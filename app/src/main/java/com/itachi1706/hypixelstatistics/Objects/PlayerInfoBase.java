package com.itachi1706.hypixelstatistics.Objects;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.Objects
 */
public class PlayerInfoBase {

    private boolean isExpanded = false;

    public PlayerInfoBase(){}

    public PlayerInfoBase(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}
