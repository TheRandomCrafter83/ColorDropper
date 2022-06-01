package com.coderzf1.colordropper.ui.favoritecolor.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.databinding.FavoriteColorsItemBinding;

import java.util.List;

public class FavoriteColorsAdapter extends RecyclerView.Adapter<FavoriteColorsAdapter.FavoriteColorsViewHolder> {

    List<Color> items;



    public static class FavoriteColorsViewHolder extends RecyclerView.ViewHolder{
        private FavoriteColorsItemBinding binding;
        public FavoriteColorsViewHolder(FavoriteColorsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public FavoriteColorsAdapter(){

    }

    public void setItems(List<Color> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteColorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavoriteColorsItemBinding binding = FavoriteColorsItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FavoriteColorsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteColorsViewHolder holder, int position) {
        Color color = items.get(position);
        holder.binding.textViewColorName.setText(color.getColorName());
        holder.binding.textViewHexValue.setText(String.valueOf(color.getColorValue()));
        holder.binding.surfaceViewColorPreview.setBackgroundTintList(getColorValue(color.getColorValue()));
    }

    private ColorStateList getColorValue(int color){
        int[][] states = new int[][]{new int[]{0}};
        int[] colors = new int[]{color};
        return new ColorStateList(states,colors);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }
}
