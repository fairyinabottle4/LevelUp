package com.Events.LevelUp.ui.events;

import com.example.LevelUp.ui.Occasion;

import java.util.Date;

public class EventsItem implements Occasion {
    private String timeInfo;
    private int hourOfDay;
    private int minute;
    private Date dateInfo;
    private String locationInfo;
    private String title;
    private String description;
    private int category;

    private String eventID;
    private String creatorID;
    private int numLikes;


    public EventsItem(Date dateInfo, String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description) {
        this.dateInfo = dateInfo;
        this.timeInfo = timeInfo;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
    }

    // Overloaded Constructor to push Event ID and Creator ID to Firebase
    public EventsItem(int numLikes, String eventID, String creatorID, Date dateInfo, String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description, int category) {
        this.numLikes = numLikes;
        this.eventID = eventID;
        this.creatorID = creatorID;
        this.dateInfo = dateInfo;
        this.timeInfo = timeInfo;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public EventsItem() {

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

    public String getEventID() { return eventID; }

    public String getOccasionID() {return eventID; }

    public void setOccasionID(String newID) { this.eventID = newID; }

    public String getCreatorID() {
        return creatorID;
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
        return false;
    }
}
