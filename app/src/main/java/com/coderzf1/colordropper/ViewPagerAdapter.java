package com.coderzf1.colordropper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.coderzf1.colordropper.ui.colorpicker.FragmentColorPicker;
import com.coderzf1.colordropper.ui.favoritecolor.FragmentFavoriteColors;

public class ViewPagerAdapter extends FragmentStateAdapter {


    @SuppressWarnings("unused")
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new FragmentColorPicker();
        //if position is number of tabs, return the final fragment
        return new FragmentFavoriteColors();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
