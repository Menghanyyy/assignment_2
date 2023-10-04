package com.example.myapplication.database;

public interface DatabaseCallback<Object> {
    void onSuccess(Object result);
    void onError(String error);
}