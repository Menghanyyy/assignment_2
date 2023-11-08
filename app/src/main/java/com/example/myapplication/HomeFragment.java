package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    final String[] tabs = new String[]{"Event Joined", "Event created"};
    private TabLayoutMediator mediator;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        // fragment view
        fragments.add(EventFragment.newInstance("","")); // 0
        fragments.add(CreatedEventFragment.newInstance("","")); // 1

        viewPager2.setOffscreenPageLimit(1);

        //Adapter
        viewPager2.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        mediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                tab.setText(tabs[position]);
            }
        });

        
        mediator.attach();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}