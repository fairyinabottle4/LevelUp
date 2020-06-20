package com.Mktplace.LevelUp.ui.mktplace;

public class MktplaceItem {
    private String mImageUrl;
    private String mName;
    private String mLocation;
    private String mDescription;

    public MktplaceItem(String name, String imageUrl, String location, String description) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.mName = name;
        this.mImageUrl = imageUrl;
        this.mLocation = location;
        this.mDescription = description;
    }

    public MktplaceItem() {
        //empty constructor needed
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getDescription() {
        return mDescription;
    }
}