package de.thm.scanman.persistence.liveData;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.thm.scanman.model.User;

public class UserLiveData extends LiveData<User> {
    private Query query;

    private final UserListener userListener = new UserListener();

    private class UserListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            user.setId(dataSnapshot.getKey());
            //Auth:to check if user has rights to see documents
            setValue(user);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("UserLiveData", "DatabaseError: " + databaseError.getDetails());
        }
    }

    public UserLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(userListener);
        Log.d("LiveData", "User Live Data connected");
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(userListener);
        Log.d("LiveData", "User Live Data disconnected");
    }
}