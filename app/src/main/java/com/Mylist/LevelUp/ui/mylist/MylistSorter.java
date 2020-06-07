package com.Mylist.LevelUp.ui.mylist;

import com.Events.LevelUp.ui.events.EventsItem;
import com.Jios.LevelUp.ui.jios.JiosItem;
import com.example.LevelUp.ui.Occasion;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;

import java.util.ArrayList;
import java.util.Comparator;

//this class is not used yet
public class MylistSorter implements Comparator<Occasion> {

    private ArrayList<Occasion> combinedList;
    private ArrayList<JiosItem> jiosList = new JiosFragment().getJiosItemList();
    private ArrayList<EventsItem> eventsList = new EventsFragment().getEventsItemList();

    @Override
    public int compare(Occasion o1, Occasion o2) {
        return 0;
    }

    //there will be a method to combine the lists

    //there will also be a method to sort based on date.

    //this method will be called by MylistFragment
    public ArrayList<Occasion> getCombinedList() {
        return combinedList;
    }
}
