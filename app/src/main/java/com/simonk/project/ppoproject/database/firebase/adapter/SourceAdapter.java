package com.simonk.project.ppoproject.database.firebase.adapter;

import com.simonk.project.ppoproject.database.firebase.model.HistoryFirebaseModel;
import com.simonk.project.ppoproject.database.firebase.model.SourceFirebaseModel;
import com.simonk.project.ppoproject.model.HistoryEntry;

public class SourceAdapter {

    public static HistoryEntry convertFirebaseToModelSource(SourceFirebaseModel model) {
        if (model == null) {
            return null;
        }

        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setPath(model.path);
        historyEntry.setName(model.name);

        return historyEntry;
    }

    public static SourceFirebaseModel convertModelToFirebaseSource(HistoryEntry historyEntry) {
        if (historyEntry == null) {
            return null;
        }

        SourceFirebaseModel model = new SourceFirebaseModel();
        model.path = historyEntry.getPath();
        model.name = historyEntry.getName();

        return model;
    }

}
