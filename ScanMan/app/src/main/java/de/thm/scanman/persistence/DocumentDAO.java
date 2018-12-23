package de.thm.scanman.persistence;

import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {

    private void add(DatabaseReference reference, Document document) {
        DatabaseReference documentRef = reference.push();
        document.setId(documentRef.getKey());
        documentRef.setValue(document);
    }

    /**
     * Add all documents from the list into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     * @param ownerId
     * @param documentList
     */
    public void addCreatedDocuments(String ownerId, List<Document> documentList) {
        documentList.forEach(document -> {
            document.setOwnerId(ownerId);
            add(FirebaseDatabase.createdDocsRef.child(ownerId), document);
        });
    }

    /**
     * Add all documents from the list into the sharedDocuments node
     * @param userId
     * @param documentList
     */
    public void addSharedDocuments(String userId, List<Document> documentList) {
        documentList.forEach(document -> add(FirebaseDatabase.createdDocsRef.child(userId), document));
    }

    private LiveData<List<Document>> get(DatabaseReference reference) {
        return new DocumentLiveData(reference);
    }

    LiveData<List<Document>> getCreatedDocuments(String userId) {
        return get(FirebaseDatabase.createdDocsRef.child(userId));
    }

    LiveData<List<Document>> getSharedDocuments(String userId) {
        return get(FirebaseDatabase.sharedDocsRef.child(userId));
    }

    public void update(Document... documents) {
        Arrays.asList(documents).forEach(document -> {
            // Update in createdDocs
            FirebaseDatabase
                    .createdDocsRef
                    .child(document.getOwnerId())
                    .child(document.getId())
                    .setValue(document);

            // Update in sharedDocs
            document.getUserIds().forEach(userId -> {
                FirebaseDatabase
                        .sharedDocsRef
                        .child(userId)
                        .child(document.getId())
                        .setValue(document);
            });
        });

    }

    public void removeUserDocuments(String userId) {
        FirebaseDatabase.createdDocsRef.child(userId).removeValue();
        FirebaseDatabase.sharedDocsRef.child(userId).removeValue();
    }

    public void remove(List<Document> documentList) {
        documentList.forEach(document -> {
            // Remove from createdDocuments
            FirebaseDatabase
                    .createdDocsRef
                    .child(document.getOwnerId())
                    .child(document.getId())
                    .removeValue();

            // Remove from sharedDocuments
            document.getUserIds().forEach(userId ->
                    FirebaseDatabase
                            .sharedDocsRef
                            .child(userId)
                            .child(document.getId())
                            .removeValue());
        });
    }

    public void remove(Document... documents) {
        remove(Arrays.asList(documents));
    }

}