package com.example.myapplication;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication instance;

    public int eventPagerTabIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return MyApplication.instance;
    }

    public Object getSystemService(String name) {
        // 在这里可以添加自己的逻辑来处理系统服务
        return super.getSystemService(name);
    }
}
