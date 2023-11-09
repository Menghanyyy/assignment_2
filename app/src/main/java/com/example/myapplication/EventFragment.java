package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.component.Event;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 *  This Fragment handle the create event of the home page
 */
public class EventFragment extends Fragment{


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // other
    private DatabaseManager databaseManager;

    private List<Event> events;

    //view
    private View emptyEventLayout;
    private View eventsLayout;
    private ViewGroup eventsCardLayout;

    private ImageView empty_add;

    private TextView searchBar;

    private GifImageView gifImageView;

    public EventFragment() {
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
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
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

        databaseManager = new DatabaseManager(this.getContext());
        events = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_event, container, false);
        View view = inflater.inflate(R.layout.fragment_event, container, false);



        searchBar = getActivity().findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if(charSequence.toString().isEmpty()) {
                    showEventsView((ArrayList<Event>) events);

                }
                else {
                    ArrayList <Event> tmpEvents = new ArrayList<>();
                    for(Event e : events) {
                        if(e.getEventName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            tmpEvents.add(e);
                        }
                    }

                    if(tmpEvents.size() > 0) {
                        showEventsView(tmpEvents);
                    }
                    else if(tmpEvents.size() <= 0) {
                        showEventsView(tmpEvents);
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emptyEventLayout = view.findViewById(R.id.emptyEventsView);
        eventsLayout = view.findViewById(R.id.eventsView);
        eventsCardLayout = view.findViewById(R.id.eventsCardView);
        empty_add = view.findViewById(R.id.iv_add);
        gifImageView=view.findViewById(R.id.loading_animation_layout);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((Home)getActivity()).setTopNavigationVisibility(true);

        databaseManager.getJoinedEvents(MyApplication.getCurrentUser().getUserId(), new DatabaseCallback<ArrayList<Event>>() {
            @Override
            public void onSuccess(ArrayList<Event> result) {
                events = result;


                String searhText = searchBar.getText().toString();
                if(searhText.isEmpty()) {
                    // send view
                    showEventsView(result);

                } else {

                    ArrayList<Event> tmpEvents = new ArrayList<>();
                    for(Event e : result) {
                        if(e.getEventName().toLowerCase().contains(searhText.toLowerCase())) {
                            tmpEvents.add(e);
                        }
                    }

                    showEventsView(tmpEvents);


                }
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error joined events", error);

                showEmptyEventsView();
            }
        });


    

    }

    private void showEmptyEventsView() {
        emptyEventLayout.setVisibility(View.VISIBLE);
        eventsLayout.setVisibility(View.GONE);
        gifImageView.setVisibility(View.GONE);

        empty_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Home) getActivity()).navigationChange(1);
            }
        });
    }

    private void showEventsView(ArrayList<Event> events) {

        emptyEventLayout.setVisibility(View.GONE);
        eventsLayout.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.GONE);


        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        eventsCardLayout.removeAllViews();

        for (Event event : events) {
            // Inflate the card layout
            View cardView = inflater.inflate(R.layout.event_card, eventsCardLayout, false);

            // Find views within the card and populate them
            ImageView mainImage = cardView.findViewById(R.id.mainImage);  // Assuming you've given your ImageView an ID
            TextView title = cardView.findViewById(R.id.eventTitle);
            TextView location = cardView.findViewById(R.id.eventLocation);
            TextView organisation = cardView.findViewById(R.id.eventOrganisation);
            TextView desc = cardView.findViewById(R.id.eventDescription);

            title.setText(event.getEventName());
            organisation.setText(event.getOrganisationName());

            if(event.getEventLocation().isEmpty()) {
                location.setText("Melbourne");
            }
            else {
                location.setText(event.getEventLocation());
            }

            desc.setText(event.getDescription());

            if(event.getImage().isEmpty()) {

                mainImage.setImageResource(R.drawable.img_placeholder);

            } else {

                byte[] decodedImageBytes = Base64.decode(event.getImage(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
                mainImage.setImageBitmap(decodedBitmap);
            }


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent cardInfo = new Intent(getActivity(), EventPageActivity.class);
                    cardInfo.putExtra("eventId", event.getEventId());

                    // Add bbox to intent as a String
                    String pointsJson = new Gson().toJson(event.getEventRange());
                    cardInfo.putExtra("bbox", pointsJson);

                    cardInfo.putExtra("source", "join");

                    getActivity().startActivity(cardInfo);
                }
            });


            eventsCardLayout.addView(cardView);
        }
    }


}
