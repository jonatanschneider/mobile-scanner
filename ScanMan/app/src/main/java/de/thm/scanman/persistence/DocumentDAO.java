package de.thm.scanman.persistence;

import android.arch.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {
    String userId = FirebaseAuth.getInstance().getUid();

    /**
     * Add all documents from the list into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     * @param documentList
     */
    public void addCreatedDocuments(List<Document> documentList) {
        documentList.forEach(document -> {
            document.setOwnerId(userId);
            DatabaseReference documentRef = FirebaseDatabase.createdDocsRef.child(userId).push();
            document.setId(documentRef.getKey());
            documentRef.setValue(document);
        });
    }

    /**
     * Add all documents from the list into the sharedDocuments node
     * @param documentList
     */
    public void addSharedDocuments(List<Document> documentList) {
        //TODO meaningful error messages instead of simple return
        documentList.forEach(document -> {
            if (userId.equals(document.getOwnerId())) return;
            if (document.getId().equals("") || document.getId() == null) return;
            FirebaseDatabase.sharedDocsRef.child(userId).child(document.getId()).setValue(document);
        });
    }

    private LiveData<List<Document>> get(DatabaseReference reference) {
        return new DocumentLiveData(reference);
    }

    LiveData<List<Document>> getCreatedDocuments() {
        return get(FirebaseDatabase.createdDocsRef.child(userId));
    }

    LiveData<List<Document>> getSharedDocuments() {
        return get(FirebaseDatabase.sharedDocsRef.child(userId));
    }

    public void update(Document... documents) {
        Arrays.asList(documents).forEach(document -> {
            // Update created docs
            if (document.getOwnerId().equals(userId)) {
                FirebaseDatabase.getCreatedDocumentsReference(document).setValue(document);
            }
            // Update shared docs
            else {
                FirebaseDatabase.getSharedDocumentsReference(userId, document).setValue(document);
            }
        });
    }

    public void update(List<Document> documentList) {
        update(documentList.toArray(new Document[0]));
    }

    public void remove(Document document) {
        DatabaseReference reference = document.getOwnerId().equals(userId) ?
                        FirebaseDatabase.createdDocsRef :
                        FirebaseDatabase.sharedDocsRef;
        reference.child(userId).child(document.getId()).removeValue();
    }

    public void removeAccess(Document document, String userToBeRemoved) {
        if (!document.getOwnerId().equals(userId)) return;
        document.getUserIds().remove(userToBeRemoved);
        update(document);
    }

    public void removeUserDocuments() {
        FirebaseDatabase.createdDocsRef.child(userId).removeValue();
        FirebaseDatabase.sharedDocsRef.child(userId).removeValue();
    }
}