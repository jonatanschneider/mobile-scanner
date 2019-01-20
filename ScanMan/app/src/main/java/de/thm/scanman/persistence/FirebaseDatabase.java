package de.thm.scanman.persistence;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.thm.scanman.model.Document;

public class FirebaseDatabase {
    public static final UserDAO userDAO = new UserDAO();
    public static final DocumentDAO documentDAO = new DocumentDAO();
    public static final DatabaseReference rootRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    public static final DatabaseReference usersRef = rootRef.child("users");
    public static final DatabaseReference createdDocsRef = rootRef.child("createdDocuments");
    public static final DatabaseReference sharedDocsRef = rootRef.child("sharedDocuments");

    public static final StorageReference documentStorageRef = FirebaseStorage.getInstance().getReference().child("documents");
    public static final StorageReference addImageRef = documentStorageRef.child("add_image.png");

    public static DatabaseReference getCreatedDocumentsReference(Document document) {
        return createdDocsRef.child(document.getOwnerId()).child(document.getId());
    }

    public static DatabaseReference getSharedDocumentsReference(String userId, Document document) {
        return sharedDocsRef.child(userId).child(document.getId());
    }

    public static StorageReference toStorageReference(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        for (String segment : uri.getPathSegments()) {
            reference = reference.child(segment);
        }
        return reference;
    }
}
