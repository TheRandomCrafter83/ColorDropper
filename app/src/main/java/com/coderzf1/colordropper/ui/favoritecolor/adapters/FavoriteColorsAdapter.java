package com.coderzf1.colordropper.ui.favoritecolor.adapters;

import android.content.Context;
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

    FavoriteColorsItemBinding binding;
    LiveData<List<Color>> items;

    public static class FavoriteColorsViewHolder extends RecyclerView.ViewHolder{
        public FavoriteColorsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public FavoriteColorsAdapter(Context context, LiveData<List<Color>> items){
        this.items = items;
        binding = FavoriteColorsItemBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    public FavoriteColorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteColorsViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteColorsViewHolder holder, int position) {
        //TODO: design the layout, then update to set values
    }

    @Override
    public int getItemCount() {
        if (items.getValue() == null) return 0;
        return items.getValue().size();
    }
}
