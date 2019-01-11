package de.thm.scanman.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.thm.scanman.R;
import de.thm.scanman.util.ImageAdapter;
import de.thm.scanman.util.ImageList;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class EditDocumentActivity extends AppCompatActivity {

    private FloatingActionButton saveFab;
    GridView gridview;
    ImageAdapter ia;
    private Uri addImage = Uri.parse("android.resource://de.thm.scanman/drawable/ic_add_circle_outline_black_24dp");
    private ImageList<Uri> imagesList = new ImageList<>(addImage);

    private int imageNr;
    private boolean firstVisit;

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
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            if (id == ia.getCount() - 1) {  // click on addButton
                shootNewImage();
            } else {                        // click on real image
                imageNr = position;
                Uri selectedImage = (Uri)ia.getItem(position);
                CropImage.activity(selectedImage)
                        .start(this);
            }
        });

        Uri data = getIntent().getData();
        if (data != null && data.toString().equals(FIRST_VISIT)) {
            firstVisit = true;
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
                    } else {                                        // update existing image
                        imagesList.update(imageNr, resultUri);
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
        // TODO implement saveDocument / implement liveData
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
