package com.itachi1706.hypixelstatistics.Objects;

import android.graphics.drawable.Drawable;

import net.hypixel.api.util.GameType;

/**
 * Created by Kenneth on 18/11/2014, 9:03 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
@SuppressWarnings("unused")
public class BoosterDescription {

    private String _purchaser;
    private int _boostRate, _originalTime, _timeRemaining;
    private long _date;
    private GameType _gameType;
    private String _mcName, _mcNameWithRank;
    private String _purchaseruuid;
    private boolean _done = false;
    private Drawable mcHead;

    public BoosterDescription(int boostRate, long date, int gameType, int timeRemain, int originalTime, String uuid, String purchaser){
        _boostRate = boostRate;
        _originalTime = originalTime;
        _timeRemaining = timeRemain;
        _date = date;
        _gameType = GameType.fromId(gameType);
        _purchaser = purchaser;
        _purchaseruuid = uuid;
    }

    public BoosterDescription(int boostRate, long date, int gameType, int timeRemain, int originalTime, String uuid){
        _boostRate = boostRate;
        _originalTime = originalTime;
        _timeRemaining = timeRemain;
        _date = date;
        _gameType = GameType.fromId(gameType);
        _purchaseruuid = uuid;
    }

    public BoosterDescription(String gamemode, int totalDuration, int count){
        this._mcName = gamemode;
        this._timeRemaining = totalDuration;
        this._boostRate = count;
    }

    public int get_boostRate() {
        return _boostRate;
    }

    public int get_originalTime() {
        return _originalTime;
    }

    public int get_timeRemaining() {
        return _timeRemaining;
    }

    public long get_date() {
        return _date;
    }

    public GameType get_gameType() {
        return _gameType;
    }

    @Deprecated
    public String get_purchaser() {
        return _purchaser;
    }

    public String get_mcName() {
        return _mcName;
    }

    public void set_mcName(String _mcName) {
        this._mcName = _mcName;
    }

    public void set_timeRemaining(int timeRemaining) { this._timeRemaining = timeRemaining; }

    public boolean is_done() {
        return _done;
    }

    public void set_done(boolean _done) {
        this._done = _done;
    }

    public Drawable getMcHead() {
        return mcHead;
    }

    public void setMcHead(Drawable mcHead) {
        this.mcHead = mcHead;
    }

    public boolean checkIfBoosterActive(){
        return _timeRemaining != _originalTime;
    }

    public String get_mcNameWithRank() {
        return _mcNameWithRank;
    }

    public void set_mcNameWithRank(String _mcNameWithRank) {
        this._mcNameWithRank = _mcNameWithRank;
    }

    public String get_purchaseruuid() {
        return _purchaseruuid;
    }

    public void set_purchaseruuid(String _purchaseruuid) {
        this._purchaseruuid = _purchaseruuid;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public BoosterDescription clone(){
        BoosterDescription d;
        if (_gameType == null)
            d = new BoosterDescription(_mcName, _timeRemaining, _boostRate);
        else
            d = new BoosterDescription(_boostRate, _date, _gameType.getId(), _timeRemaining, _originalTime, _purchaseruuid);
        if (mcHead != null)
            d.setMcHead(mcHead);
        d.set_done(_done);
        if (_mcName != null)
            d.set_mcName(_mcName);
        if (_mcNameWithRank != null)
            d.set_mcNameWithRank(_mcNameWithRank);
        return d;
    }
}
