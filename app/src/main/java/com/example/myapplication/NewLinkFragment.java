package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.myapplication.component.Event;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class NewLinkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewLinkFragment() {
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
    public static NewLinkFragment newInstance(String param1, String param2) {
        NewLinkFragment fragment = new NewLinkFragment();
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


    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home)getActivity()).setTopNavigationVisibility(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_link, container, false);

        DatabaseManager databaseManager = new DatabaseManager(getContext());

        MaterialButton insertBt = view.findViewById(R.id.insertButton);
        TextView linkField = view.findViewById(R.id.link);
        insertBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Button clicked", "OK");

                String link = linkField.getText().toString();

                // Try to get the event ID from the link
                databaseManager.getEventByLink(link, new DatabaseCallback<Event>() {
                    @Override
                    public void onSuccess(Event result) {

                        Log.i("Success link", link);

                        // Use event ID to join the event
                        databaseManager.joinEvent(MyApplication.getCurrentUser().getUserId(),
                                result.getEventId(), new DatabaseCallback<String>() {

                                    @Override
                                    public void onSuccess(String result) {
                                        Log.i("join event success", result.toString());
                                        ((Home)getActivity()).navigationChange(0);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.println(Log.ASSERT, "Error joining", error);
//                                        ((Home)getActivity()).navigationChange(0);
                                    }
                                });
                    }

                    @Override
                    public void onError(String error) {
                        Log.println(Log.ASSERT, "Error Retrieving json", error);
                    }
                });
            }
        });

        view.findViewById(R.id.Root_new_link).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });

        return view;
    }

}
