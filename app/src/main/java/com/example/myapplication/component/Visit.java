package com.example.myapplication.component;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class Visit {

    private String userId;
    private LocalDateTime visitingTime;
    private String visitActivityId;
    private String visitEventId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Visit(String UserId, String VisitActivityId, String VisitEventId) {
        this.userId = UserId;
        this.visitingTime = LocalDateTime.now();
        this.visitActivityId = VisitActivityId;
        this.visitEventId = VisitEventId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getVisitingTime() {
        return this.visitingTime;
    }

    public String getVisitActivityId() {
        return this.visitActivityId;
    }

    public void setVisitActivityId(String visitActivityId) {
        this.visitActivityId = visitActivityId;
    }

    public String getVisitEventId() {
        return this.visitEventId;
    }

    public void setVisitEventId(String visitEventId) {
        this.visitEventId = visitEventId;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "userId='" + userId + '\'' +
                ", visitingTime=" + visitingTime.toString()+
                ", visitActivityId='" + visitActivityId + '\'' +
                ", visitEventId='" + visitEventId + '\'' +
                '}';
    }
}
