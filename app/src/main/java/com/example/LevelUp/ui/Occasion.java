package com.example.LevelUp.ui;

import androidx.fragment.app.Fragment;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;
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

}
