package de.thm.scanman.persistence;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.liveData.UserLiveData;

public class UserDAO {

    public void add(User user) {
        List<Document> createdDocuments = user.getCreatedDocuments();
        List<Document> sharedDocuments = user.getSharedDocuments();
        user.setCreatedDocuments(new ArrayList<>());
        user.setSharedDocuments(new ArrayList<>());
        DatabaseReference reference = FirebaseDatabase.usersRef.push();
        user.setId(reference.getKey());
        reference.setValue(user);
        DocumentDAO documentDAO = new DocumentDAO();
        documentDAO.addCreatedDocuments(user, createdDocuments);
    }

    public LiveData<User> get(String userId, LifecycleOwner owner) {
        return new UserLiveData(FirebaseDatabase.usersRef.child(userId), userId, owner);
    public LiveData<User> getInfo(String userId, LifecycleOwner owner) {
        return new UserLiveData(FirebaseDatabase.usersRef.child(userId));
    }

    public void update(User user) {
        FirebaseDatabase.usersRef.child(user.getId()).setValue(user);
    }

    public void remove(String id) {
        FirebaseDatabase.usersRef.child(id).removeValue();
    }
}