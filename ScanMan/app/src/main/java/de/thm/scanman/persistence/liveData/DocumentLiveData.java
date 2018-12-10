package de.thm.scanman.persistence.liveData;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.thm.scanman.model.Document;

public class DocumentLiveData extends LiveData<Document> {
    private Query query;
    private LifecycleOwner owner;
    private final UserListener userListener = new UserListener();

    private class UserListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Document document = dataSnapshot.getValue(Document.class);
            document.setId(dataSnapshot.getKey());
            setValue(document);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("DocumentLiveData","DatabaseError: " + databaseError.getCode());
        }
    }

    public DocumentLiveData(Query query, LifecycleOwner owner) {
        this.query = query;
        this.owner = owner;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(userListener);
        Log.d("LiveData", "Document Live Data connected");
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(userListener);
        Log.d("LiveData", "Document Live Data disconnected");
    }
}