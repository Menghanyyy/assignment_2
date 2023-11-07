package com.example.myapplication.component;

import android.os.Parcelable;

public abstract class User implements Parcelable {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPin;

    private String name;

    public User(String UserId, String UserName, String Name, String UserEmail, String UserPin) {
        this.userId = UserId;
        this.userName = UserName;
        this.userEmail = UserEmail;
        this.userPin = UserPin;
        this.name = Name;
    }

    public String getName() {
        return name;
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
