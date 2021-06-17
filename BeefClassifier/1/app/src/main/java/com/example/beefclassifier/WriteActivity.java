package com.example.beefclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.perfmark.Tag;

import static com.example.beefclassifier.ImageUtils.BitmapToString;
import static com.example.beefclassifier.ImageUtils.StringToBitmap;
import static com.example.beefclassifier.ImageUtils.binaryStringToByteArray;
import static com.example.beefclassifier.ImageUtils.bitmapToByteArray;
import static com.example.beefclassifier.ImageUtils.byteArrayToBinaryString;
import static com.example.beefclassifier.ImageUtils.byteArrayToBitmap;



public class WriteActivity extends AppCompatActivity {
    private Button ConfirmBtn;
    private EditText titleedit,contentedit;
    private static final int PICTURE_REQUEST_CODE = 100;
    private ImageView attachimage1,attachimage2,attachimage3;
    private TextView attachtext;
    private ArrayList<Uri> Uris;
    private ArrayList<Bitmap> Bitmaps;
    private String Username;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String Postkey;
    private LinearLayout linearLayout;
    private ImageButton attachbtn,Backbtn;
    private ImageButton imageDeleteBtn1,imageDeleteBtn2,imageDeleteBtn3;
    private ImageButton[] imageDeleteBtns =new ImageButton[3];
    private ImageView[] images =new ImageView[3];
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //ClipData 또는 Uri를 가져온다

                Uri uri = data.getData();
                ClipData clipData = data.getClipData();
                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri urione = clipData.getItemAt(i).getUri();
                        //Uris.add(urione);
                        Bitmaps.add(UriToBitmap(urione));
                        //attachimage.setImageURI(urione);


                    }
                } else if (uri != null) {
                    //  Uris.add(uri);
                    Bitmaps.add(UriToBitmap(uri));
                    // attachimage.setImageURI(uri);

                }
                if(Bitmaps.size()>=1)
                    attachtext.setText(Bitmaps.size()+"/3");

            }
        }
        for(int i=0;i<Bitmaps.size();i++)
        {
            images[i].setImageBitmap(Bitmaps.get(i));
            imageDeleteBtns[i].setVisibility(View.VISIBLE);
            imageDeleteBtns[i].bringToFront();
        }
    }



    private Bitmap UriToBitmap(Uri selectedFileUri) {
        Bitmap bitmap=null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        // linearLayout = findViewById(R.id.LnLayout);

        Bitmaps = new ArrayList<Bitmap>();
        ConfirmBtn = findViewById(R.id.ConfirmButton);
        Backbtn = findViewById(R.id.WriteBackBtn);
        titleedit = findViewById(R.id.EditTitle);
        contentedit = findViewById(R.id.EditContent);
        attachimage1 = findViewById(R.id.attachimg1);
        attachimage2 = findViewById(R.id.attachimg2);
        attachimage3 = findViewById(R.id.attachimg3);
        attachbtn = findViewById(R.id.attachBtn);
        imageDeleteBtn1 = findViewById(R.id.ImageDeleteBtn1);
        imageDeleteBtn2 = findViewById(R.id.ImageDeleteBtn2);
        imageDeleteBtn3 = findViewById(R.id.ImageDeleteBtn3);
        images[0]= attachimage1;
        images[1]= attachimage2;
        images[2]= attachimage3;
        imageDeleteBtns[0] = imageDeleteBtn1;
        imageDeleteBtns[1] = imageDeleteBtn2;
        imageDeleteBtns[2] = imageDeleteBtn3;

        for(int i =0;i<imageDeleteBtns.length;i++)
        {
            imageDeleteBtns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId()==R.id.ImageDeleteBtn1)
                    {
                        Bitmaps.remove(0);


                    }
                    else if(v.getId()==R.id.ImageDeleteBtn2)
                    {
                        Bitmaps.remove(1);


                    }
                    else if(v.getId()==R.id.ImageDeleteBtn3)
                    {
                        Bitmaps.remove(2);


                    }
                    for(int i=0;i<3;i++)
                    {
                        images[i].setImageDrawable(null);
                        imageDeleteBtns[i].setVisibility(View.GONE);
                    }
                    for(int i=0;i<Bitmaps.size();i++)
                    {
                        images[i].setImageBitmap(Bitmaps.get(i));
                        imageDeleteBtns[i].setVisibility(View.VISIBLE);

                    }
                    attachtext.setText(Bitmaps.size()+"/3");
                }
            });
        }
        attachtext = findViewById(R.id.attachtext);
        attachtext.bringToFront();
        Intent intent = getIntent();
        Username = intent.getStringExtra("Username");
        database = FirebaseDatabase.getInstance();
        if(intent.getStringExtra("postkey") != null)
        {
            Postkey= intent.getStringExtra("postkey");
            ref = database.getReference("Board").child("post").child(Postkey);


            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Post post = snapshot.getValue(Post.class);
                    titleedit.setText(post.getTitle());
                    contentedit.setText(post.getContent());
                    if(post.getImages() != null)
                    {

                        String[] imguris = post.getImages().split(",");


                        for(int i =0; i< imguris.length;i++)
                        {
                            Bitmaps.add(StringToBitmap(imguris[i]));
                        }

                        for(int i=0;i<Bitmaps.size();i++)
                        {
                            images[i].setImageBitmap(Bitmaps.get(i));
                            imageDeleteBtns[i].setVisibility(View.VISIBLE);
                            imageDeleteBtns[i].bringToFront();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        attachtext.setText(Bitmaps.size()+"/3");
        attachbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bitmaps.size()>=3)
                {
                    Toast.makeText(WriteActivity.this, "사진은 3장까지 첨부 가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICTURE_REQUEST_CODE);
            }
        });

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("Board");
                HashMap<Object,String> hashMap = new HashMap<>();
                Post post = new Post();
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                post.setDate(mFormat.format(date));
                post.setUid(user.getUid());
                post.setWriterName(Username);
                post.setTitle(titleedit.getText().toString());
                post.setContent(contentedit.getText().toString());
                if(Bitmaps.size() >= 1) {
                    String a = "";
                    for (int i = 0; i<Bitmaps.size(); i++) {

                        if(i ==Bitmaps.size()-1)
                            a += BitmapToString(Bitmaps.get(i));
                        else
                            a +=  BitmapToString(Bitmaps.get(i)) + ",";
                    }
                    post.setImages(a);
                }
                else {

                }
                if(Postkey != null){
                    ref.child("post").child(Postkey).setValue(post);

                    ref.child("user-post").child(user.getUid()).child(Postkey).setValue(post);
                }
                else {
                    String key = ref.push().getKey();
                    ref.child("post").child(key).setValue(post);

                    ref.child("user-post").child(user.getUid()).child(key).setValue(post);
                }
                finish();
            }

        });

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}