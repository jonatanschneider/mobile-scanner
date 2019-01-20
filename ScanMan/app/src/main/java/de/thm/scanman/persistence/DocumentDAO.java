package de.thm.scanman.persistence;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.liveData.DocumentListLiveData;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {
    private String userId = FirebaseAuth.getInstance().getUid();

    /**
     * Add the document into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     * @param document
     */
    public void addCreatedDocument(Document document) {
        document.setOwnerId(userId);
        DatabaseReference documentRef = FirebaseDatabase.createdDocsRef.child(userId).push();
        document.setId(documentRef.getKey());
        uploadImages(document);
        documentRef.setValue(document);
    }

    /**
     * Add all documents from the list into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     * @param documentList
     */
    public void addCreatedDocuments(List<Document> documentList) {
        documentList.forEach(this::addCreatedDocument);
    }

    private void uploadImages(Document document) {
        for (int i = 0; i < document.getImages().size(); i++) {
            Document.Image image = document.getImages().get(i);
            Uri uri = Uri.parse(image.getFile());
            //Skip files that are already uploaded
            if (!uri.getScheme().equals("file")) continue;

            StorageReference reference = FirebaseDatabase.documentStorageRef
                    .child(document.getId())
                    .child(image.getId());

            image.setFile(reference.toString());

            UploadTask uploadTask = reference.putFile(uri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                System.out.println("upload success");
            }).addOnFailureListener(exception -> {
                System.out.println("upload failed");
            });
        }
    }

    /**
     * Add document into the sharedDocuments node
     * @param document
     */
    public void addSharedDocument(Document document) {
        if (userId.equals(document.getOwnerId())) return;
        if (document.getId() == null || document.getId().equals("")) return;
        FirebaseDatabase.sharedDocsRef.child(userId).child(document.getId()).setValue(document);
    }

    /**
     * Add all documents from the list into the sharedDocuments node
     * @param documentList
     */
    public void addSharedDocuments(List<Document> documentList) {
        documentList.forEach(this::addSharedDocument);
    }

    public LiveData<Document> getCreatedDocument(String documentId) {
        return new DocumentLiveData(FirebaseDatabase.createdDocsRef.child(userId).child(documentId));
    }

    LiveData<List<Document>> getCreatedDocuments() {
        return new DocumentListLiveData(FirebaseDatabase.createdDocsRef.child(userId));
    }

    LiveData<List<Document>> getSharedDocuments() {
        return new DocumentListLiveData(FirebaseDatabase.sharedDocsRef.child(userId));
    }

    /**
     * Updates all documents
     * Automatically decides whether updating created or shared documents
     * @param documents single document or an array of documents
     */
    public void update(Document... documents) {
        Arrays.asList(documents).forEach(document -> {
            uploadImages(document);
            if (document.getOwnerId().equals(userId)) {
                FirebaseDatabase.getCreatedDocumentsReference(document).setValue(document);
            }
            else {
                FirebaseDatabase.getSharedDocumentsReference(userId, document).setValue(document);
            }
        });
    }

    /**
     * Updates all documents
     * Automatically decides whether updating created or shared documents
     * @param documentList
     */
    public void update(List<Document> documentList) {
        update(documentList.toArray(new Document[0]));
    }

    /**
     * Remove the given document
     * If the user is the owner, the document will be deleted from the database
     * Otherwise the user is removing their access to the document
     * @param document
     */
    public void remove(Document document) {
        DatabaseReference reference = document.getOwnerId().equals(userId) ?
                        FirebaseDatabase.createdDocsRef :
                        FirebaseDatabase.sharedDocsRef;
        reference.child(userId).child(document.getId()).removeValue();
    }

    /**
     * Remove access to an document for an user
     * Only the owner can remove accesses
     * @param document
     * @param userToBeRemoved
     */
    public void removeAccess(Document document, String userToBeRemoved) {
        if (!document.getOwnerId().equals(userId)) return;
        document.getUserIds().remove(userToBeRemoved);
        update(document);
    }

    /**
     * Remove all documents from a user
     */
    public void removeUserDocuments() {
        FirebaseDatabase.createdDocsRef.child(userId).removeValue();
        FirebaseDatabase.sharedDocsRef.child(userId).removeValue();
    }
}