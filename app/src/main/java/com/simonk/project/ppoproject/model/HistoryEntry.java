package com.simonk.project.ppoproject.model;

public class HistoryEntry {

    private int mId;

    private String mPath;

    private String mName;

    public int getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
