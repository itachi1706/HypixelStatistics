package com.itachi1706.hypixelstatistics.Objects;

import java.io.File;

/**
 * Created by Kenneth on 13/5/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.Objects
 */
public class ExceptionObject {

    private String title, description;
    private Long timeStampInSeconds, timeStampInMillis;
    private File file;
    int count;

    public ExceptionObject(File fileObject, String exceptionTitle, String exceptionDescription, long timeStamp, int count){
        this.title = exceptionTitle;
        this.description = exceptionDescription;
        this.file = fileObject;
        this.timeStampInSeconds = timeStamp;
        this.timeStampInMillis = this.timeStampInSeconds * 1000;
        this.count = count;
    }

    public ExceptionObject(File fileObject, String exceptionDescription, long timeStamp, int count){
        this.title = "Exception";
        this.description = exceptionDescription;
        this.file = fileObject;
        this.timeStampInSeconds = timeStamp;
        this.timeStampInMillis = this.timeStampInSeconds * 1000;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Long getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public File getFile() {
        return file;
    }

    public int getCount() {return count;}
}
