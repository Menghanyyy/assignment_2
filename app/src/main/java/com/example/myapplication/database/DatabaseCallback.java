package com.example.myapplication.database;

public interface DatabaseCallback<T> {
    void onSuccess(T result);
    void onError(String error);
}