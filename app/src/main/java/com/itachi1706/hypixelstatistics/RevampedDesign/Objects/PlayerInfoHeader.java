package com.itachi1706.hypixelstatistics.RevampedDesign.Objects;

import java.util.List;

/**
 * Created by Kenneth on 9/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.Objects
 */
public class PlayerInfoHeader extends PlayerInfoBase {

    private String title;
    private List<PlayerInfoStatistics> child;

    public PlayerInfoHeader(){
        super();
    }

    public PlayerInfoHeader(String title){
        super();
        this.title = title;
    }

    public PlayerInfoHeader(String title, List<PlayerInfoStatistics> child){
        super();
        this.title = title;
        this.child = child;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PlayerInfoStatistics> getChild() {
        return child;
    }

    public void setChild(List<PlayerInfoStatistics> child) {
        this.child = child;
    }

    public boolean hasChild(){
        return this.child != null && this.child.size() != 0;
    }
}
