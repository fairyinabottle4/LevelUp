package com.example.LevelUp.ui;

import androidx.fragment.app.Fragment;

import java.util.Date;

public interface Occasion {
    int getProfilePicture();
    String getTitle();
    String getDescription();
    Date getDateInfo();
    String getLocationInfo();
    int getHourOfDay();
    int getMinute();
    String getTimeInfo();
}
