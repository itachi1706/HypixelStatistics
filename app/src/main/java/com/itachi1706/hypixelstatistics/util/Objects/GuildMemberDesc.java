package com.itachi1706.hypixelstatistics.util.Objects;

/**
 * Created by Kenneth on 20/12/2014, 5:43 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
@SuppressWarnings("unused")
public class GuildMemberDesc {

    private String _mcName, _mcNameWithRank, _name, _rank, _dailyCoins = null;
    private String _uuid;
    private long _joined;
    private boolean _done = false;
    //private Drawable mcHead;

    public GuildMemberDesc(String uuid, String rank, long dateJoined){
        _uuid = uuid;
        _rank = rank;
        _joined = dateJoined;
    }

    public GuildMemberDesc(String uuid, String name, String rank, long dateJoined){
        _uuid = uuid;
        _name = name;
        _rank = rank;
        _joined = dateJoined;
    }

    public String get_mcName() {
        return _mcName;
    }

    public void set_mcName(String _mcName) {
        this._mcName = _mcName;
    }

    public String get_mcNameWithRank() {
        return _mcNameWithRank;
    }

    public void set_mcNameWithRank(String _mcNameWithRank) {
        this._mcNameWithRank = _mcNameWithRank;
    }

    @Deprecated
    public String get_name() {
        return _name;
    }

    public String get_rank() {
        return _rank;
    }

    public long get_joined() {
        return _joined;
    }

    public boolean is_done() {
        return _done;
    }

    public void set_done(boolean _done) {
        this._done = _done;
    }

    public String get_dailyCoins() {
        return _dailyCoins;
    }

    public void set_dailyCoins(String _dailyCoins) {
        this._dailyCoins = _dailyCoins;
    }

    public String get_uuid() {
        return _uuid;
    }

    public void set_uuid(String _uuid) {
        this._uuid = _uuid;
    }
}
