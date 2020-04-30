package com.columnhack.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesFragment extends Fragment {

    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notes, container, false);
        FloatingActionButton fab = mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NoteActivity.class));
            }
        });
        initializeDisplayContent();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    public void initializeDisplayContent(){
        RecyclerView recyclerNotes = (RecyclerView) mView.findViewById(R.id.list_items);
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(getActivity());
        recyclerNotes.setLayoutManager(notesLayoutManager);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(getActivity(), notes);
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);
    } // ends initializeDisplayContent
}
