package de.thm.scanman.persistence;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;

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

        FirebaseDatabase.documentDAO.addCreatedDocuments(user, createdDocuments);
    }


    public LiveData<User> get(String userId, LifecycleOwner owner) {
        DocumentDAO documentDAO = new DocumentDAO();
        LiveData<List<Document>> createdDocuments = documentDAO.get(FirebaseDatabase.createdDocsRef.child(userId), owner);
        LiveData<List<Document>> sharedDocuments = documentDAO.get(FirebaseDatabase.sharedDocsRef.child(userId), owner);

        return Transformations.switchMap(getInfo(userId), user -> {
            MediatorLiveData<User> mediator = new MediatorLiveData<>();
            mediator.addSource(createdDocuments, created -> {
                if (created == null || created.isEmpty()) return;
                user.setCreatedDocuments(created);
                mediator.setValue(user);
            });
            mediator.addSource(sharedDocuments, shared -> {
                if (shared == null || shared.isEmpty()) return;
                user.setCreatedDocuments(shared);
                mediator.setValue(user);
            });
            return mediator;
        });
    }

    public LiveData<User> getInfo(String userId) {
        return new UserLiveData(FirebaseDatabase.usersRef.child(userId));
    }

    public void update(User user) {
        FirebaseDatabase.usersRef.child(user.getId()).setValue(user);
    }

    public void remove(String id) {
        FirebaseDatabase.usersRef.child(id).removeValue();
    }
}