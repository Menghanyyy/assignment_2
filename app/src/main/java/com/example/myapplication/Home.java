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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    public static GeneralUser currentUser = null;

    BottomNavigationView bottomNavigationView ;
    HomeFragment mEventFragment;
    MapFragment mMapFragment;
    ProfileFragment mProfileFragment;

    DatabaseManager databaseManager;

    private int currentIndex = 0; //当前Frament索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mEventFragment = HomeFragment.newInstance();//EventFragment.newInstance("","");
        mMapFragment = MapFragment.newInstance("","");
        mProfileFragment = ProfileFragment.newInstance("","");


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
                        switch (item.getItemId()) {
                            case R.id.events:
                                changeTable(0);

                                break;
                            case R.id.map:
                                changeTable(1);
                                break;
                            case R.id.profile:
                                changeTable(2);
                                break;

                        }
                        return false;
                    }
                });
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

        // Optional: If you want to open the app settings
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void replaceFg(Fragment myFragment){

        Intent intent = getIntent();
        currentUser = (GeneralUser) intent.getParcelableExtra("user");

        Log.i("userId", currentUser.getUserId()+"");

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 开始一个Fragment事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // 将新的Fragment添加到Activity的布局中
        fragmentTransaction.replace(R.id.fragment_container, myFragment);

        // 提交事务
        fragmentTransaction.commit();
    }

    public void changeTable(Integer index){//导航栏切换方法
        if(0==index){
            if(0==currentIndex){
                //iflycode

            }else{
                replaceFg(mEventFragment);
                currentIndex = 0;

            }

        }else if(1 == index){
            if(1==currentIndex){

            }else{
                Intent intent = new Intent(this, NewLink.class);
                startActivity(intent);

            }
        }else{
            if(2==currentIndex){

            }else{
                replaceFg(mProfileFragment);
                currentIndex = 2;
            }

        }

    }

}