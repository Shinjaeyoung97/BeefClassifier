package com.example.beefclassifier;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private ArrayList<Recipe> arrayList;
    private Context context;

    public RecipeAdapter(ArrayList<Recipe> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list, parent, false);
        RecipeViewHolder holder = new RecipeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecipeViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getFoodImg())
                .into(holder.recipe_image);
        holder.recipe_name.setText(arrayList.get(position).getFoodName());
        holder.recipe_supplies.setText(arrayList.get(position).getSupplies());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(v.getContext(), RecipeDetailActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipe_image;
        TextView recipe_name;
        TextView recipe_supplies;

        public RecipeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.recipe_image = itemView.findViewById(R.id.recipe_image);
            this.recipe_name = itemView.findViewById(R.id.recipe_name);
            this.recipe_supplies = itemView.findViewById(R.id.recipe_supplies);
        }
    }
}
