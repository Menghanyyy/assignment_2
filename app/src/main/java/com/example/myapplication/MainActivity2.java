package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {
    BottomNavigationView bottomNavigationView ;
    EventFragment mEventFragment;
    MapFragment mMapFragment;
    ProfileFragment mProfileFragment;

    private int currentIndex = 0; //当前Frament索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mEventFragment = EventFragment.newInstance("","");
        mMapFragment = MapFragment.newInstance("","");
        mProfileFragment =ProfileFragment.newInstance("","");

        replaceFg(mEventFragment);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
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

    private void replaceFg(Fragment myFragment){
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
                replaceFg(mMapFragment);
                currentIndex = 1;

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