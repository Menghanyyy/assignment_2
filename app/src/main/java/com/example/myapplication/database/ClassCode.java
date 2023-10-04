package com.example.myapplication.database;

import java.util.ArrayList;

public class ClassCode<T> {
    private final int code;
    private final Class<T> clazz;
    private boolean isArraylist;

    public ClassCode(int code, Class<T> clazz, boolean isArraylist) {
        this.code = code;
        this.clazz = clazz;
        this.isArraylist = isArraylist;
    }

    public int getCode() {
        return code;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public boolean getIsArraylist() {
        return isArraylist;
    }
}