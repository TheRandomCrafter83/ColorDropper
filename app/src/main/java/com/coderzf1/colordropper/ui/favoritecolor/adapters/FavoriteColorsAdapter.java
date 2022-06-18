package com.coderzf1.colordropper.ui.favoritecolor.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.coderzf1.colordropper.database.Color;
import com.coderzf1.colordropper.databinding.FavoriteColorsItemBinding;
import com.coderzf1.colordropper.ui.favoritecolor.utils.Utils;

@SuppressWarnings("ALL")
public class FavoriteColorsAdapter extends ListAdapter<Color, FavoriteColorsAdapter.FavoriteColorsViewHolder> {

    public interface FavoriteColorsAdapterItemClickListener{
        void onItemClicked(int position);
    }

    private FavoriteColorsAdapterItemClickListener listener;

    @SuppressWarnings("unused")
    class FavoriteColorsViewHolder extends RecyclerView.ViewHolder{
        private final FavoriteColorsItemBinding binding;
        public FavoriteColorsViewHolder(FavoriteColorsItemBinding binding) {
            super(binding.getRoot());
            View itemView = binding.getRoot();
            final View.OnClickListener itemViewClick = view -> {
                int pos = getAdapterPosition();
                if(listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClicked(pos);
                }
            };
            itemView.setOnClickListener(itemViewClick);
            this.binding = binding;
        }

    }

    public FavoriteColorsAdapter(){
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<Color> DIFF_CALLBACK = new DiffUtil.ItemCallback<Color>() {
        @Override
        public boolean areItemsTheSame(@NonNull Color oldItem, @NonNull Color newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Color oldItem, @NonNull Color newItem) {
            return oldItem.getColorName().equals(newItem.getColorName()) &&
                    oldItem.getColorValue() == newItem.getColorValue();
        }
    };

    @NonNull
    @Override
    public FavoriteColorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavoriteColorsItemBinding binding = FavoriteColorsItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FavoriteColorsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteColorsViewHolder holder, int position) {
        Color color = getItem(position);
        holder.binding.textViewColorName.setText(color.getColorName());
        holder.binding.textViewHexValue.setText(Utils.colorIntToHexString(color.getColorValue()));
        holder.binding.surfaceViewColorPreview.setBackgroundTintList(getColorValue(color.getColorValue()));
    }

    public Color getColorAt(int position){
        return getItem(position);
    }

    public void setItemClickListener(FavoriteColorsAdapterItemClickListener listener){
        this.listener = listener;
    }

    private ColorStateList getColorValue(int color){
        int[][] states = new int[][]{new int[]{0}};
        int[] colors = new int[]{color};
        return new ColorStateList(states,colors);
    }

}
