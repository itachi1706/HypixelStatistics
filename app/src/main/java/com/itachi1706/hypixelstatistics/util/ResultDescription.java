package com.itachi1706.hypixelstatistics.util;

import android.content.Intent;

/**
 * Created by Kenneth on 13/11/2014, 9:11 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class ResultDescription {

    private String _title, _result;
    private boolean _hasDescription;

    public ResultDescription(String title, String result){
        this._title = title;
        this._result = result;
        this._hasDescription = true;
    }

    public ResultDescription(String title, String result, boolean description){
        this._title = title;
        this._result = result;
        this._hasDescription = description;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_result() {
        return _result;
    }

    public void set_result(String _result) {
        this._result = _result;
    }

    public boolean is_hasDescription() {
        return _hasDescription;
    }

    public void set_hasDescription(boolean _hasDescription) {
        this._hasDescription = _hasDescription;
    }
}
