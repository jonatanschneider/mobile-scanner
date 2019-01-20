package de.thm.scanman.persistence;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.liveData.UserLiveData;

public class UserDAO {

    /**
     * Add a single user and all it's created and shared Documents to the database.
     * The owner attribute of all createdDocuments will automatically be set to the
     * corresponding user id.
     * @param user
     * @return user with set id
     */
    public User add(User user, Context context) {
        // Remove documents from user object, will be stored independently in other nodes
        List<Document> createdDocuments = user.getCreatedDocuments();
        List<Document> sharedDocuments = user.getSharedDocuments();
        user.setCreatedDocuments(new ArrayList<>());
        user.setSharedDocuments(new ArrayList<>());

        FirebaseDatabase.usersRef.child(user.getId()).setValue(user);

        FirebaseDatabase.documentDAO.addCreatedDocuments(createdDocuments);
        FirebaseDatabase.documentDAO.addSharedDocuments(sharedDocuments);
        return user;
    }

    /**
     * @param userId
     * @return LiveData of a user object with all it's documents
     */
    public LiveData<User> get(String userId) {
        DocumentDAO documentDAO = new DocumentDAO();
        LiveData<List<Document>> createdDocuments = documentDAO.getCreatedDocuments();
        LiveData<List<Document>> sharedDocuments = documentDAO.getSharedDocuments();

        return Transformations.switchMap(getInfo(userId), user -> {
            MediatorLiveData<User> mediator = new MediatorLiveData<>();
            mediator.addSource(createdDocuments, created -> {
                if (created != null && !created.isEmpty()) {
                    user.setCreatedDocuments(created);
                }
                mediator.setValue(user);
            });
            mediator.addSource(sharedDocuments, shared -> {
                if (shared != null && !shared.isEmpty()) {
                    user.setSharedDocuments(shared);
                }
                mediator.setValue(user);
            });
            return mediator;
        });
    }

    /**
     * @param userId
     * @return LiveData of a user object with all it's public info (name, email, createdAt), all other attributes are not set
     */
    public LiveData<User> getInfo(String userId) {
        return new UserLiveData(FirebaseDatabase.usersRef.child(userId));
    }

    /**
     * Update the user and all it's documents
     * @param user
     */
    public void update(User user) {
        updateInfo(user);
        FirebaseDatabase.documentDAO.update(user.getCreatedDocuments());
        FirebaseDatabase.documentDAO.update(user.getSharedDocuments());
    }

    /**
     * Update the users info (name, email, createdAt)
     * @param user
     */
    public void updateInfo(User user) {
        FirebaseDatabase.usersRef.child(user.getId()).setValue(user);
    }

    /**
     * Removes a user and all it's entries in createdDocuments and sharedDocuments
     * @param userId
     */
    public void remove(String userId) {
        FirebaseDatabase.usersRef.child(userId).removeValue();
        FirebaseDatabase.documentDAO.removeUserDocuments();
    }
}