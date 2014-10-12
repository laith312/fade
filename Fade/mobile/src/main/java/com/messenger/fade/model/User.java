package com.messenger.fade.model;

public class User {

    private String userId;
    private String primaryEmail;
    private String userName;
    private String handle;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private int age;

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(final String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(final String handle) {
        this.handle = handle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(final String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }
}
