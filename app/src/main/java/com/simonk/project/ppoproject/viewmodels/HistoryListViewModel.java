package com.simonk.project.ppoproject.viewmodels;

import android.app.Application;

import com.simonk.project.ppoproject.model.HistoryEntry;
import com.simonk.project.ppoproject.repository.RssRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class HistoryListViewModel extends AndroidViewModel {

    public HistoryListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<RssRepository.DatabaseResult<List<HistoryEntry>>> getHistory() {
        return RssRepository.getInstance().getHistory();
    }

    public LiveData<RssRepository.DatabaseResult> saveHistory(HistoryEntry entry) {
        return RssRepository.getInstance().saveHistoryEntry(entry);
    }

    public LiveData<RssRepository.DatabaseResult<List<HistoryEntry>>> getSources() {
        return RssRepository.getInstance().getHistory();
    }

    public LiveData<RssRepository.DatabaseResult> saveSource(HistoryEntry entry) {
        return RssRepository.getInstance().saveHistoryEntry(entry);
    }
}
