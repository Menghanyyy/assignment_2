package com.example.myapplication.component;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class Visit {

    private String userId;

    public void setVisitingTime(LocalDateTime visitingTime) {
        this.visitingTime = visitingTime;
    }

    private LocalDateTime visitingTime;
    private String visitActivityId;
    private String visitEventId;

    private String visitOrganisationId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Visit(String UserId, String VisitActivityId, String VisitEventId, String VisitOrganisationId) {
        this.userId = UserId;
        this.visitingTime = LocalDateTime.now();
        this.visitActivityId = VisitActivityId;
        this.visitEventId = VisitEventId;
        this.visitOrganisationId = VisitOrganisationId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Visit(String UserId, String VisitActivityId) {
        this.userId = UserId;
        this.visitingTime = LocalDateTime.now();
        this.visitActivityId = VisitActivityId;
        this.visitEventId = "";
        this.visitOrganisationId = "";
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
