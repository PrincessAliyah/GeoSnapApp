package org.kashmirworldfoundation.WildlifeGeoSnap;

import com.google.firebase.database.Exclude;
import java.util.Date;

class Takedown {
    private String stationId;
    private String totalPics;
    private String signsInput;
    private String sdNum;
    private String lureType;
    private String camWorks;
    private Date currentTime;

    public Takedown() {

    }
    public Takedown(Date time, String totalPics, String signsInput, String sdNum, String lureType, String camWorks,String stationId){
        this.currentTime = time;
        this.totalPics = totalPics;
        this.signsInput = signsInput;
        this.sdNum = sdNum;
        this.lureType = lureType;
        this.camWorks = camWorks;
        this.stationId=stationId;
    }

    @Exclude
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Date getCurrentTime(){
        return currentTime;
    }

    public String getTotalPics(){ return totalPics;}
    public String getSignsInput(){ return signsInput;}
    public String getSdNum(){ return sdNum;}
    public String getLureType() { return lureType;}
    public String getCamWorks() { return camWorks; }
}
