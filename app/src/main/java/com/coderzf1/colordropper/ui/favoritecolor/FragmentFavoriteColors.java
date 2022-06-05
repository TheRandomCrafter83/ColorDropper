package com.coderzf1.colordropper.ui.favoritecolor;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.databinding.FragmentFavoriteColorsBinding;
import com.coderzf1.colordropper.ui.favoritecolor.adapters.FavoriteColorsAdapter;
import com.coderzf1.colordropper.ui.favoritecolor.callbacks.SwipeToDeleteCallback;
import com.coderzf1.colordropper.ui.favoritecolor.viewmodel.FavoriteColorViewModel;
import com.google.android.material.snackbar.Snackbar;

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
            adapter.submitList(colors);
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Color swipedColor = adapter.getColorAt(position);
                model.delete(swipedColor);

                Snackbar snackbar = Snackbar
                        .make(binding.recyclerView, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        model.insert(swipedColor);
                        //adapter.restoreItem(adapter.getColorAt(position), viewHolder.getAdapterPosition());
                        //binding.recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(android.graphics.Color.YELLOW);
                snackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        return binding.getRoot();
    }
}