package de.thm.scanman.persistence;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import de.thm.scanman.model.User;

public class UserDAO {

    public void add(User user) {
        DatabaseReference reference = FirebaseDatabase.usersRef.push();
        user.setId(reference.getKey());
        reference.setValue(user);
    }

    public LiveData<User> get(String userId, LifecycleOwner owner) {
        return new UserLiveData(FirebaseDatabase.usersRef.child(userId), userId, owner);
    }

    public void update(User user) {
        FirebaseDatabase.usersRef.child(user.getId()).setValue(user);
    }

    public void remove(String id) {
        FirebaseDatabase.usersRef.child(id).removeValue();
    }
}