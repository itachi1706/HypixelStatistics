package com.itachi1706.hypixelstatistics.util;

/**
 * Created by Kenneth on 13/11/2014, 9:11 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class ResultDescription {

    private String _title, _result;
    private boolean _hasDescription, _subTitle;

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

    public ResultDescription(String title, String result, boolean description, boolean subTitle){
        this._title = title;
        this._result = result;
        this._hasDescription = description;
        this._subTitle = subTitle;
    }

    public String get_title() {
        return _title;
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

    public boolean is_subTitle() {
        return _subTitle;
    }

}
