package com.simonk.project.ppoproject.database.firebase;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonk.project.ppoproject.database.HistoryManager;
import com.simonk.project.ppoproject.database.firebase.adapter.AccountAdapter;
import com.simonk.project.ppoproject.database.firebase.adapter.HistoryAdapter;
import com.simonk.project.ppoproject.database.firebase.adapter.SourceAdapter;
import com.simonk.project.ppoproject.database.firebase.model.HistoryFirebaseModel;
import com.simonk.project.ppoproject.database.firebase.model.SourceFirebaseModel;
import com.simonk.project.ppoproject.model.HistoryEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class FirebaseHistoryManager extends FirebaseManager implements HistoryManager {

    @Override
    public void getHistoryEntries(String userId, RetrieveDataListener<List<HistoryEntry>> listener) {
        DatabaseReference historyReference =
                getFirebase().getReference("users").child(userId).child("history");
        historyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener == null) {
                    return;
                }

                List<HistoryEntry> historyEntryList = new ArrayList<>();
                for (DataSnapshot childrenSnapshot : dataSnapshot.getChildren()) {
                    HistoryFirebaseModel model = childrenSnapshot.getValue(HistoryFirebaseModel.class);
                    HistoryEntry historyEntry = HistoryAdapter.convertFirebaseToModelHistory(model);
                    historyEntryList.add(historyEntry);
                }

                listener.onComplete(historyEntryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener == null) {
                    return;
                }

                if (databaseError.getCode() == DatabaseError.DISCONNECTED) {
                    listener.onDisconnected();
                } else if (databaseError.getCode() == DatabaseError.NETWORK_ERROR) {
                    listener.onNetworkError();
                } else {
                    listener.onError(databaseError.toException());
                }
            }
        });
    }

    @Override
    public void getLastEntryInHistory(String userId, RetrieveDataListener<String> listener) {
        DatabaseReference historyReference =
                getFirebase().getReference("users").child(userId).child("history");
        historyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener == null) {
                    return;
                }

                List<HistoryEntry> historyEntryList = new ArrayList<>();
                for (DataSnapshot childrenSnapshot : dataSnapshot.getChildren()) {
                    HistoryFirebaseModel model = childrenSnapshot.getValue(HistoryFirebaseModel.class);
                    HistoryEntry historyEntry = HistoryAdapter.convertFirebaseToModelHistory(model);
                    historyEntryList.add(historyEntry);
                }

                if (historyEntryList.size() == 0) {
                    listener.onComplete(null);
                } else {
                    listener.onComplete(historyEntryList.get(historyEntryList.size() - 1).getPath());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener == null) {
                    return;
                }

                if (databaseError.getCode() == DatabaseError.DISCONNECTED) {
                    listener.onDisconnected();
                } else if (databaseError.getCode() == DatabaseError.NETWORK_ERROR) {
                    listener.onNetworkError();
                } else {
                    listener.onError(databaseError.toException());
                }
            }
        });
    }

    @Override
    public void getSources(String userId, RetrieveDataListener<List<HistoryEntry>> listener) {
        DatabaseReference historyReference =
                getFirebase().getReference("users").child(userId).child("sources");
        historyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener == null) {
                    return;
                }

                List<HistoryEntry> historyEntryList = new ArrayList<>();
                for (DataSnapshot childrenSnapshot : dataSnapshot.getChildren()) {
                    SourceFirebaseModel model = childrenSnapshot.getValue(SourceFirebaseModel.class);
                    HistoryEntry historyEntry = SourceAdapter.convertFirebaseToModelSource(model);
                    historyEntry.setId(childrenSnapshot.getKey());
                    historyEntryList.add(historyEntry);
                }

                listener.onComplete(historyEntryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener == null) {
                    return;
                }

                if (databaseError.getCode() == DatabaseError.DISCONNECTED) {
                    listener.onDisconnected();
                } else if (databaseError.getCode() == DatabaseError.NETWORK_ERROR) {
                    listener.onNetworkError();
                } else {
                    listener.onError(databaseError.toException());
                }
            }
        });
    }

    @Override
    public void addHistoryEntry(String userId, HistoryEntry historyEntry, SaveDataListener listener) {
        DatabaseReference historyReference =
                getFirebase().getReference("users").child(userId).child("history");
        String newHistoryEntry = historyReference.push().getKey();
        HistoryFirebaseModel model = HistoryAdapter.convertModelToFirebaseHistory(historyEntry);
        historyReference.child(newHistoryEntry).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener == null) {
                    return;
                }

                if (task.isSuccessful()) {
                    normalizeHistory(userId);
                    listener.onComplete();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseNetworkException error) {
                        listener.onNetworkError();
                    } catch (Exception e) {
                        listener.onError(e);
                    }
                }
            }

        });
    }

    @Override
    public void addSourceEntry(String userId, HistoryEntry historyEntry, SaveDataListener listener) {
        DatabaseReference historyReference =
                getFirebase().getReference("users").child(userId).child("sources");
        if (historyEntry.getId() == null) {
            String newHistoryEntry = historyReference.push().getKey();
            historyReference = historyReference.child(newHistoryEntry);
        } else {
            historyReference = historyReference.child(historyEntry.getId());
        }
        SourceFirebaseModel model = SourceAdapter.convertModelToFirebaseSource(historyEntry);
        historyReference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener == null) {
                    return;
                }

                if (task.isSuccessful()) {
                    listener.onComplete();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseNetworkException error) {
                        listener.onNetworkError();
                    } catch (Exception e) {
                        listener.onError(e);
                    }
                }
            }

        });
    }

    private void normalizeHistory(String userId) {
        DatabaseReference historyReference =
                getFirebase().getReference("users").child(userId).child("history");
        historyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > MAX_HISTORY_SIZE) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    for (int i = 0; i < dataSnapshot.getChildrenCount() - MAX_HISTORY_SIZE; i++) {
                        historyReference.child(iterator.next().getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void removeHistoryEntry(String userId, HistoryEntry historyEntry) {

    }

}
