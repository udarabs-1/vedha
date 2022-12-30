package com.example.vedha;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.examples.detection.tflite.Detector;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;


public class DetectFragment extends Fragment {
    ImageView mImageView;
    Button galleryBtn;
    Button cameraBtn;
    TextView detectText;
    ImageButton menuBtn;
    Button detectBtn;

    Uri image_uri;
    Bitmap bp;
    private static final int RESULT_LOAD_IMAGE = 123;
    public static final int IMAGE_CAPTURE_CODE = 654;
    Detector detector;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraBtn = view.findViewById(R.id.camera);
        cameraBtn.setOnClickListener(new View.OnClickListener() { @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 112); }
                else {
                    openCamera(); }
            }
            else {
                openCamera(); }
        }
        });



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detect, container, false);
        mImageView = root.findViewById(R.id.image12);
        galleryBtn = root.findViewById(R.id.gallery);
        menuBtn = root.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener((v)->showMenu());

        detectText = (TextView) root.findViewById(R.id.text_view_id);
        try {
            detector = TFLiteObjectDetectionAPIModel.create(getContext().getApplicationContext(),"modelLeaves.tflite","labelmap.txt", 320,true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RESULT_LOAD_IMAGE);



            }
        });



        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null &&  requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            image_uri = data.getData();
            doInference();
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK){
            bp = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(bp);
        }
    }

    public void doInference1() {

        Bitmap mutable = bp.copy(Bitmap.Config.ARGB_8888, true);
        Paint p1 = new Paint();
        p1.setColor(Color.RED);
        p1.setStyle(Paint.Style.STROKE);
        p1.setStrokeWidth(4);

        Paint p2 = new Paint();
        p2.setColor(Color.BLUE);
        p2.setTextSize(80);
        Canvas canvas = new Canvas(mutable);
        List<Detector.Recognition> recognitionList =detector.recognizeImage(bp);
        String name = "No Leaf";
        for (Detector.Recognition r:
                recognitionList) {
            if (r.getConfidence()>0.5) {
                Log.d("tryRecognition", r.getTitle() + "  " + r.getConfidence() + "  " + r.getId() + "  " + r.getLocation().toString());
                canvas.drawRect(r.getLocation(), p1);
                canvas.drawText(r.getTitle(), r.getLocation().left, r.getLocation().top,p2 );
                name = r.getTitle();
            }
        }

        detectText.setText(name+" Detected");
        mImageView.setImageBitmap(mutable);
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext(),LoginActivity.class ));
                    getActivity().finish();
                    return true;

                }
                return false;
            }
        });

    }



    public void doInference() {
        mImageView.setImageURI(image_uri);
        Bitmap inputBmp = uriToBitmap(image_uri);

        Bitmap mutable = inputBmp.copy(Bitmap.Config.ARGB_8888, true);
        Paint p1 = new Paint();
        p1.setColor(Color.RED);
        p1.setStyle(Paint.Style.STROKE);
        p1.setStrokeWidth(4);

        Paint p2 = new Paint();
        p2.setColor(Color.BLUE);
        p2.setTextSize(80);
        Canvas canvas = new Canvas(mutable);
        List<Detector.Recognition> recognitionList =detector.recognizeImage(inputBmp);
        String name = "No Leaf";
        for (Detector.Recognition r:
        recognitionList) {
            if (r.getConfidence()>0.5) {
                Log.d("tryRecognition", r.getTitle() + "  " + r.getConfidence() + "  " + r.getId() + "  " + r.getLocation().toString());
                canvas.drawRect(r.getLocation(), p1);
                canvas.drawText(r.getTitle(), r.getLocation().left, r.getLocation().top,p2 );
                name = r.getTitle();
            }
        }

        if (name == "salad") {
            name = "Salad Leaves";
        }

        if (name == "cabbage") {
            name = "Cabbage Leaves";
        }

        detectText.setText(name+" Detected");
        mImageView.setImageBitmap(mutable);
    }

    private void openCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

}