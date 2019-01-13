package de.thm.scanman.persistence;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.liveData.DocumentLiveData;

public class DocumentDAO {
    private String userId = FirebaseAuth.getInstance().getUid();

    /**
     * Add the document into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     * @param document
     */
    public void addCreatedDocument(Document document, Context context) {
        document.setOwnerId(userId);
        DatabaseReference documentRef = FirebaseDatabase.createdDocsRef.child(userId).push();
        document.setId(documentRef.getKey());
        copyImagesToFilesDir(document, context);
        uploadImages(document);

        /*
        We need to copy our local uris temporarily because otherwise firebase would upload
        the uri to the database, which will result in an error at CustomClassMapper because
        firebase can't handle URI type
         */
        List<Document.Image> images = document.getImages();
        List<Uri> uriList = images.stream().map(Document.Image::getLocalUri).collect(Collectors.toList());
        document.getImages().forEach(image -> image.setLocalUri(null));
        documentRef.setValue(document);
        IntStream.range(0, images.size()).forEach(i -> images.get(i).setLocalUri(uriList.get(i)));

    }

    /**
     * Add all documents from the list into the createdDocuments node
     * Will also set the documents ownerId to the corresponding user id
     * @param documentList
     */
    public void addCreatedDocuments(List<Document> documentList, Context context) {
        documentList.forEach(document -> addCreatedDocument(document, context));
    }

    private void copyImagesToFilesDir(Document document, Context context) {
        for (int i = 0; i < document.getImages().size(); i++) {

            Document.Image image = document.getImages().get(i);

            //File: filesDir/documentId/i.jpeg
            File dest = new File(context.getFilesDir(), document.getId()+ File.separator + i + ".jpeg");
            File source = new File(image.getLocalUri().getPath());

            try {
                FileUtils.copyFile(source, dest);
                image.setLocalUri(Uri.parse(dest.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImages(Document document) {
        for (int i = 0; i < document.getImages().size(); i++) {
            Document.Image image = document.getImages().get(i);
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("documents").child(document.getId()).child(""+ i);
            image.setStorageUri(storage.toString());

            UploadTask uploadTask = storage.putFile(Uri.parse("file://" + image.getLocalUri()));
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

    LiveData<List<Document>> getCreatedDocuments() {
        return new DocumentLiveData(FirebaseDatabase.createdDocsRef.child(userId));
    }

    LiveData<List<Document>> getSharedDocuments() {
        return new DocumentLiveData(FirebaseDatabase.sharedDocsRef.child(userId));
    }

    /**
     * Updates all documents
     * Automatically decides whether updating created or shared documents
     * @param documents single document or an array of documents
     */
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