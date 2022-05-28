package com.coderzf1.colordropper.ui.favoritecolor;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.coderzf1.colordropper.databinding.FragmentFavoriteColorsBinding;
import com.coderzf1.colordropper.ui.favoritecolor.adapters.FavoriteColorsAdapter;

public class FragmentFavoriteColors extends Fragment {
    FavoriteColorViewModel model;
    FragmentFavoriteColorsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoriteColorsBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        binding.recyclerView.setAdapter(new FavoriteColorsAdapter(requireContext(),model.getAllColors()));
        return binding.getRoot();
    }
}