package com.example.LevelUp.ui;

import java.util.Date;

public interface Occasion {
    String getTitle();

    String getDescription();

    Date getDateInfo();

    String getLocationInfo();

    int getHourOfDay();

    int getMinute();

    String getTimeInfo();

    String getCreatorID();

    String getOccasionID();

    boolean isJio();

    int getNumLikes();

    void setNumLikes(int numLikes);

    int getCategory();
}
