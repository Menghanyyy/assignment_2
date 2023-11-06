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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment{

    ImageView imageView;

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

                Log.i("textSearch", charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emptyEventLayout = view.findViewById(R.id.emptyEventsView);
        eventsLayout = view.findViewById(R.id.eventsView);
        eventsCardLayout = view.findViewById(R.id.eventsCardView);
        empty_add = view.findViewById(R.id.iv_add);

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
                showEventsView(result);
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

        empty_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NewLink.class);
                startActivity(i);
            }
        });
    }

    private void showEventsView(ArrayList<Event> events) {
        emptyEventLayout.setVisibility(View.GONE);
        eventsLayout.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        for (Event event : events) {
            // Inflate the card layout
            View cardView = inflater.inflate(R.layout.event_card, eventsCardLayout, false);

            // Find views within the card and populate them
            ImageView mainImage = cardView.findViewById(R.id.mainImage);  // Assuming you've given your ImageView an ID
            TextView title = cardView.findViewById(R.id.eventTitle);
            TextView location = cardView.findViewById(R.id.eventLocation);
            TextView time = cardView.findViewById(R.id.eventTime);
            TextView desc = cardView.findViewById(R.id.eventDescription);

            title.setText(event.getEventName());
            location.setText("Location: Melbourne");
            desc.setText(event.getDescription());

            if(event.getImage().isEmpty()) {
                Log.i("Image", "No" );

                mainImage.setImageResource(R.mipmap.aaaa);

            } else {
                Log.i("Image", "Yes" );

                byte[] decodedImageBytes = Base64.decode(event.getImage(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
                mainImage.setImageBitmap(decodedBitmap);
            }


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent cardInfo = new Intent(getActivity(), EventPageActivity.class);//src to tagactivity
                    cardInfo.putExtra("eventId", event.getEventId());

                    // Add bbox to intent as a String
                    String pointsJson = new Gson().toJson(event.getEventRange());
                    cardInfo.putExtra("bbox", pointsJson);

                    getActivity().startActivity(cardInfo);
                }
            });


            // and similarly for other views...
            // Populate the views with data from the event
            // mainImage.setImageResource(event.getImageResource());  // Assuming Event has a method to provide image resource
            // title.setText(event.getTitle());
            // ... similarly, populate other views ...

            // Add the populated card to the parent layout
            eventsCardLayout.addView(cardView);
        }
    }


}
