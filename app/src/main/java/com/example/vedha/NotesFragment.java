package com.example.vedha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NotesFragment extends Fragment {

    FloatingActionButton addNoteBtn;
    NoteDetailsFragment noteDetailsFragment = new NoteDetailsFragment();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);

        addNoteBtn = root.findViewById(R.id.add_note_btn);

        addNoteBtn.setOnClickListener((v)->{
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, noteDetailsFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });
        // Inflate the layout for this fragment
        return root;
    }
}