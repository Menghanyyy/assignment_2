package com.example.myapplication.component;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User Object Class
 * Child Class of the User
 */
public class GeneralUser extends User{

    /** Class Field **/
    private List<Visit> visitedLocation;
    private List<String> subscribedOrganisation;

    /** Constructor **/
    public GeneralUser(String UserId, String UserName, String UserEmail, String UserPin, String Name) {
        super(UserId, UserName, UserEmail, UserPin, Name);

        this.visitedLocation = new ArrayList<Visit>();
        this.subscribedOrganisation = new ArrayList<String>();
    }

    /** Parcelable implementation **/
    protected GeneralUser(Parcel in) {
        super(in.readString(), in.readString(), in.readString(), in.readString(), in.readString());
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

    public static final Parcelable.Creator<GeneralUser> CREATOR = new Parcelable.Creator<GeneralUser>() {
        @Override
        public GeneralUser createFromParcel(Parcel in) {
            return new GeneralUser(in);
        }

        @Override
        public GeneralUser[] newArray(int size) {
            return new GeneralUser[size];
        }
    };

    /** Getter and Setter **/

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
