package de.thm.scanman.persistence;

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

    public static final StorageReference documentStorage = FirebaseStorage.getInstance().getReference().child("documents");

    public static DatabaseReference getCreatedDocumentsReference(Document document) {
        return createdDocsRef.child(document.getOwnerId()).child(document.getId());
    }

    public static DatabaseReference getSharedDocumentsReference(String userId, Document document) {
        return sharedDocsRef.child(userId).child(document.getId());
    }
}
