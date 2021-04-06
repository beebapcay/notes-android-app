package com.beebapcay.notesapp.entities;


public class Tag {


    private String mName;

    public Tag(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "mName='" + mName + '\'' +
                '}';
    }
}
