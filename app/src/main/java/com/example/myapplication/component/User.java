package com.example.myapplication.component;

public abstract class User {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPin;

    public String getName() {
        return name;
    }

    private String name;

    public User(String UserId, String UserName, String UserEmail, String UserPin, String Name) {
        this.userId = UserId;
        this.userName = UserName;
        this.userEmail = UserEmail;
        this.userPin = UserPin;
        this.name = Name;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPin() {
        return this.userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }

    @Override
    public abstract String toString();
}
