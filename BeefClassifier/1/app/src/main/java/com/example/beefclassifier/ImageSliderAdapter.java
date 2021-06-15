package com.example.beefclassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private Bitmap[] sliderImage;
    private View.OnClickListener itemClickListener;
    public ImageSliderAdapter(Context context, Bitmap[] sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage[position]);
    }


    // (3) 외부에서 클릭 시 이벤트 설정
    public void setItemClickListener(View.OnClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        return sliderImage.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
            mImageView.setOnClickListener(itemClickListener);
        }

        public void bindSliderImage(Bitmap imageBitmap) {
            Glide.with(context)
                    .load(imageBitmap)
                    .into(mImageView);
        }
    }
}

