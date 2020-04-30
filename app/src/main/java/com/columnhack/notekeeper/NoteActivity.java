package com.columnhack.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSTION = "com.columnhack.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNewNotePosition;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

        // Only restore when the activity is totally destroyed and recreated
        if(mViewModel.mIsNewlyCreated && savedInstanceState != null){
            mViewModel.restoreState(savedInstanceState);
        }

        mViewModel.mIsNewlyCreated = false;

        mSpinnerCourses = findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                // For the selected course
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        // for the spinner dropdown
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        saveOriginalNoteValue();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
    } // ends onCreate

    private void saveOriginalNoteValue() {
        if(mIsNewNote) return;

        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mNote.getTitle();
        mViewModel.mOriginalNoteText = mNote.getText();
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState != null){
            mViewModel.saveState(outState);
        }
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSTION, POSITION_NOT_SET);

        mIsNewNote = position == POSITION_NOT_SET;

        if(mIsNewNote){
            createNewNote();
        } else{
            mNote = DataManager.getInstance().getNotes().get(position);
        }

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNewNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNewNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            // Remove the empty note that was created if it's newNote
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mNewNotePosition);
            } else {
                // Put the old values back to the note
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void storePreviousNoteValues() {
        // Get a reference to the original course
        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
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
        } else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish(); // while finishing, onPause method will get called
        } else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {
        // Save changes to the current note
        // before advancing to the next one
        saveNote();
        ++mNewNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNewNotePosition);

        saveOriginalNoteValue();
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNewNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the pluralsight course \"" +
                course.getTitle() + "\"\n" + mTextNoteText.getText();
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);
    }
}
