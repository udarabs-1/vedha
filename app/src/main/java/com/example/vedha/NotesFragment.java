package com.example.vedha;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;


public class NotesFragment extends Fragment {
    Context context;
    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;
    NoteDetailsFragment noteDetailsFragment = new NoteDetailsFragment();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        addNoteBtn = root.findViewById(R.id.add_note_btn);
        recyclerView = root.findViewById(R.id.recycler_view);

        menuBtn = root.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener((v)->showMenu());



        addNoteBtn.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, noteDetailsFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        Query query =  Utility.getCollectionReferenceForNotes().orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new NoteAdapter(options, getContext());
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();
    }

    void showMenu() {

    }

    void setupRecyclerView() {

    }

    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}