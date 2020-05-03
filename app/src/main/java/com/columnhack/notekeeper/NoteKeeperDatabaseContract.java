package com.columnhack.notekeeper;

import android.provider.BaseColumns;

import static android.provider.BaseColumns._ID;

public final class NoteKeeperDatabaseContract {
    // There's no reason for anyone to create an instance of this class
    // So we give it a private constructor
    private NoteKeeperDatabaseContract(){ // make non-creatable

    }

    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        // CREATE TABLE course_info (course_id, course_title)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_COURSE_ID + " TEXT NOT NULL UNIQUE," +
                        COLUMN_COURSE_TITLE + " TEXT NOT NULL)";
    }

    public static final class NoteInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_COURSE_ID + " TEXT NOT NULL, " +
                        COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_NOTE_TEXT + " TEXT)";
    }
}
