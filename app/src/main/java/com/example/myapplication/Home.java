package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import java.util.ArrayList;

/**
 * Class that use to handle navigation of the application.
 * Handle the fragment switching across the application.
 */
public class Home extends AppCompatActivity {


    private ViewGroup topNavigationView;
    private BottomNavigationView bottomNavigationView ;
    private HomeFragment mEventFragment;
    private NewLinkFragment mNewLinkFragment;
    private ProfileFragment mProfileFragment;

    private DatabaseManager databaseManager;

    private ImageView headerSearchButton;
    private LinearLayout headerButtons;
    private ImageView searchCloseBtn;
    private TextView headerTitle;
    private EditText searchBar;


    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        topNavigationView = findViewById(R.id.topNavigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        headerSearchButton = findViewById(R.id.header_search_button);
        searchCloseBtn  = findViewById(R.id.search_close_btn);
        headerButtons = findViewById(R.id.header_buttons);
        headerTitle = findViewById(R.id.header_title);
        searchBar = findViewById(R.id.search_bar);

        headerSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set visibility for the views
                headerButtons.setVisibility(View.GONE);
                headerTitle.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                searchCloseBtn.setVisibility(View.VISIBLE);
            }
        });
        searchCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set visibility for the views
                headerButtons.setVisibility(View.VISIBLE);
                headerTitle.setVisibility(View.VISIBLE);
                searchBar.setText("");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                searchBar.setVisibility(View.GONE);
                searchCloseBtn.setVisibility(View.GONE);
            }
        });

        mEventFragment = HomeFragment.newInstance();
        mProfileFragment = ProfileFragment.newInstance("","");
        mNewLinkFragment = NewLinkFragment.newInstance("","");

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Home.this, CreateEditEvent.class);
                startActivity(i);
            }
        });

        databaseManager = new DatabaseManager(this);

        replaceFg(mEventFragment);

        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Log.i("itemId", item.getItemId()+"");
                        switch (item.getItemId()) {
                            case R.id.events:
                                changeTable(0);
                                break;
                            case R.id.join:
                                changeTable(1);
                                break;
                            case R.id.profile:
                                changeTable(2);
                                break;

                        }
                        return true;
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeTable(MyApplication.getInstance().eventPagerTabIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.customise_toast, null, false);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText("Cannot get back anymore!");

        Toast toast = new Toast(Home.this);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }

    private void replaceFg(Fragment myFragment){

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Start a new fragment transition
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.addToBackStack(null); // Add this transaction to the back stack

        // Replaceing fragment content
        fragmentTransaction.replace(R.id.fragment_container, myFragment);

        // apply fragment
        fragmentTransaction.commit();
    }


    private void changeTable(Integer index) {
        if (index == currentIndex) {

            //Do nothing

        } else {

            // It's a different tab, we change to the new fragment
            switch (index) {

                case 0:
                    replaceFg(mEventFragment);
                    currentIndex = 0;
                    break;

                case 1:
                    replaceFg(mNewLinkFragment);
                    currentIndex = 1;
                    break;

                case 2:
                    replaceFg(mProfileFragment);
                    currentIndex = 2;
                    break;

                default:
                    break;
            }
        }
    }

    public void navigationChange(Integer index) {

        if(index == 0) {
            bottomNavigationView.setSelectedItemId(R.id.events);

        }
        else if(index == 1) {

            bottomNavigationView.setSelectedItemId(R.id.join);

        }else {

            bottomNavigationView.setSelectedItemId(R.id.profile);

        }
    }


    public void setTopNavigationVisibility(boolean b) {

        if(b) {
            topNavigationView.setVisibility(View.VISIBLE);
        }
        else {

            topNavigationView.setVisibility(View.GONE);

        }

    }
}