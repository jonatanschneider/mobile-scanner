package de.thm.scanman.persistence.liveData;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import de.thm.scanman.model.Document;

public class DocumentListLiveData extends LiveData<List<Document>> {
    private Query query;
    private final UserListener userListener = new UserListener();
    private List<Document> documentList = new ArrayList<>();

    private class UserListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> it = dataSnapshot.getChildren();
            documentList.clear();
            for(DataSnapshot ds: it) {
                Document document = ds.getValue(Document.class);
                document.setId(ds.getKey());
                documentList.add(document);
            }
            setValue(documentList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("DocumentListLiveData","DatabaseError: " + databaseError.getMessage());
        }
    }

    public DocumentListLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(userListener);
        Log.d("LiveData", "Document Live Data connected");
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(userListener);
        Log.d("LiveData", "Document Live Data disconnected");
    }
}