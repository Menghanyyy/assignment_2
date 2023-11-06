package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseManager databaseManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(getActivity() instanceof Home) {
            ((Home) getActivity()).setTopNavigationVisibility(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        databaseManager = new DatabaseManager(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTitle();
        setProgressBarProgressThreaded();
    }

    private void addTitle(){
        String name = MyApplication.getCurrentUser().getName();

        // Create TextView for eventName
        TextView textView = getView().findViewById(R.id.user_name);
        textView.setText(name + "'s Progress");
        textView.setTextColor(Color.BLACK);

        Log.i("uu", MyApplication.getCurrentUser().getUserName());
        Log.i("ee", MyApplication.getCurrentUser().getUserEmail());
        Log.i("nn", MyApplication.getCurrentUser().getName());
//        Log.i("pp", MyApplication.getCurrentUser().getUserPin());
    }

    private void setProgressBarProgressThreaded() {

        String userID = MyApplication.getCurrentUser().getUserId();
        Log.i("User ID", userID);

        databaseManager.getJoinedEvents(userID, new DatabaseCallback<ArrayList<Event>>() {
            @Override
            public void onSuccess(ArrayList<Event> joinedEvents) {

                Log.i("get joined events", Integer.toString(joinedEvents.size()));
                LinearLayout progressBarContainer = getView().findViewById(R.id.progressContainer);

                for (Event event : joinedEvents){

                    databaseManager.visitCountForUserAtEvent(userID, event.getEventId(), new DatabaseCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer completedActivities) {
                            Log.i("user at event count:", completedActivities.toString());

                            databaseManager.getAllActivities(event.getEventId(), new DatabaseCallback<ArrayList<Activity>>() {
                                @Override
                                public void onSuccess(ArrayList<Activity> activities) {

                                    Log.i("get activities", String.valueOf(activities.size()));

                                    int totalActivities = activities.size();

                                    // Set the progress
                                    createNewBar(progressBarContainer, completedActivities, totalActivities, event.getEventName());
                                }

                                @Override
                                public void onError(String error) {
                                    Log.println(Log.ASSERT, "Error get activities", error);
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            Log.println(Log.ASSERT, "Error finding count", error);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error joined events", error);
            }
        });
    }

    private void createNewBar(LinearLayout progressBarContainer, int completedActivities, int totalActivities, String eventName){

        float progress;
        try {
            progress =  Math.round(completedActivities * 100 / totalActivities);
        } catch (Exception e){
            // Divide by 0 probably
            progress = 0;
        }

        // Create ProgressBar
        ProgressBar progressBar = new ProgressBar(getView().getContext(), null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        progressBarParams.setMargins(0, 0, 0, 16);
        progressBar.setLayoutParams(progressBarParams);
        progressBar.setProgress(Math.round(progress));
        progressBarContainer.addView(progressBar);

        // Create TextView for eventName
        TextView textView = new TextView(getView().getContext());
        textView.setLayoutParams(progressBarParams);
        textView.setText("Completed " + completedActivities + " / " +
                totalActivities + " activities in event '" + eventName + "'.");
        textView.setTextColor(Color.BLACK); // Set text color as needed
        progressBarContainer.addView(textView);

        // Add spacer after progress bar
        Space spacerAfter = new Space(getView().getContext());
        LinearLayout.LayoutParams spacerParamsAfter = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100
        );
        spacerAfter.setLayoutParams(spacerParamsAfter);
        progressBarContainer.addView(spacerAfter);
    }
}