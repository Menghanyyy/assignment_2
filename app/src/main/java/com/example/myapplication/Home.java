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
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {


    ViewGroup topNavigationView;
    BottomNavigationView bottomNavigationView ;
    HomeFragment mEventFragment;
    MapFragment mMapFragment;
    NewLinkFragment mNewLinkFragment;
    ProfileFragment mProfileFragment;

    DatabaseManager databaseManager;

    private int currentIndex = 0; //当前Frament索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        topNavigationView = findViewById(R.id.topNavigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        mEventFragment = HomeFragment.newInstance();//EventFragment.newInstance("","");
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

        // 开始一个Fragment事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.addToBackStack(null); // Add this transaction to the back stack

        // 将新的Fragment添加到Activity的布局中
        fragmentTransaction.replace(R.id.fragment_container, myFragment);

        // 提交事务
        fragmentTransaction.commit();
    }


//    public void changeTable(Integer index){//导航栏切换方法
//        Log.i("index", index+"");
//        if(0==index){
//            if(0==currentIndex){
//                //iflycode
//
//            }else{
//                replaceFg(mEventFragment);
//                currentIndex = 0;
//
//            }
//
//        }else if(1 == index){
//            if(1==currentIndex){
//
//
//            }else{
//                Intent intent = new Intent(this, NewLink.class);
//                startActivity(intent);
//                currentIndex = 1;
//
//            }
//        }else{
//            if(2==currentIndex){
//
//            }else{
//                replaceFg(mProfileFragment);
//                currentIndex = 2;
//            }
//
//        }
//
//    }

    public void changeTable(Integer index) {
        Log.i("index", index + "");
        if (index == currentIndex) {
            // User tapped the same item that's already selected, let's reset to the event fragment
            if (index == 1) { // If we are on the map tab, let's reset it to the event fragment
                replaceFg(mEventFragment);
                currentIndex = 0;
                bottomNavigationView.setSelectedItemId(R.id.events); // You might need to reset the selected item in the bottom navigation
            }
            // You can handle other indices if needed.
        } else {
            // It's a different tab, we change to the new fragment
            switch (index) {
                case 0:
                    replaceFg(mEventFragment);
                    currentIndex = 0;
                    break;
                case 1:
//                    Intent intent = new Intent(this, NewLink.class);
//                    startActivity(intent);
                    replaceFg(mNewLinkFragment);
                    currentIndex = 1;
                    break;
                case 2:
                    replaceFg(mProfileFragment);
                    currentIndex = 2;
                    break;
                default:
                    // handle default or error case
                    break;
            }
        }
    }




    public void setBottomNavigationVisibility(boolean b) {

        if(b) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        else {

            bottomNavigationView.setVisibility(View.GONE);

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