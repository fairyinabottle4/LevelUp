package com.Jios.LevelUp.ui.jios;

import com.example.LevelUp.ui.Occasion;

import java.util.Date;

public class JiosItem implements Occasion {
    private int profilePicture;
    private String timeInfo;
    private int hourOfDay;
    private int minute;
    private Date dateInfo;
    private String locationInfo;
    private String title;
    private String description;

    public JiosItem(int profilePicture, Date dateInfo,  String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description) {
        this.hourOfDay = hourOfDay;
        this.timeInfo = timeInfo;
        this.minute = minute;
        this.profilePicture = profilePicture;
        this.dateInfo = dateInfo;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
    }

    public JiosItem() {

    }

    public int getProfilePicture() {
        return profilePicture;
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
}