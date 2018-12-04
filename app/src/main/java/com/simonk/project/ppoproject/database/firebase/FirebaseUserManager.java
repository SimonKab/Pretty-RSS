package com.simonk.project.ppoproject.database.firebase;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonk.project.ppoproject.database.UserManager;
import com.simonk.project.ppoproject.database.firebase.adapter.AccountAdapter;
import com.simonk.project.ppoproject.database.firebase.model.AccountFirebaseModel;
import com.simonk.project.ppoproject.model.Account;

import java.lang.ref.WeakReference;
import java.util.Objects;

import androidx.annotation.NonNull;

public class FirebaseUserManager implements UserManager {

    @Override
    public void getUserById(String userId, RetrieveDataListener<Account> dataListener) {
        DatabaseReference userReference =
                FirebaseDatabase.getInstance().getReference("users").child(userId);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataListener == null) {
                    return;
                }

                AccountFirebaseModel model = dataSnapshot.getValue(AccountFirebaseModel.class);
                Account account = AccountAdapter.convertFirebaseToModelAccount(model);
                if (account != null) {
                    account.setId(userId);
                }
                dataListener.onComplete(account);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dataListener == null) {
                    return;
                }

                if (databaseError.getCode() == DatabaseError.DISCONNECTED) {
                    dataListener.onDisconnected();
                } else if (databaseError.getCode() == DatabaseError.NETWORK_ERROR) {
                    dataListener.onNetworkError();
                } else {
                    dataListener.onError(databaseError.toException());
                }
            }
        });
    }

    @Override
    public void saveAccount(Account account, SaveDataListener listener) {
        DatabaseReference userReference =
                FirebaseDatabase.getInstance().getReference("users");
        String newUserId = userReference.push().getKey();
        if (newUserId == null) {
            throw new IllegalStateException("Firebase returns null id for new user");
        }

        internalSaveAccount(userReference, newUserId, account, listener);
    }

    @Override
    public void saveAccount(String userId, Account account, SaveDataListener listener) {
        DatabaseReference userReference =
                FirebaseDatabase.getInstance().getReference("users");

        internalSaveAccount(userReference, userId, account, listener);
    }

    private void internalSaveAccount(DatabaseReference userReference, String userId,
                                     Account account, SaveDataListener listener) {
        AccountFirebaseModel model = AccountAdapter.convertModelToFirebaseAccount(account);
        userReference.child(userId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
