package com;

public class ActivityOccasionItem {
    private String occasionID;
    private String userID;

    public ActivityOccasionItem(String occasionID, String userID) {
        this.occasionID = occasionID;
        this.userID = userID;
    }

    public ActivityOccasionItem() {

    }

    public String getOccasionID() {
        return occasionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setOccasionID(String occasionID) {
        this.occasionID = occasionID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
