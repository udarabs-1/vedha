package com.example.vedha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;


public class ViewActivity extends Fragment {

    private ImageView imageView;
    TextView textView1;
    TextView textView2;
    ImageButton backBtn;


    DocumentReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_activity, container, false);
        imageView = root.findViewById(R.id.image_single_view_activity);
        textView1 = root.findViewById(R.id.text_single_view_Activity);
        textView2 = root.findViewById(R.id.textDescription_single_view_Activity);
        backBtn = root.findViewById(R.id.backBtn);
        String PlantKey = getArguments().getString("YourKey");
        Log.d("MESSAGE","The Document ID: " + PlantKey);


        ref= FirebaseFirestore.getInstance().collection("Plant").document(PlantKey);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value !=null && value.exists()){
                    String PlantName=value.get("PlantName").toString();
                    String ImageUrl=value.get("ImageUrl").toString();
                    String Description= value.get("Description").toString();

                    Picasso.get().load(ImageUrl).into(imageView);
                    textView1.setText(PlantName);
                    textView2.setText(Description);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(ViewActivity.this)
                        .commit();
            }
        });
        return root;
    }


}