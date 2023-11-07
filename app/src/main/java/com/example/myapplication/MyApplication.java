package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.component.GeneralUser;

public class MyApplication extends Application {
    private static MyApplication instance;
    private static GeneralUser currentUser;

    public int eventPagerTabIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return MyApplication.instance;
    }

    public static GeneralUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(GeneralUser user) {
        currentUser = user;
    }


    public Object getSystemService(String name) {
        // 在这里可以添加自己的逻辑来处理系统服务
        return super.getSystemService(name);
    }

}
