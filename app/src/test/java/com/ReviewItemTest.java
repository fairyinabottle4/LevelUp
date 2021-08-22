package com;

import static org.junit.Assert.*;

import org.junit.Test;
import com.ReviewItem;

public class ReviewItemTest {
    final ReviewItem reviewItem = new ReviewItem("testid123", "testerName",
        "2021-09-09", "Fantastic person!");

    @Test
    public void getUserID() {
        String userID = reviewItem.getUserID();
        assertEquals(userID, "testid123");
    }

    @Test
    public void getReviewerdisplayName() {
        String displayName = reviewItem.getReviewerdisplayName();
        assertEquals(displayName, "testerName");
    }

    @Test
    public void getDate() {
        String date = reviewItem.getDate();
        assertEquals(date, "2021-09-09");
    }

    @Test
    public void getReview() {
        String review = reviewItem.getReview();
        assertEquals(review, "Fantastic person!");
    }
}