package com.example.myapplication.database;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;

import java.util.ArrayList;

public class ClassCodes {

    public static final int STRING_CLASS_VALUE = 1;
    public static final int INT_CLASS_VALUE = 2;
    public static final int EVENT_CLASS_VALUE = 3;
    public static final int ACTIVITY_CLASS_VALUE = 4;
    public static final int USER_CLASS_VALUE = 5;
    public static final int VISIT_CLASS_VALUE = 6;
    public static final int ACTIVITY_ARRAYLIST_CLASS_VALUE = 7;
    public static final int USER_ARRAYLIST_CLASS_VALUE = 8;
    public static final int VISIT_ARRAYLIST_CLASS_VALUE = 9;
    public static final int EVENT_ARRAYLIST_CLASS_VALUE = 10;

    // Class code values
    public static final ClassCode<String> STRING_CLASS = new ClassCode<>(
            STRING_CLASS_VALUE,
            String.class
    );
    public static final ClassCode<Integer> INT_CLASS = new ClassCode<>(
            INT_CLASS_VALUE,
            Integer.class
    );
    public static final ClassCode<Event> EVENT_CLASS = new ClassCode<>(
            EVENT_CLASS_VALUE,
            Event.class
    );
    public static final ClassCode<Activity> ACTIVITY_CLASS = new ClassCode<>(
            ACTIVITY_CLASS_VALUE,
            Activity.class
    );
    public static final ClassCode<User> USER_CLASS = new ClassCode<>(
            USER_CLASS_VALUE,
            User.class
    );
    public static final ClassCode<Visit> VISIT_CLASS = new ClassCode<>(
            VISIT_CLASS_VALUE,
            Visit.class
    );

    public static final ClassCode<ArrayList<Activity>> ACTIVITY_ARRAYLIST_CLASS =
            (ClassCode<ArrayList<Activity>>) new ClassCode<>(
                    ACTIVITY_ARRAYLIST_CLASS_VALUE,
                    new ArrayList<Activity>().getClass()
            );

    public static final ClassCode<ArrayList<User>> USER_ARRAYLIST_CLASS =
            (ClassCode<ArrayList<User>>) new ClassCode<>(
                    USER_ARRAYLIST_CLASS_VALUE,
                    new ArrayList<User>().getClass()
            );

    public static final ClassCode<ArrayList<Visit>> VISIT_ARRAYLIST_CLASS =
            (ClassCode<ArrayList<Visit>>) new ClassCode<>(
                    VISIT_ARRAYLIST_CLASS_VALUE,
                    new ArrayList<Visit>().getClass()
            );

    public static final ClassCode<ArrayList<Visit>> EVENT_ARRAYLIST_CLASS =
            (ClassCode<ArrayList<Visit>>) new ClassCode<>(
                    EVENT_ARRAYLIST_CLASS_VALUE,
                    new ArrayList<Event>().getClass()
            );
}
