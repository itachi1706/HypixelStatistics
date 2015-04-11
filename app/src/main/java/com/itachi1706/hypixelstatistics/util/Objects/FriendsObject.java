package com.itachi1706.hypixelstatistics.util.Objects;

/**
 * Created by Kenneth on 11/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.util.Objects
 */
@SuppressWarnings("unused")
public class FriendsObject {

    private long date;
    private String _mcName, _mcNameWithRank, friendUUID;
    private boolean _done;

    public FriendsObject(long date, String friendUUID){
        this.date = date;
        this.friendUUID = friendUUID;
    }

    public String getFriendUUID() {
        return friendUUID;
    }

    public void setFriendUUID(String friendUUID) {
        this.friendUUID = friendUUID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String get_mcNameWithRank() {
        return _mcNameWithRank;
    }

    public void set_mcNameWithRank(String _mcNameWithRank) {
        this._mcNameWithRank = _mcNameWithRank;
    }

    public String get_mcName() {
        return _mcName;
    }

    public void set_mcName(String _mcName) {
        this._mcName = _mcName;
    }

    public boolean is_done() {
        return _done;
    }

    public void set_done(boolean _done) {
        this._done = _done;
    }
}
