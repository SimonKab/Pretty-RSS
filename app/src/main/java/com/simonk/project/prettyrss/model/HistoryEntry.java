package com.simonk.project.prettyrss.model;

public class HistoryEntry {

    private String mId;

    private String mPath;

    private String mName;

    public String getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }

    public void setId(String id) {
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
