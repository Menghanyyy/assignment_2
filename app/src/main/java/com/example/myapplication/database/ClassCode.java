package com.example.myapplication.database;

public class ClassCode<T> {
    private final int code;
    private final Class<T> clazz;

    public ClassCode(int code, Class<T> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public int getCode() {
        return code;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}