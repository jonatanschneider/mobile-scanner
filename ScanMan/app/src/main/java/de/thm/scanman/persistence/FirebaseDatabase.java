package de.thm.scanman.persistence;

import com.google.firebase.database.DatabaseReference;

public class FirebaseDatabase {
    public static final UserDAO userDAO = new UserDAO();
    public static final DocumentDAO documentDAO = new DocumentDAO();
    public static final DatabaseReference rootRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    public static final DatabaseReference usersRef = rootRef.child("users");
    public static final DatabaseReference createdDocsRef = rootRef.child("createdDocuments");
    public static final DatabaseReference sharedDocsRef = rootRef.child("sharedDocuments");
}
