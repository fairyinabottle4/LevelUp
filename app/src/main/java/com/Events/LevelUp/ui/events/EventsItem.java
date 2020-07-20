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

    private String eventID;
    private String creatorID;


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
    public EventsItem(String eventID, String creatorID, Date dateInfo, String timeInfo, int hourOfDay, int minute, String locationInfo, String title, String description) {
        this.eventID = eventID;
        this.creatorID = creatorID;
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

    /*
    public EventsItem(Parcel in) {
        profilePicture = in.readInt();
        timeInfo = in.readString();
        hourOfDay = in.readInt();
        minute = in.readInt();
        locationInfo = in.readString();
        title = in.readString();
        description = in.readString();
    }



    public static final Creator<EventsItem> CREATOR = new Creator<EventsItem>() {
        @Override
        public EventsItem createFromParcel(Parcel in) {
            return new EventsItem(in);
        }

        @Override
        public EventsItem[] newArray(int size) {
            return new EventsItem[size];
        }
    };

     */

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



    /*
    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(profilePicture);
        dest.writeInt(hourOfDay);
        dest.writeInt(minute);
        dest.writeString(timeInfo);
        dest.writeString(description);
        dest.writeString(locationInfo);
        dest.writeString(title);
        dest.writeString(DateFormat.getInstance().format(dateInfo));
    }

     */





}
