package com.example.myapplication.component;

import java.util.ArrayList;
import java.util.List;

public class GeneralUser extends User{

    private List<Visit> visitedLocation;
    private List<String> subscribedOrganisation;


    public GeneralUser(String UserId, String UserName, String UserEmail, String UserPin) {
        super(UserId, UserName, UserEmail, UserPin);

        this.visitedLocation = new ArrayList<Visit>();
        this.subscribedOrganisation = new ArrayList<String>();
    }

    public List<Visit> getVisitedLocation() {
        return this.visitedLocation;
    }

    public boolean addVisitedLocation (Visit visit) {
        return this.visitedLocation.add(visit);
    }

    public List<String> getSubscribedOrganisation() {
        return this.subscribedOrganisation;
    }

    public boolean addSubscribedOrganisation(String subscribeLink) {
        return this.subscribedOrganisation.add(subscribeLink);
    }

    @Override
    public String toString() {
        return "GeneralUser{" +
                "visitedLocation=" + visitedLocation +
                ", subscribedOrganisation=" + subscribedOrganisation +
                '}';
    }
}
