package com;

import com.google.firebase.auth.FirebaseUser;

public class UserItem {
    public String id;
    public String profilePictureUri;
    public String name;
    public String email;
    public int residential;

    // 1 for cinnamon, 2 for tembusu, 3 for CAPT, 4 for RC4
    // halls ill do later
    // -1 for not specified

    public UserItem(String id, String profilePictureUri, String name, String email, int residential) {
        this.id = id;
        this.profilePictureUri = profilePictureUri;
        this.name = name;
        this.email = email;
        this.residential = residential;
    }


//    public UserItem(int profilePicture, String name, String email, int residential) {
//        this.profilePicture =  profilePicture;
//        this.name = name;
//        this.email = email;
//        this.residential = residential;
//    }
//
//    // Overloaded Constructor to push to Firebase
//    public UserItem(String ID, int profilePicture, String name, String email, int residential) {
//        this.ID = ID;
//        this.profilePicture =  profilePicture;
//        this.name = name;
//        this.email = email;
//        this.residential = residential;
//    }

    public UserItem() {

    }

    public int getResidential() {
        return residential;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }
}
