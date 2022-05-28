package com.coderzf1.colordropper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.coderzf1.colordropper.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    String[] tabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.yellow,null));
        setSupportActionBar(binding.toolbar);
        initialize();
    }

    private void initialize(){
        initTabTitles();
        binding.viewPager.setUserInputEnabled(false);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private void initTabTitles(){
        String titles = getString(R.string.tab_titles);
        tabTitles = titles.split(",");
    }

}