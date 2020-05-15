package com.columnhack.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.columnhack.notekeeper.NoteActivity.LOADER_NOTES;
import static com.columnhack.notekeeper.NoteKeeperDatabaseContract.*;
import static com.columnhack.notekeeper.NotekeeperProviderContract.*;

public class NotesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

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
        initializeDisplayContent(); // WAS NOT ORIGINALLY HERE
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES){
            String noteColumns[] = {
                    Notes._ID,
                    Notes.COLUMN_NOTE_TITLE,
                    Notes.COLUMN_COURSE_TITLE
            };
            final String noteOrderBy = Courses.COLUMN_COURSE_TITLE +
                    "," + Notes.COLUMN_NOTE_TITLE;
            loader = new CursorLoader(getActivity(), Notes.CONTENT_EXPANDED_URI, noteColumns,
                    null, null, noteOrderBy);
//                @Override
//                public Cursor loadInBackground() {
//                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//                    String noteColumns[] = {
//                            NoteInfoEntry.getQName(NoteInfoEntry._ID),
//                            NoteInfoEntry.COLUMN_NOTE_TITLE,
//                            CourseInfoEntry.COLUMN_COURSE_TITLE
//                    };
//                    final String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE +
//                            "," + NoteInfoEntry.COLUMN_NOTE_TITLE;

                    // note_info JOIN course_info ON  note_info.course_id = course_info.course_id
//                    String tablesWithJoin =
//                            NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON " +
//                                    NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
//                                    CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

//                    Cursor cursor = db.query(tablesWithJoin, noteColumns,
//                            null, null, null, null,
//                            noteOrderBy);
//                    return cursor;
//                }
//            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES){
            mNoteRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES){
            mNoteRecyclerAdapter.changeCursor(null);
        }
    }
}
