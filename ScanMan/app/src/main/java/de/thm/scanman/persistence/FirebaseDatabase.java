package de.thm.scanman.persistence;

import com.google.firebase.database.DatabaseReference;

public class FirebaseDatabase {
    public UserDAO userDAO;
    public DocumentDAO documentDAO;
    public static final DatabaseReference rootRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    public static final DatabaseReference usersRef = rootRef.child("z_users");
    public static final DatabaseReference documentRef = rootRef.child("z_documents");
    public static final DatabaseReference createdDocsRef = rootRef.child("z_createdDocuments");
    public static final DatabaseReference sharedDocsRef = rootRef.child("z_sharedDocuments");

    public FirebaseDatabase getDb() {
        userDAO = new UserDAO();
        documentDAO = new DocumentDAO();
        return this;
    }
}
