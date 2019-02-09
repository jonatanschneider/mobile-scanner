package de.thm.scanman.persistence.liveData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import de.thm.scanman.model.Document;

public class DocumentLiveData extends LiveData<Document> {
    private Query query;
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
            Log.e("DocumentLiveData","DatabaseError: " + databaseError.getMessage());
        }
    }

    public DocumentLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(userListener);
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(userListener);
    }
}