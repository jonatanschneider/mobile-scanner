package de.thm.scanman.persistence.liveData;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ScanmanLiveData extends LiveData<DataSnapshot> {
    private Query query;
    private final EventListener listener = new EventListener();
    private class EventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("LiveData","DatabaseError: " + databaseError);
        }
    }

    public ScanmanLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(listener);
    }
}
