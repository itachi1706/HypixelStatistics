package com.itachi1706.hypixelstatistics.util;

import android.graphics.drawable.Drawable;

import net.hypixel.api.util.GameType;

/**
 * Created by Kenneth on 18/11/2014, 9:03 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class BoosterDescription {

    private int _boostRate, _originalTime, _timeRemaining;
    private long _date;
    private GameType _gameType;
    private String _purchaser, _mcName;
    private boolean _done = false;
    private Drawable mcHead;

    public BoosterDescription(int boostRate, long date, int gameType, int timeRemain, int originalTime, String purchaser){
        _boostRate = boostRate;
        _originalTime = originalTime;
        _timeRemaining = timeRemain;
        _date = date;
        _gameType = GameType.fromId(gameType);
        _purchaser = purchaser;
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

    public String get_purchaser() {
        return _purchaser;
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

    public Drawable getMcHead() {
        return mcHead;
    }

    public void setMcHead(Drawable mcHead) {
        this.mcHead = mcHead;
    }

    public boolean checkIfBoosterActive(){
        if (_timeRemaining != _originalTime){
            return true;
        }
        return false;
    }
}
