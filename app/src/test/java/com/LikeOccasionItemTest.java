package com;

import static org.junit.Assert.*;

import org.junit.Test;
import com.LikeOccasionItem;

public class LikeOccasionItemTest {

    final LikeOccasionItem stub = new LikeOccasionItem("testoccasionid", "testuserid");
    @Test
    public void getOccasionID() {
        String occasionID = stub.getOccasionID();
        assertEquals(occasionID, "testoccasionid");
    }

    @Test
    public void getUserID() {
        String userID = stub.getUserID();
        assertEquals(userID, "testuserid");
    }

    @Test
    public void setOccasionID() {
        stub.setOccasionID("testoccasionid2");
        String newOccasionID = stub.getOccasionID();
        assertEquals(newOccasionID, "testoccasionid2");
    }

    @Test
    public void setUserID() {
        stub.setUserID("testuserid2");
        String newUserID = stub.getUserID();
        assertEquals(newUserID, "testuserid2");
    }
}