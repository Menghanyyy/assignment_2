package com.example.myapplication.component;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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

    protected OrganisationUser(Parcel in) {
        super(in.readString(), in.readString(), in.readString(), in.readString(), in.readString());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(getUserId());
        dest.writeString(getUserName());
        dest.writeString(getUserEmail());
        dest.writeString(getUserPin());
        dest.writeString(getName());

    }

    // Parcelable.Creator implementation for OrganisationUser
    public static final Parcelable.Creator<OrganisationUser> CREATOR = new Parcelable.Creator<OrganisationUser>() {
        @Override
        public OrganisationUser createFromParcel(Parcel in) {
            return new OrganisationUser(in);
        }

        @Override
        public OrganisationUser[] newArray(int size) {
            return new OrganisationUser[size];
        }
    };
}
