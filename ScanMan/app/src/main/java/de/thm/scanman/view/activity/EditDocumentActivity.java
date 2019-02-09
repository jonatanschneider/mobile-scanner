package de.thm.scanman.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.FirebaseDatabase;
import de.thm.scanman.persistence.GlideApp;
import de.thm.scanman.util.ImageAdapter;
import de.thm.scanman.util.ImageList;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import static de.thm.scanman.persistence.FirebaseDatabase.CREATED_DOCUMENT;
import static de.thm.scanman.persistence.FirebaseDatabase.SHARED_DOCUMENT;
import static de.thm.scanman.persistence.FirebaseDatabase.addImageRef;
import static de.thm.scanman.persistence.FirebaseDatabase.documentDAO;

import java.util.ArrayList;
import java.util.List;

public class EditDocumentActivity extends AppCompatActivity {

    private FloatingActionButton saveFab;
    GridView gridview;
    ImageAdapter ia;
    private Uri addImage = Uri.parse(addImageRef.toString());
    private ImageList<Uri> imagesList = new ImageList<>(addImage);
    private LiveData<Document> liveData;
    private Context context = this;

    private int imageNr;
    private boolean firstVisit;
    private Document document;

    public static final String FIRST_VISIT = "FirstVisit";
    private static final int DEFAULT_IMAGE_NR = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);

        ActionBar ac = getSupportActionBar();
        if(ac != null) {
            ac.setDisplayHomeAsUpEnabled(true);
        }

        saveFab = findViewById(R.id.check_fab);

        saveFab.setOnClickListener(v -> {
            saveDocument();
            finish();
        });

        gridview = findViewById(R.id.grid_view);
        ia = new ImageAdapter(this, imagesList.getList());
        gridview.setAdapter(ia);

        // onClickListener for editing
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            if (id == ia.getCount() - 1) {  // click on addButton
                shootNewImage();
            } else {                        // click on real image
                imageNr = position;
                Uri uri = imagesList.get(position);

                if (uri.getScheme().equals("file")) {
                    CropImage.activity(uri).start(this);
                }
                else {
                    Activity activity = this;
                    GlideApp.with(this)
                            .asFile()
                            .load(FirebaseDatabase.toStorageReference(uri))
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(new SimpleTarget<File>() {
                                @Override
                                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                    //TODO Start Activity to View Image
                                    CropImage.activity(Uri.parse(resource.toURI().toString())).start(activity);
                                }
                            });
                }
            }
        });

        Uri data = getIntent().getData();
        if (data != null && data.toString().equals(FIRST_VISIT)) {
            firstVisit = true;
        } else if (data != null) { //Data is document id
            int documentType = getIntent().getIntExtra("documentType", -1);
            if (documentType == CREATED_DOCUMENT) liveData = documentDAO.getCreatedDocument(data.toString());
            else if (documentType == SHARED_DOCUMENT) liveData = documentDAO.getSharedDocument(data.toString());
            else if (documentType == -1) return;

            liveData.observeForever(doc -> {
                document = doc;
                document.getImages().forEach(image -> {
                    StorageReference reference = FirebaseDatabase.toStorageReference(Uri.parse(image.getFile()));
                    imagesList.add(Uri.parse(reference.toString()));
                });
                ia.notifyDataSetChanged();

            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firstVisit) {   // There is no "real" image
            shootNewImage();
        }
    }

    @Override
    protected void onDestroy() {
        if (liveData != null) liveData.removeObservers(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // setMultiChoice Listener
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            List<Uri> selectedImages = new ArrayList<>();
            List<Integer> selectedPositions = new ArrayList<>();
            Integer selectedPictures = 0;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.delete_menu, menu);
                imagesList.hideAddImage();
                selectedImages.clear();
                saveFab.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.delete);
                        if (selectedImages.size() == 1) {
                            builder.setMessage("Bild löschen?");
                        } else {
                            builder.setMessage(selectedImages.size() + " Bilder löschen?");
                        }
                        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                            selectedImages.forEach(image -> imagesList.remove(image));
                            selectedPositions.stream()
                                    .map(pos -> document.getImages().get(pos))
                                    .forEach(image -> document.getImages().remove(image));
                            ia.notifyDataSetChanged();
                        });
                        builder.setNeutralButton(R.string.cancel, null);
                        builder.show();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Object selected = ia.getItem(position);
                if (selected != null && selected.getClass().toString().contains("Uri")) {
                    Uri uri = (Uri) selected;
                    if (checked) {
                        gridview.getChildAt(position).setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                        selectedImages.add(uri);
                        selectedPositions.add(position);
                        selectedPictures++;
                        mode.setTitle(selectedPictures + " " + getResources().getString(R.string.selected));
                    } else {
                        gridview.getChildAt(position).setBackground(null);
                        selectedImages.remove(uri);
                        selectedPositions.remove((Object)position); //cast to object to avoid using the position as index
                        selectedPictures--;
                        mode.setTitle(selectedPictures + " " + getResources().getString(R.string.selected));
                    }
                } else {
                    mode.finish();
                    saveFab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked
                imagesList.showAddImage();
                for (int i = 0; i < gridview.getChildCount(); i++) {
                    gridview.getChildAt(i).setBackground(null);
                }
                selectedPictures = 0;
                ia.notifyDataSetChanged();
                saveFab.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_document, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitDocumentActivity();
                return true;
            case R.id.action_share:
                // implement call for sharing documents here
                return true;
            case R.id.action_export:
                // implement call for exporting documents here
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null){
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();

                    if (imageNr == DEFAULT_IMAGE_NR){               // add new image
                        imagesList.add(resultUri);
                        document.setImages(buildImages());
                    } else {                                        // update existing image
                        imagesList.update(imageNr, resultUri);
                        document.getImages().get(imageNr).setFile(resultUri.toString());
                    }
                    ia.notifyDataSetChanged(); // updates the adapter
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    System.out.println(result.getError());
                }
            } else {
                System.out.println("result is null!");
            }
        }
        firstVisit = false;
    }

    @Override
    public void onBackPressed() {
        exitDocumentActivity();
    }

    private void saveDocument() {
        if(liveData != null) liveData.removeObservers(this);
        if (firstVisit) {
            buildDocument();
            documentDAO.addCreatedDocument(document);
        }
        else {
            document.setImages(buildImages());
            document.setLastUpdateAt(new Date().getTime());
            documentDAO.update(document);
        }
    }

    private void buildDocument() {
        document.setCreatedAt(new Date().getTime());
        document.setName("Upload Test");
        document.setImages(buildImages());
    }

    private List<Document.Image> buildImages() {
        List<Document.Image> images = new ArrayList<>();
        for(int i = 0; i < imagesList.size(); i++) {
            Uri uri = imagesList.get(i);
            if (uri.equals(addImage)) continue;
            if (!uri.getScheme().equals("file")) {
                images.add(document.getImages().get(i));
            } else {
                Document.Image image = new Document.Image(uri.toString(), new Date().getTime());
                image.setId("" + i);
                images.add(image);
            }
        }
        return images;
    }

    private void exitDocumentActivity() {
        if (imagesList.isEmpty()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.exitEditDocument);
            builder.setMessage(R.string.saveDocuments);
            builder.setNeutralButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                saveDocument();
                finish();
            });
            builder.setNegativeButton(R.string.no, (e, r) -> finish());
            builder.show();
        }
    }
    
    private void shootNewImage() {
        imageNr = DEFAULT_IMAGE_NR;   // -> adds image in onActivityResult
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
}
