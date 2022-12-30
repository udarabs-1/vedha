package com.example.vedha;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    ViewActivity viewActivity = new ViewActivity();

    EditText inputSearch;
    RecyclerView recyclerView;
    FirestoreRecyclerOptions<Plant> options;
    FirestoreRecyclerAdapter<Plant, MyViewHolder> adapter;
    ArrayList<Plant> plantArrayList;
    FirebaseFirestore db;
    Query query;
    ImageButton menuBtn;
    //OnListItemClick onListItemClick;
    DocumentSnapshot snapshot;

    DocumentReference ref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_search, container, false);
        menuBtn = root.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener((v)->showMenu());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        plantArrayList = new ArrayList<Plant>();
        // query = db.collection("Plant").orderBy("PlantName").startAt(data).endAt(data+"\uf8ff");

        inputSearch = view.findViewById(R.id.inputSearch);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        EventChangeListner();
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

    private void EventChangeListner() {
        db.collection("Plant")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                plantArrayList.add(dc.getDocument().toObject(Plant.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
        LoadData("");

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString()!=null){
                    LoadData(editable.toString());
                }
                else
                {
                    LoadData("");
                }

            }
        });

    }

    private void LoadData(String data) {

        query = db.collection("Plant").orderBy("PlantName").startAt(data).endAt(data+"\uf8ff");

        options = new FirestoreRecyclerOptions.Builder<Plant>().setQuery(query, Plant.class).build();
        adapter = new FirestoreRecyclerAdapter<Plant, MyViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Plant model) {
                holder.textView.setText(model.getPlantName());
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ref = db.collection("Plant").document();
                        Bundle args = new Bundle();
                        args.putString("YourKey", getSnapshots().getSnapshot(position).getId());
                        viewActivity.setArguments(args);
                        //Inflate the fragment
                        getFragmentManager().beginTransaction().add(R.id.container, viewActivity).commit();



                    }
                });

            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
}