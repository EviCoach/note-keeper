package com.columnhack.notekeeper;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class NextThroughNotesTest {
    // This will start the activity before running the test
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void NextThroughNotes(){
        // Open the drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Navigate to the notes menu
        onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.notes_menu));

        // Select a note from the list of notes,
        // this will open NoteActivity
        onView(withId(R.id.list_items)).perform(RecyclerViewActions
                .actionOnItemAtPosition(0, click()));

        // In NoteActivity
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        for(int index = 0; index < notes.size(); index++) {
            NoteInfo note = notes.get(index);

            // Check the spinner text
            onView(withId(R.id.spinner_courses)).check(
                    matches(withSpinnerText(note.getCourse().getTitle())));

            // Check the note title
            onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
            onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));

            // go to the next note
            if(index < notes.size() - 1)
            onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());
        } // ends the for statement

        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();
    }
}