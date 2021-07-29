package com.Jios.LevelUp.ui.jios;

import java.util.Date;

import com.example.LevelUp.ui.Occasion;

public class JiosItem implements Occasion {
    private String timeInfo;
    private int hourOfDay;
    private int minute;
    private Date dateInfo;
    private String locationInfo;
    private String title;
    private String description;
    private int category;

    private String jioID;
    private String creatorID;
    private int numLikes;

    public JiosItem(Date dateInfo, String timeInfo, int hourOfDay, int minute,
                    String locationInfo, String title, String description) {
        this.hourOfDay = hourOfDay;
        this.timeInfo = timeInfo;
        this.minute = minute;
        this.dateInfo = dateInfo;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
    }

    // Overloaded Constructor to push Jio ID and Creator ID to Firebase
    public JiosItem(int numLikes, String jioID, String creatorID, Date dateInfo, String timeInfo,
                    int hourOfDay, int minute, String locationInfo, String title,
                    String description, int category) {
        this.numLikes = numLikes;
        this.jioID = jioID;
        this.creatorID = creatorID;
        this.hourOfDay = hourOfDay;
        this.timeInfo = timeInfo;
        this.minute = minute;
        this.dateInfo = dateInfo;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
        this.category = category;
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

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getCategory() {
        return category;
    }

    @Override
    public boolean isJio() {
        return true;
    }

}
