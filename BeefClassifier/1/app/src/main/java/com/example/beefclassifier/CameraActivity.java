package com.example.beefclassifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.beefclassifier.tflite.Classifier;
import com.example.beefclassifier.tflite.Logger;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.okhttp.internal.framed.FrameReader;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.beefclassifier.ImageUtils.bitmapToByteArray;
import static com.example.beefclassifier.ImageUtils.imageProxyToBitmap;

public class CameraActivity extends AppCompatActivity {
    private PreviewView mPreviewView;
    private ImageButton CaptureBtn;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Classifier classifier;
    private Classifier.Device device = Classifier.Device.CPU;
    private int numThreads = -1;
    private Bitmap resizeimg;
    private Bitmap Originalimg;
    private Mat inputImage = new Mat(); // bitmap으로 c++로 못줘서 mat로 변환해서 전송
    private Mat outputImage; // 반환받는 mat
    private double check_fat = 0; // 마블링 정도
    private static final Logger LOGGER = new Logger();
    ProgressDialog customProgressDialog;
    // java c++연결 인터페이스 double(마블링)로 반환
    // 여기 수정하면 jni폴더에 .h, cpp 파일도 수정해야함
    // 옆에 app 말고 sdk 폴더 생긴건 opencv import하니까 생기더라
    public native double GrabCut(long matAddrInput, long matAddrResult);

    static { //c++라이브러리
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_acitivity);

        try {
            classifier = Classifier.create(this, device, numThreads);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPreviewView = findViewById(R.id.CameraView);
        CaptureBtn = findViewById(R.id.CaptureBtn);
        CaptureBtn.bringToFront();

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }
    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);


                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {

            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                // insert your code here.
                Originalimg = imageProxyToBitmap(image);

                image.close();

            }
        });

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());


        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);


        CaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classifier!=null){
                    customProgressDialog = new ProgressDialog(CameraActivity.this);
                    //로딩창을 투명하게
                    customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    customProgressDialog.show();
                    resizeimg= Bitmap.createScaledBitmap(Originalimg,224,224,true);

                    Utils.bitmapToMat(resizeimg, inputImage); // bitmap으로 전송안되서 mat으로 변환

                    if (outputImage == null) {
                        outputImage = new Mat(inputImage.rows(), inputImage.cols(), inputImage.type()); // 반환받을 mat 생성
                    }

                    // main.cpp 파일에서 그랩컷 한 이미지 outputImage에 저장
                    // check_fat에 마블링 정도 double 형으로 저장
                    check_fat = GrabCut(inputImage.getNativeObjAddr(), outputImage.getNativeObjAddr());

                    // 반환받은 이미지 resizeimg에 다시 저장
                    Utils.matToBitmap(outputImage, resizeimg);

                    final List<Classifier.Recognition> results =
                            classifier.recognizeImage(resizeimg, 90-getScreenOrientation());
                    Classifier.Recognition recognition =  results.get(0);
                    LOGGER.v("Detect: %s", results);
                    //Toast.makeText(MainActivity.this,recognition.getTitle()+","+recognition.getConfidence(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CameraActivity.this, ResultActivity.class);
                    intent.putExtra("image",bitmapToByteArray(Originalimg));
                    intent.putExtra("title",recognition.getTitle());
                    intent.putExtra("Confidence",recognition.getConfidence());
                    intent.putExtra("fat",check_fat);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            customProgressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                }

            }
        });
    }

    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }
}