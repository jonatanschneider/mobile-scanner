package de.thm.scanman.persistence.liveData;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.FirebaseDatabase;

public class UserLiveData extends LiveData<User> {
    private Query query;
    private String userId;
    private LifecycleOwner owner;

    private final UserListener userListener = new UserListener();

    private class UserListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            FirebaseDatabase database = new FirebaseDatabase();
            Log.e("LiveData", "" + dataSnapshot);
            User user = dataSnapshot.getValue(User.class);
            user.setId(dataSnapshot.getKey());
            //Auth; to check if user has rights to see documents

            List<Document> createdDocuments = new ArrayList<>();
            List<Document> sharedDocuments = new ArrayList<>();

            List<String> sharedDocumentIds = new ArrayList<>();

            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

            ScanmanLiveData createdDocumentsLiveData = new ScanmanLiveData(FirebaseDatabase.createdDocsRef.child(userId));
            createdDocumentsLiveData.observe(owner, snapshot -> {
                List<String> ids;
                if (snapshot.getValue() != null) {
                    ids = snapshot.getValue(t);

                    ids.forEach(id -> {
                        database.documentDAO.get(id, owner).observe(owner, document -> {
                            Log.e("LiveData Document", "" + document);
                            createdDocuments.add(document);
                        });
                    });
                }
            });

            ScanmanLiveData sharedDocumentsLiveData = new ScanmanLiveData(FirebaseDatabase.sharedDocsRef.child(userId));
            sharedDocumentsLiveData.observe(owner, snapshot -> {
                Log.e("LiveData2", "" + snapshot);
                sharedDocumentIds.clear();
                if (snapshot.getValue() != null) {
                    sharedDocumentIds.addAll(snapshot.getValue(t));
                    sharedDocumentIds.forEach(id -> {
                        database.documentDAO.get(id, owner).observe(owner, document -> {
                            Log.e("LiveData Document", document.toString());
                            sharedDocuments.add(document);
                        });
                    });
                }
            });

            user.setCreatedDocuments(createdDocuments);
            user.setSharedDocuments(sharedDocuments);

            setValue(user);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("UserLiveData", "DatabaseError: " + databaseError.getCode());
        }
    }

    public UserLiveData(Query query, String userId, LifecycleOwner owner) {
        this.query = query;
        this.userId = userId;
        this.owner = owner;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(userListener);
        Log.d("LiveData", "User Live Data connected");
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(userListener);
        Log.d("LiveData", "User Live Data disconnected");
    }
}