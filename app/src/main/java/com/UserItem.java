package com;

public class UserItem {
    private String id;
    private String profilePictureUri;
    private String name;
    private String email;
    private int residential;
    private String telegramHandle;
    private long phoneNumber;
    private boolean isStaff;

    /**
     * Constructor for the UserItem class
     *
     * @param id ID of the user
     * @param profilePictureUri Link to storage where the user's profile picture is stored
     * @param name Name of the user
     * @param email Email of the user
     * @param residential Residence on campus of the user
     * @param telegram Telegram handle of the user
     * @param phone Phone number of the user
     * @param isStaff If the user is a NUS staff member
     */
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
