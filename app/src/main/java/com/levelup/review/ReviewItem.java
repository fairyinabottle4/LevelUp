package com.levelup.review;

public class ReviewItem {

    private String userID;
    private String reviewerdisplayName;
    private String date;
    private String review;

    public ReviewItem(String userID, String reviewerdisplayName, String date, String review) {
        this.userID = userID;
        this.reviewerdisplayName = reviewerdisplayName;
        this.date = date;
        this.review = review;
    }

    public ReviewItem() {

    }

    public String getUserID() {return  userID;}
    public String getReviewerdisplayName() {return reviewerdisplayName;}
    public String getDate() {return date;}
    public String getReview() {return review;}
}
