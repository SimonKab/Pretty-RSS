package com.simonk.project.ppoproject.database.firebase.adapter;

import com.simonk.project.ppoproject.database.firebase.model.AccountFirebaseModel;
import com.simonk.project.ppoproject.database.firebase.model.HistoryFirebaseModel;
import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.model.HistoryEntry;

public class HistoryAdapter {

    public static HistoryEntry convertFirebaseToModelHistory(HistoryFirebaseModel model) {
        if (model == null) {
            return null;
        }

        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setPath(model.path);

        return historyEntry;
    }

    public static HistoryFirebaseModel convertModelToFirebaseHistory(HistoryEntry historyEntry) {
        if (historyEntry == null) {
            return null;
        }

        HistoryFirebaseModel model = new HistoryFirebaseModel();
        model.path = historyEntry.getPath();

        return model;
    }

}
