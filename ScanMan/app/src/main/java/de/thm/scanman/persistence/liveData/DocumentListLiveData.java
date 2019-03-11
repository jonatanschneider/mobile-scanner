package de.thm.scanman.persistence.liveData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import de.thm.scanman.model.Document;

public class DocumentListLiveData extends LiveData<List<Document>> {
    private Query query;
    private final UserListener userListener = new UserListener();
    private List<Document> documentList = new ArrayList<>();

    private class UserListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> it = dataSnapshot.getChildren();
            documentList.clear();
            for(DataSnapshot ds: it) {
                Document document = ds.getValue(Document.class);
                document.setId(ds.getKey());
                documentList.add(document);
            }
            setValue(documentList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("DocumentListLiveData","DatabaseError: " + databaseError.getMessage());
        }
    }

    public DocumentListLiveData(Query query) {
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