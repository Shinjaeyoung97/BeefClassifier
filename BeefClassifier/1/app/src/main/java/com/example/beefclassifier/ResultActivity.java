package com.example.beefclassifier;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.beefclassifier.ImageUtils.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.beefclassifier.ImageUtils.byteArrayToBitmap;

public class ResultActivity extends AppCompatActivity {
    private ImageView resultimgView;
    private TextView resultTitleView,resultconfView, resultgradeView;
    private Bitmap resultimg;
    private String resultTitle;
    private float resultConf;
    private String resultgrade;
    private double resultFat;
    private Toolbar toolbar;
    private Button reloadBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        reloadBtn = findViewById(R.id.reloadbutton);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        resultimgView = findViewById(R.id.resultimg);
        resultTitleView = findViewById(R.id.resultTitle);
        resultconfView = findViewById(R.id.resultdistribution);
        resultgradeView = findViewById(R.id.resultgradeView);

        Intent intent = getIntent();

        resultimg= byteArrayToBitmap(intent.getByteArrayExtra("image"));
        resultTitle=intent.getStringExtra("title");
        resultConf=intent.getFloatExtra("Confidence",0);
        resultFat=intent.getDoubleExtra("fat",0);

        Matrix matrix = new Matrix();
        matrix.preRotate(90,0,0);



        if(resultFat * 100 > 17)
            resultgrade = "1++";
        else if(resultFat * 100 < 17 && resultFat * 100 >= 12)
            resultgrade = "1+";
        else if(resultFat * 100 < 12 && resultFat * 100 >= 9)
            resultgrade = "1";
        else if (resultFat * 100 < 9 && resultFat * 100 >= 5)
            resultgrade = "2";
        else if(resultFat  * 100 < 5)
            resultgrade = "3";



        resultimg = Bitmap.createBitmap(resultimg,0,0,resultimg.getWidth(),resultimg.getHeight(),matrix,false);
        resultimgView.setImageBitmap(resultimg);
        resultTitleView.setText(resultTitle);
        resultconfView.setText(String.format("%.1f", resultFat*100) + "%");
        resultgradeView.setText(resultgrade + "등급");

        resultTitleView.bringToFront();
        resultconfView.bringToFront();

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this,CameraActivity.class));
                finish();
            }
        });
    }

}