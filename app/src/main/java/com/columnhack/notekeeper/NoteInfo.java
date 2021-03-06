package com.columnhack.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

public final class NoteInfo implements Parcelable{
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;
    private int mId;


    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    public NoteInfo(int id, CourseInfo course, String noteTitle, String noteText) {
        this(course, noteTitle, noteText);
        setId(id);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }


    private NoteInfo(Parcel parcel) {
        // A class loader provides information on
        // how to create instances of a type
        mCourse = parcel.readParcelable(CourseInfo.class.getClassLoader());
        mTitle = parcel.readString();
        mText = parcel.readString();
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Responsible to write the member information
        // for the type instance into the parcel
        dest.writeParcelable(mCourse, 0);
        dest.writeString(mTitle);
        dest.writeString(mText);
    }

    public static final Parcelable.Creator<NoteInfo> CREATOR =
            new Parcelable.Creator<NoteInfo>(){

                @Override
                public NoteInfo createFromParcel(Parcel source) {
                    // Set the values in the same order
                    // as you set them in writeToParcel
                    return new NoteInfo(source);
                }

                @Override
                public NoteInfo[] newArray(int size) {
                    return new NoteInfo[size];
                }
            };
}
