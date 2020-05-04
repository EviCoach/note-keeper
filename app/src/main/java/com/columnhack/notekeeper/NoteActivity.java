//package com.columnhack.notekeeper;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.lifecycle.ViewModelProvider;
//
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.Spinner;
//
//import com.columnhack.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
//
//import java.util.List;
//
//public class NoteActivity extends AppCompatActivity {
//    public static final String NOTE_ID = "com.columnhack.notekeeper.NOTE_POSITION";
//    public static final int ID_NOT_SET = -1;
//    private NoteInfo mNote;
//    private boolean mIsNewNote;
//    private Spinner mSpinnerCourses;
//    private EditText mTextNoteTitle;
//    private EditText mTextNoteText;
//    private int mNewNotePosition;
//    private boolean mIsCancelling;
//    private NoteActivityViewModel mViewModel;
//    private NoteKeeperOpenHelper mDbOpenHelper;
//    private Cursor mNoteCursor;
//    private int mCourseIdPos;
//    private int mNoteTitlePos;
//    private int mNoteTextPos;
//    private int mNoteId;
//    private String mCourseId;
//    private String mNoteTitle;
//    private String mNoteText;
//
//    @Override
//    protected void onDestroy() {
//        mDbOpenHelper.close();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_note);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        mDbOpenHelper = new NoteKeeperOpenHelper(this);
//
//        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
//                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
//        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);
//
//        // Only restore when the activity is totally destroyed and recreated
//        if(mViewModel.mIsNewlyCreated && savedInstanceState != null){
//            mViewModel.restoreState(savedInstanceState);
//        }
//
//        mViewModel.mIsNewlyCreated = false;
//
//        mSpinnerCourses = findViewById(R.id.spinner_courses);
//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
//        ArrayAdapter<CourseInfo> adapterCourses =
//                // For the selected course
//                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
//        // for the spinner dropdown
//        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinnerCourses.setAdapter(adapterCourses);
//
//        readDisplayStateValues();
//        if(savedInstanceState == null)
//            saveOriginalNoteValue();
////        saveOriginalNoteValue();
//
//        mTextNoteTitle = findViewById(R.id.text_note_title);
//        mTextNoteText = findViewById(R.id.text_note_text);
//
//        if(!mIsNewNote)
//            loadNoteData();
//    } // ends onCreate
//
//    private void loadNoteData() {
//        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//
//        String selection = NoteInfoEntry._ID + " = ?";
//        String[] selectionArgs = {String.valueOf(mNoteId)};
//
//        String[] noteColumns = {
//                NoteInfoEntry.COLUMN_COURSE_ID,
//                NoteInfoEntry.COLUMN_NOTE_TITLE,
//                NoteInfoEntry.COLUMN_NOTE_TEXT
//        };
//        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
//                selection, selectionArgs, null, null, null);
//        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
//        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
//        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
//
//        mNoteCursor.moveToNext();
//        displayNote();
//    }
//
//    private void saveOriginalNoteValue() {
//        if(mIsNewNote) return;
//
////        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
////        mViewModel.mOriginalNoteTitle = mNote.getTitle();
////        mViewModel.mOriginalNoteText = mNote.getText();
//
//        mViewModel.mOriginalNoteCourseId = mCourseId;
//        mViewModel.mOriginalNoteTitle = mNoteTitle;
//        mViewModel.mOriginalNoteText = mNoteText;
//    }
//
//    private void displayNote() {
//        mCourseId = mNoteCursor.getString(mCourseIdPos);
//        mNoteTitle = mNoteCursor.getString(mNoteTitlePos);
//        mNoteText = mNoteCursor.getString(mNoteTextPos);
//
//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
//        CourseInfo course = DataManager.getInstance().getCourse(mCourseId);
//        int courseIndex = courses.indexOf(course);
//        mSpinnerCourses.setSelection(courseIndex);
//
//        mTextNoteTitle.setText(mNoteTitle);
//        mTextNoteText.setText(mNoteText);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        if(outState != null){
//            mViewModel.saveState(outState);
//        }
//    }
//
//
//    private void readDisplayStateValues() {
//        Intent intent = getIntent();
//        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
//
//        mIsNewNote = mNoteId == ID_NOT_SET;
//
//        if(mIsNewNote){
//            createNewNote();
//        }
//    }
//
//    private void createNewNote() {
//        DataManager dm = DataManager.getInstance();
//        mNewNotePosition = dm.createNewNote();
//        mNote = dm.getNotes().get(mNewNotePosition);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_note, menu);
//        return true;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if(mIsCancelling){
//            // Remove the empty note that was created if it's newNote
//            if(mIsNewNote){
//                DataManager.getInstance().removeNote(mNewNotePosition);
//            } else {
//                // Put the old values back to the note
//                storePreviousNoteValues();
//            }
//        } else {
//            saveNote();
//        }
//    }
//
//    private void storePreviousNoteValues() {
//        // Get a reference to the original course
//        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
//        mNote.setCourse(course);
//        mNote.setTitle(mViewModel.mOriginalNoteTitle);
//        mNote.setText(mViewModel.mOriginalNoteText);
//    }
//
//    private void saveNote() {
//        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
//        mNote.setTitle(mTextNoteTitle.getText().toString());
//        mNote.setText(mTextNoteText.getText().toString());
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_send_mail) {
//            sendEmail();
//            return true;
//        } else if(id == R.id.action_cancel){
//            mIsCancelling = true;
//            finish(); // while finishing, onPause method will get called
//        } else if(id == R.id.action_next){
//            moveNext();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void moveNext() {
//        // Save changes to the current note
//        // before advancing to the next one
//        saveNote();
//        ++mNewNotePosition;
//        mNote = DataManager.getInstance().getNotes().get(mNewNotePosition);
//
//        saveOriginalNoteValue();
//        displayNote();
//        invalidateOptionsMenu();
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu){
//        MenuItem item = menu.findItem(R.id.action_next);
//        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
//        item.setEnabled(mNewNotePosition < lastNoteIndex);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    private void sendEmail() {
//        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
//        String subject = mTextNoteTitle.getText().toString();
//        String text = "Checkout what I learned in the pluralsight course \"" +
//                course.getTitle() + "\"\n" + mTextNoteText.getText();
//        Intent intent = new Intent (Intent.ACTION_SEND);
//        intent.setType("message/rfc2822");
//        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        intent.putExtra(Intent.EXTRA_TEXT, text);
//
//        startActivity(intent);
//    }
//}

package com.columnhack.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.columnhack.notekeeper.NoteKeeperDatabaseContract.*;
import static com.columnhack.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_ID = "com.jwhh.jim.notekeeper.NOTE_ID";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.jwhh.jim.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.jwhh.jim.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.jwhh.jim.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNoteId;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;
    private SimpleCursorAdapter mAdapterCourses;

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        // Using SimpleCursorAdapter
        mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1}, 0);
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(mAdapterCourses);

        loadCourseData();

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalNoteValues(savedInstanceState);
        }

        mTextNoteTitle = (EditText) findViewById(R.id.text_note_title);
        mTextNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            loadNoteData();
        Log.d(TAG, "onCreate");
    }

    private void loadCourseData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String courseColumns[] = {
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };
        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns,
                null, null, null, null,
                CourseInfoEntry.COLUMN_COURSE_TITLE);
        mAdapterCourses.changeCursor(cursor);
    }

    private void loadNoteData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String selection = NoteInfoEntry._ID + " = ?";

        String[] selectionArgs = {Integer.toString(mNoteId)};

        String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT
        };
        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                selection, selectionArgs, null, null, null);
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteCursor.moveToNext();
        displayNote();
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote) return;
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            Log.i(TAG, "Cancelling note at position: " + mNoteId);
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNoteId);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
        Log.d(TAG, "onPause");
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void displayNote() {
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);

        int courseIndex = getIndexOfCourseId(courseId);
        mSpinnerCourses.setSelection(courseIndex);
        
        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = mAdapterCourses.getCursor();

        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        // Walk thru the cursor row by row
        // Trying to find the course we're looking for

        // We don't know where the cursor is currently positioned
        // So we move it from wherever it is to the first row
        // so we can loop thru it from beginning to end
        // looking for the current course
        boolean more = cursor.moveToFirst();
        while(more){
            String cursorCourseId = cursor.getString(courseIdPos);
            if(courseId.equals(cursorCourseId))
                break;
            courseRowIndex++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        mIsNewNote = mNoteId == ID_NOT_SET;
        if(mIsNewNote) {
            createNewNote();
        }

        Log.i(TAG, "mNoteId: " + mNoteId);
//        mNote = DataManager.getInstance().getNotes().get(mNoteId);

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNoteId = dm.createNewNote();
//        mNote = dm.getNotes().get(mNoteId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if(id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNoteId < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        ++mNoteId;
        mNote = DataManager.getInstance().getNotes().get(mNoteId);

        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course \"" +
                course.getTitle() +"\"\n" + mTextNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
