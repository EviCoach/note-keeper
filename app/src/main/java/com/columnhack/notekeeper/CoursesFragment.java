package com.columnhack.notekeeper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoursesFragment extends Fragment {

    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.fragment_courses, container, false);

        initializeDisplayContent();
        return mView;
    }


    public void initializeDisplayContent(){
        RecyclerView recyclerNotes = (RecyclerView) mView.findViewById(R.id.list_items);
        GridLayoutManager notesLayoutManager = new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.course_grid_span));
        recyclerNotes.setLayoutManager(notesLayoutManager);

        List<CourseInfo> notes = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(getActivity(), notes);
        recyclerNotes.setAdapter(mCourseRecyclerAdapter);
    } // ends initializeDisplayContent
}
