package com.coderzf1.colordropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.yellow,null));
        setSupportActionBar(toolbar);
        initializeApp();
    }

    public void initializeApp(){
        Fragment selFragment;
        selFragment = new FragmentColorPicker();
        currentFragment = selFragment;
        FragmentManager fm  = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment,selFragment);
        transaction.commit();
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            Fragment selFragment;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm  = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(currentFragment);
                switch (tab.getPosition()){
                    case 0:
                        selFragment = new FragmentColorPicker();
                        currentFragment = selFragment;
                        break;
                    case 1:
                        selFragment = new FragmentFavoriteColors();
                        currentFragment = selFragment;
                        break;
                }

                transaction.replace(R.id.fragment,selFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}