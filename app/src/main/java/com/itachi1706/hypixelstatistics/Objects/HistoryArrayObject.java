package com.itachi1706.hypixelstatistics.Objects;

/**
 * Created by Kenneth on 21/9/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.Objects
 */
public class HistoryArrayObject {

    private String rank;
    private String packageRank;
    private String uuid;
    private String displayname;
    private String playername;
    private String newPackageRank;
    private long dateObtained;
    private String prefix;

    public HistoryArrayObject(){}

    public HistoryArrayObject(String rank, String packageRank, String uuid, String displayname, String playername, String newPackageRank, long dateObtained, String prefix) {
        this.rank = rank;
        this.packageRank = packageRank;
        this.uuid = uuid;
        this.displayname = displayname;
        this.playername = playername;
        this.newPackageRank = newPackageRank;
        this.dateObtained = dateObtained;
        this.prefix = prefix;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public boolean hasRank(){
        return rank != null;
    }

    public String getPackageRank() {
        return packageRank;
    }

    public void setPackageRank(String packageRank) {
        this.packageRank = packageRank;
    }

    public boolean hasPackageRank(){
        return packageRank != null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean hasUuid(){
        return uuid != null;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public long getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(long dateObtained) {
        this.dateObtained = dateObtained;
    }

    public boolean hasDateObtained() {
        return dateObtained != 0;
    }

    public String getNewPackageRank() {
        return newPackageRank;
    }

    public void setNewPackageRank(String newPackageRank) {
        this.newPackageRank = newPackageRank;
    }

    public boolean hasNewPackageRank(){
        return newPackageRank != null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean hasPrefix(){ return prefix != null; }

    @Override
    public String toString() {
        return "HistoryArrayObject{" +
                "rank=" + rank + ", packagerank=" +
                packageRank + ", displayname=" + displayname +
                ", playername=" + playername + ", uuid=" +
                uuid + ", date=" + dateObtained + ", prefix=" + prefix + ", newPackageRank=" + newPackageRank + "}";
    }

}
