package com.columnhack.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.columnhack.notekeeper.NoteKeeperDatabaseContract.*;

public class NotesFragment extends Fragment {

    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private View mView;
    private NoteKeeperOpenHelper mDbOpenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbOpenHelper = new NoteKeeperOpenHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
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
        // Get the latest set of data
        // out of the database
        loadNotes();
    }

    private void loadNotes() {
        // Query the database
        // to get back the list of notes
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry._ID
        };

        String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + ", " + NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                null, null, null, null, noteOrderBy);
        mNoteRecyclerAdapter.changeCursor(noteCursor);
    }

    @Override
    public void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    public void initializeDisplayContent(){
        DataManager.loadFromDatabase(mDbOpenHelper);
        RecyclerView recyclerNotes = (RecyclerView) mView.findViewById(R.id.list_items);
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(getActivity());
        recyclerNotes.setLayoutManager(notesLayoutManager);

        mNoteRecyclerAdapter = new NoteRecyclerAdapter(getActivity(), null);
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);
    } // ends initializeDisplayContent
}
