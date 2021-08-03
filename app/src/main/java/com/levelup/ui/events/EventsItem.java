package com.levelup.ui.events;

import com.levelup.occasion.Occasion;

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

    /**
     * Constructor for the EventsItem class
     *
     * @param dateInfo date the Event is taking place on
     * @param timeInfo Time when the Event starts
     * @param hourOfDay Hour of the day related to time
     * @param minute Minute information related to time
     * @param locationInfo Location where the Event will take place
     * @param title Title of the Event
     * @param description Description of the Event
     */
    public EventsItem(Date dateInfo, String timeInfo, int hourOfDay, int minute,
                      String locationInfo, String title, String description) {
        this.dateInfo = dateInfo;
        this.timeInfo = timeInfo;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.locationInfo = locationInfo;
        this.title = title;
        this.description = description;
    }

    /**
     * Overloaded constructor for the EventsItem class to push JioId and CreatorID to Firebase
     *
     * @param numLikes Number of likes for this Event
     * @param eventID Identifying string for this Event
     * @param creatorID Identifying string for the user who created the Event
     * @param dateInfo Date on which the Event will take place
     * @param timeInfo Time on which the Event will start
     * @param hourOfDay Hour information related to time
     * @param minute Minute information related to time
     * @param locationInfo Location where the Event will take place
     * @param title Title of the Event
     * @param description Description of the Event
     * @param category Category to which the Event belongs
     */
    public EventsItem(int numLikes, String eventID, String creatorID, Date dateInfo,
                      String timeInfo, int hourOfDay, int minute, String locationInfo, String title,
                      String description, int category) {
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

    public String getEventID() {
        return eventID;
    }

    public String getOccasionID() {
        return eventID;
    }

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
