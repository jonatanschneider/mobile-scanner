package de.thm.scanman.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
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

import static de.thm.scanman.persistence.FirebaseDatabase.CREATED_DOCUMENT;
import static de.thm.scanman.persistence.FirebaseDatabase.SHARED_DOCUMENT;
import static de.thm.scanman.persistence.FirebaseDatabase.addImageRef;
import static de.thm.scanman.persistence.FirebaseDatabase.documentDAO;

public class EditDocumentActivity extends AppCompatActivity {

    private static final int EDIT_IMAGE = 1;
    private FloatingActionButton saveFab;
    GridView gridview;
    ImageAdapter ia;
    private Uri addImage = Uri.parse(addImageRef.toString());
    private ImageList<Uri> imagesList = new ImageList<>(addImage);
    private LiveData<Document> liveData;
    private Context context = this;

    private int imageNr;
    private boolean firstVisit;
    private boolean editDocument = false;
    private Document document;

    public static final String FIRST_VISIT = "FirstVisit";

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

                Intent i = new Intent(this, ViewImageActivity.class);
                i.setData(uri);
                startActivityForResult(i, EDIT_IMAGE);
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

            editDocument = true;
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
            List<Document.Image> selectedImages = new ArrayList<>();
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
                            builder.setMessage(R.string.delete_image);
                        } else {
                            builder.setMessage(getResources().getString(R.string.delete_images, selectedImages.size()));
                        }
                        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                            selectedImages.forEach(image -> {
                                imagesList.remove(Uri.parse(image.getFile()));
                                document.getImages().remove(image);
                            });
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
                    Document.Image selectedImage = document.getImages().get(position);
                    if (checked) {
                        gridview.getChildAt(position).setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                        selectedImages.add(selectedImage);
                        selectedPictures++;
                        mode.setTitle(selectedPictures + " " + getResources().getString(R.string.selected));
                    } else {
                        gridview.getChildAt(position).setBackground(null);
                        selectedImages.remove(selectedImage);
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
                    if (document == null) {
                        document = buildDocument();
                    }
                    // add new image
                    imagesList.add(resultUri);
                    document.setImages(buildImages());

                    ia.notifyDataSetChanged(); // updates the adapter
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    System.out.println(result.getError());
                }
            } else {
                System.out.println("result is null!");
            }
        } else if (requestCode == EDIT_IMAGE&& resultCode == RESULT_OK
                && data != null && data.getExtras() != null) {
            Uri resultUri = (Uri) data.getExtras().get("uri");
            // update existing image
            imagesList.update(imageNr, resultUri);
            document.getImages().get(imageNr).setFile(resultUri.toString());
            ia.notifyDataSetChanged(); // updates the adapter
        } else System.out.println("Wrong result in EditDocumentActivity");
        firstVisit = false;
    }

    @Override
    public void onBackPressed() {
        exitDocumentActivity();
    }

    private void saveDocument() {
        if (liveData != null) liveData.removeObservers(this);
        if (editDocument) {
            document.setImages(buildImages());
            document.setLastUpdateAt(new Date().getTime());
            documentDAO.update(document);
        }
        else {
            document = buildDocument();
            documentDAO.addCreatedDocument(document);
        }
    }

    private Document buildDocument() {
        Document document = new Document();
        Date date = new Date();
        document.setCreatedAt(date.getTime());
        String defaultName = new SimpleDateFormat("yyyy_MM_dd HH:mm").format(date) + " Scanman";
        document.setName(defaultName);
        document.setImages(buildImages());
        return document;
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
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
}
