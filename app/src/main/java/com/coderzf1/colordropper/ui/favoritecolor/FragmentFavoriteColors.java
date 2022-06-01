package com.coderzf1.colordropper.ui.favoritecolor;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.databinding.FragmentFavoriteColorsBinding;
import com.coderzf1.colordropper.ui.favoritecolor.adapters.FavoriteColorsAdapter;

import java.util.List;

public class FragmentFavoriteColors extends Fragment {
    FavoriteColorViewModel model;
    FragmentFavoriteColorsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        model = ViewModelProviders.of(this).get(FavoriteColorViewModel.class);
        binding = FragmentFavoriteColorsBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        FavoriteColorsAdapter adapter = new FavoriteColorsAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        model.getAllColors().observe(getViewLifecycleOwner(), colors -> {
            Log.d("Debugging", "onCreateView: " + colors.size());
            adapter.setItems(colors);
        });
        return binding.getRoot();
    }
}