package de.thm.scanman.persistence;

import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {

    public void add(DatabaseReference reference, Document document) {
        DatabaseReference documentRef = reference.push();
        document.setId(documentRef.getKey());
        documentRef.setValue(document);
    }

    public void addCreatedDocuments(User user, List<Document> documentList) {
        documentList.forEach(document -> {
            document.setOwnerId(user.getId());
            add(FirebaseDatabase.createdDocsRef.child(user.getId()), document);
        });
    }

    public void addSharedDocuments(User user, List<Document> documentList) {
        documentList.forEach(document -> {
            document.setOwnerId(user.getId());
            add(FirebaseDatabase.createdDocsRef.child(user.getId()), document);
            FirebaseDatabase.documentRef.child(document.getId()).setValue(document);
        });
    }

    public LiveData<List<Document>> get(DatabaseReference reference) {
        return new DocumentLiveData(reference);
    }

    public void update(Document document) {
        FirebaseDatabase.createdDocsRef.child(document.getOwnerId()).child(document.getId()).setValue(document);
        document.getUserIds().forEach(userId -> {
            FirebaseDatabase.sharedDocsRef.child(userId).child(document.getId()).setValue(document);
        });
    }

    public void remove(Document document) {
        FirebaseDatabase.createdDocsRef.child(document.getOwnerId()).child(document.getId()).removeValue();
        document.getUserIds().forEach(userId -> {
            FirebaseDatabase.sharedDocsRef.child(userId).child(document.getId()).removeValue();
        });
    }

}