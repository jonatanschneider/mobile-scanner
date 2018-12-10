package de.thm.scanman.persistence;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {

   public void add(Document document) {
        DatabaseReference reference = FirebaseDatabase.documentRef.push();
        document.setId(reference.getKey());
        reference.setValue(document);
    }

    public LiveData<Document> get(String docId, LifecycleOwner owner) {
       return new DocumentLiveData(FirebaseDatabase.documentRef.child(docId), owner);
    }

    public void update(Document document) {
        FirebaseDatabase.documentRef.child(document.getId()).setValue(document);

    }

    public void remove(String id) {
        FirebaseDatabase.documentRef.child(id).removeValue();
    }

}