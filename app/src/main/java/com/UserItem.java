package com;

import com.google.firebase.auth.FirebaseUser;

public class UserItem {
    public String id;
    public String profilePictureUri;
    public String name;
    public String email;
    public int residential;
    public String TelegramHandle;
    public long PhoneNumber;

    public UserItem(String id, String profilePictureUri, String name, String email, int residential, String telegram, long phone) {
        this.id = id;
        this.profilePictureUri = profilePictureUri;
        this.name = name;
        this.email = email;
        this.residential = residential;
        this.TelegramHandle = telegram;
        this.PhoneNumber = phone;
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

    public String getTelegram() { return TelegramHandle; }

    public long getPhone() { return PhoneNumber;}

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }
}
