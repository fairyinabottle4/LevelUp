package com.levelup.user;

public class UserItem {
    public String id;
    public String profilePictureUri;
    public String name;
    public String email;
    public int residential;
    public String telegramHandle;
    public long phoneNumber;
    private boolean isStaff;

    public UserItem(String id, String profilePictureUri, String name, String email,
                    int residential, String telegram, long phone, boolean isStaff) {
        this.id = id;
        this.profilePictureUri = profilePictureUri;
        this.name = name;
        this.email = email;
        this.residential = residential;
        this.telegramHandle = telegram;
        this.phoneNumber = phone;
        this.isStaff = isStaff;
    }

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

    public String getTelegram() {
        return telegramHandle;
    }

    public long getPhone() {
        return phoneNumber;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public boolean getIsStaff() {
        return this.isStaff;
    }
}
