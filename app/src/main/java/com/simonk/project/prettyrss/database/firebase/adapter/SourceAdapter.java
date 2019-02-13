package com.simonk.project.prettyrss.database.firebase.adapter;

import com.simonk.project.prettyrss.database.firebase.model.SourceFirebaseModel;
import com.simonk.project.prettyrss.model.HistoryEntry;

public class SourceAdapter {

    public static HistoryEntry convertFirebaseToModelSource(SourceFirebaseModel model) {
        if (model == null) {
            return null;
        }

        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setPath(model.path);
        historyEntry.setName(model.name);
        historyEntry.setId(model.id);

        return historyEntry;
    }

    public static SourceFirebaseModel convertModelToFirebaseSource(HistoryEntry historyEntry) {
        if (historyEntry == null) {
            return null;
        }

        SourceFirebaseModel model = new SourceFirebaseModel();
        model.path = historyEntry.getPath();
        model.name = historyEntry.getName();
        model.id = historyEntry.getId();

        return model;
    }

}
