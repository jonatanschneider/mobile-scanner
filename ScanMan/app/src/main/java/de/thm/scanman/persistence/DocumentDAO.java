package de.thm.scanman.persistence;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {

    public void add(Document document) {
        DatabaseReference reference = FirebaseDatabase.documentRef.push();
        document.setId(reference.getKey());
        reference.setValue(document);
    }

    public void addCreatedDocuments(User user, List<Document> documentList) {
        documentList.forEach(document -> {
            DatabaseReference reference = FirebaseDatabase.createdDocsRef.child(user.getId()).push();
            document.setOwnerId(user.getId());
            document.setId(reference.getKey());
            reference.setValue(document);
            FirebaseDatabase.documentRef.child(document.getId()).setValue(document);
        });
    }

    public void addSharedDocuments(User user, List<Document> documentList) {
        documentList.forEach(document -> {
            DatabaseReference reference = FirebaseDatabase.createdDocsRef.child(user.getId()).push();
            document.setId(reference.getKey());
            reference.setValue(document);
            FirebaseDatabase.documentRef.child(document.getId()).setValue(document);
        });
    }

    public LiveData<List<Document>> get(DatabaseReference reference, LifecycleOwner owner) {
        return new DocumentLiveData(reference, owner);
    }

    public void update(Document document) {
        FirebaseDatabase.documentRef.child(document.getId()).setValue(document);

    }

    public void remove(String id) {
        FirebaseDatabase.documentRef.child(id).removeValue();
    }

}