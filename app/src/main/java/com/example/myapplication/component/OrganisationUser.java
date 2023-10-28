package com.example.myapplication.component;

import java.util.ArrayList;
import java.util.List;

public class OrganisationUser extends User{

    private List<Event> hostingEvent;
    private String subscribeLink;
    private List<User> subscriber;


    public OrganisationUser(String UserId, String UserName, String UserEmail, String UserPin, String Name) {
        super(UserId, UserName, UserEmail, UserPin, Name);

        this.hostingEvent = new ArrayList<Event>();

        this.subscribeLink = "abc.com";

        this.subscriber = new ArrayList<User>();

    }

    public List<Event> getHostingEvent() {
        return this.hostingEvent;
    }

    public boolean addHostingEvent (Event event) {
       return this.hostingEvent.add(event);
    }

    public String getSubscribeLink() {
        return this.subscribeLink;
    }

    public List<User> getSubscriber() {
        return this.subscriber;
    }

    public boolean addSubscriber(User user) {
        return this.subscriber.add(user);
    }

    @Override
    public String toString() {
        return "OrganisationUser{" +
                "hostingEvent=" + hostingEvent +
                ", subscribeLink='" + subscribeLink + '\'' +
                ", subscriber=" + subscriber +
                '}';
    }
}
