package com;

public class ReviewItem {

    private String userID;
    private String reviewerdisplayName;
    private String date;
    private String review;

    /**
     * Constructor for the ReviewItem class
     *
     * @param userID ID of the user being reviewed
     * @param reviewerdisplayName Name of the user giving the review
     * @param date Date of the review
     * @param review Content of the review
     */
    public ReviewItem(String userID, String reviewerdisplayName, String date, String review) {
        this.userID = userID;
        this.reviewerdisplayName = reviewerdisplayName;
        this.date = date;
        this.review = review;
    }

    public ReviewItem() {

    }

    public String getUserID() {
        return userID;
    }
    public String getReviewerdisplayName() {
        return reviewerdisplayName;
    }
    public String getDate() {
        return date;
    }
    public String getReview() {
        return review;
    }
}
