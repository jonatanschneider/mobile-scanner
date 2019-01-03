package de.thm.scanman.persistence;

import android.arch.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;

import de.thm.scanman.model.Document;
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
        documentList.forEach(document -> {
            List<String> users = document.getUserIds();
            users.add(userId);
            add(FirebaseDatabase.sharedDocsRef.child(userId), document);
        });
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
        String userId = FirebaseAuth.getInstance().getUid();
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

    public void removeUserDocuments(String userId) {
        FirebaseDatabase.createdDocsRef.child(userId).removeValue();
        FirebaseDatabase.sharedDocsRef.child(userId).removeValue();
    }

    public void remove(List<Document> documentList) {
        String uid = FirebaseAuth.getInstance().getUid();
        documentList.forEach(document -> {
            // Remove from createdDocuments
            if (document.getOwnerId().equals(uid))
                FirebaseDatabase.getCreatedDocumentsReference(document).removeValue();

            // Remove from sharedDocuments
            document.getUserIds().forEach(userId ->
                    FirebaseDatabase.getSharedDocumentsReference(userId, document).removeValue());
        });
    }

    public void remove(Document... documents) {
        remove(Arrays.asList(documents));
    }

}