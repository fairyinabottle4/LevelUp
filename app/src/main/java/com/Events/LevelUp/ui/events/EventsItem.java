package com.Events.LevelUp.ui.events;

import com.example.LevelUp.ui.Occasion;

import java.util.Date;

public class EventsItem implements Occasion {
    private int profilePicture;
    private String timeInfo;
    private int hourOfDay;
    private int minute;
    private Date dateInfo;
    private String locationInfo;
    private String title;
    private String description;

    public EventsItem(int profilePicture, Date dateInfo, String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description) {
        this.profilePicture = profilePicture;
        this.dateInfo = dateInfo;
        this.timeInfo = timeInfo;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;

    }

    public EventsItem() {

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

    public String getTimeInfo() {
        return timeInfo;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }
}
