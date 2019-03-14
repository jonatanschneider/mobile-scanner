package de.thm.scanman.persistence;

import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {
    private String userId = FirebaseAuth.getInstance().getUid();

    /**
     * Add the document into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     *
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
     *
     * @param documentList
     */
    public void addCreatedDocuments(List<Document> documentList) {
        documentList.forEach(this::addCreatedDocument);
    }

    private void uploadImages(Document document) {
        document.getImages().stream()
                .filter(isLocalFile)
                .forEach(image -> {
                    Uri uri = Uri.parse(image.getFile());

                    StorageReference reference = FirebaseDatabase.documentStorageRef
                            .child(document.getId())
                            .child(uri.getLastPathSegment());

                    image.setFile(reference.toString());
                    image.setFileSize(calculateFileSize(uri));

                    UploadTask uploadTask = reference.putFile(uri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        System.out.println("upload success");
                    }).addOnFailureListener(exception -> {
                        System.out.println("upload failed");
                    });
                });
    }

    private Predicate<Document.Image> isLocalFile = image -> Uri.parse(image.getFile()).getScheme().equals("file");

    private long calculateFileSize(Uri uri) {
        File file = new File(uri.getPath());
        return file.length();
    }


    /**
     * Add document into the sharedDocuments node and shows success toast if set
     *
     * @param document
     * @param successToast Show toast on success
     * @param failToast Show toast on fail
     */
    public void addSharedDocument(Document document, Optional<Toast> successToast, Optional<Toast> failToast) {
        if (userId.equals(document.getOwnerId())) return;
        if (document.getId() == null || document.getId().equals("")) return;
        DatabaseReference reference = FirebaseDatabase.sharedDocsRef.child(userId).child(document.getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    failToast.ifPresent(Toast::show);
                    return;
                }
                reference.setValue(document);
                successToast.ifPresent(Toast::show);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Add document into the sharedDocuments node
     * @param document
     */
    public void addSharedDocument(Document document) {
        addSharedDocument(document, Optional.empty(), Optional.empty());
    }

    /**
     * Add all documents from the list into the sharedDocuments node
     *
     * @param documentList
     */
    public void addSharedDocuments(List<Document> documentList) {
        documentList.forEach(this::addSharedDocument);
    }

    /**
     * LiveData for a single created document
     *
     * @param documentId
     * @return
     */
    public LiveData<Document> getCreatedDocument(String documentId) {
        return new DocumentLiveData(FirebaseDatabase.createdDocsRef.child(userId).child(documentId));
    }

    /**
     * LiveData for a single shared document
     *
     * @param documentId
     * @return
     */
    public LiveData<Document> getSharedDocument(String documentId) {
        return new DocumentLiveData(FirebaseDatabase.sharedDocsRef.child(userId).child(documentId));
    }

    /**
     * Updates all documents
     * Automatically decides whether updating created or shared documents
     *
     * @param documents single document or an array of documents
     */
    public void update(Document... documents) {
        Arrays.asList(documents).forEach(document -> {
            uploadImages(document);
            if (document.getOwnerId().equals(userId)) {
                FirebaseDatabase.getCreatedDocumentsReference(document).setValue(document);
            } else {
                FirebaseDatabase.getSharedDocumentsReference(userId, document).setValue(document);
            }
        });
    }

    /**
     * Updates all documents
     * Automatically decides whether updating created or shared documents
     *
     * @param documentList
     */
    public void update(List<Document> documentList) {
        update(documentList.toArray(new Document[0]));
    }

    /**
     * Remove the given document
     * If the user is the owner, the document will be deleted from the database
     * Otherwise the user is removing their access to the document
     *
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
     *
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