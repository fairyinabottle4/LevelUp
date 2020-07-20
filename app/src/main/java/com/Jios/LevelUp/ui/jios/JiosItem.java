package com.Jios.LevelUp.ui.jios;

import com.example.LevelUp.ui.Occasion;

import java.lang.reflect.Type;
import java.util.Date;

public class JiosItem implements Occasion {
    private String timeInfo;
    private int hourOfDay;
    private int minute;
    private Date dateInfo;
    private String locationInfo;
    private String title;
    private String description;

    private String jioID;
    private String creatorID;

    public JiosItem(Date dateInfo,  String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description) {
        this.hourOfDay = hourOfDay;
        this.timeInfo = timeInfo;
        this.minute = minute;
        this.dateInfo = dateInfo;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
    }

    // Overloaded Constructor to push Jio ID and Creator ID to Firebase
    public JiosItem(String jioID, String creatorID, Date dateInfo,  String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description) {
        this.jioID = jioID;
        this.creatorID = creatorID;
        this.hourOfDay = hourOfDay;
        this.timeInfo = timeInfo;
        this.minute = minute;
        this.dateInfo = dateInfo;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
    }

    public JiosItem() {

    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateInfo() {
        return dateInfo;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public String getTimeInfo() {
        return timeInfo;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getJioID() {
        return jioID;
    }

    public String getOccasionID() {
        return jioID;
    }

    public void setOccasionID(String newID) { this.jioID = newID; }

    // set
}