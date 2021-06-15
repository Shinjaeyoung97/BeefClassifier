package com.example.beefclassifier;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.beefclassifier.ImageUtils.StringToBitmap;

public class RecipeDetailActivity extends AppCompatActivity {
    private int position;
    private TextView detail;
    private TextView name;
    private TextView supplies;
    private ImageView image;
    private ImageButton backBtn;
    private DatabaseReference ref;
    private ValueEventListener mPostListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0) + 1;
        String p = Integer.toString(position);

        ref = FirebaseDatabase.getInstance().getReference("Recipe").child(p);

        detail = findViewById(R.id.recipe_content);
        name = findViewById(R.id.recipe_detail_name);
        supplies = findViewById(R.id.recipe_detail_supplies);
        image = findViewById(R.id.recipe_detail_image);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                name.setText(recipe.getFoodName());
                supplies.setText(recipe.getSupplies());
                detail.setText(recipe.getContent().replace("\\n", System.getProperty("line.separator") + System.getProperty("line.separator")));
                Glide.with(RecipeDetailActivity.this)
                        .load(recipe.getFoodImg())
                        .into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("d", "loadRecipe:onCancelled", databaseError.toException());
                Toast.makeText(RecipeDetailActivity.this, "Failed to load recipe.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addValueEventListener(postListener);

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;



    }
    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            ref.removeEventListener(mPostListener);
        }

    }
}
