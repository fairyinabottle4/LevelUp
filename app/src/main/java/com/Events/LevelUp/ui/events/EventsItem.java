package com.Events.LevelUp.ui.events;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.LevelUp.ui.Occasion;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

public class EventsItem implements Occasion, Parcelable {
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

}
