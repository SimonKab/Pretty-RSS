package com.simonk.project.prettyrss.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

public interface LocalUserManager {

    LiveData<String> getAccountImage(Context context, String id);

    void saveAccountImage(Context context, String id, String path);

}
