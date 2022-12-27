package com.example.vedha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class NoteDetailsFragment extends Fragment {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root1 = inflater.inflate(R.layout.fragment_note_details, container, false);

        titleEditText = root1.findViewById(R.id.notes_title_text);
        contentEditText = root1.findViewById(R.id.notes_content_text);
        saveNoteBtn = root1.findViewById(R.id.save_note_btn);

        saveNoteBtn.setOnClickListener((v)->saveNote());
        return root1;
    }



    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if(noteTitle ==null || noteTitle.isEmpty()) {
            titleEditText.setError("Title is Required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimeStamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes").document();
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    titleEditText.getText().clear();
                    contentEditText.getText().clear();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(NoteDetailsFragment.this)
                            .commit();
                }
            }
        });
    }
}