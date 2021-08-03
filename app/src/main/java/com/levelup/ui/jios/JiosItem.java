package com.levelup.ui.jios;

import com.levelup.occasion.Occasion;

import java.util.Date;


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

    /**
     * Constructor for the JiosItem class
     *
     * @param dateInfo date the Jio is taking place on
     * @param timeInfo Time when the Jio starts
     * @param hourOfDay Hour of the day related to time
     * @param minute Minute information related to time
     * @param locationInfo Location where the Jio will take place
     * @param title Title of the Jio
     * @param description Description of the Jio
     */
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

    /**
     * Overloaded constructor for the JiosItem class to push JioId and CreatorID to Firebase
     *
     * @param numLikes Number of likes for this Jio
     * @param jioID Identifying string for this Jio
     * @param creatorID Identifying string for the user who created the Jio
     * @param dateInfo Date on which the Jio will take place
     * @param timeInfo Time on which the Jio will start
     * @param hourOfDay Hour information related to time
     * @param minute Minute information related to time
     * @param locationInfo Location where the Jio will take place
     * @param title Title of the Jio
     * @param description Description of the Jio
     * @param category Category to which the Jio belongs
     */
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
