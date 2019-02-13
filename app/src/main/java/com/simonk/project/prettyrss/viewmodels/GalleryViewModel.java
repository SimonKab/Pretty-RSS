package com.simonk.project.prettyrss.viewmodels;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {
    private final MutableLiveData<String> mPathData = new MutableLiveData<String>();

    public void setPath(String path) {
        mPathData.setValue(path);
    }

    public LiveData<String> getPath() {
        return mPathData;
    }
}
