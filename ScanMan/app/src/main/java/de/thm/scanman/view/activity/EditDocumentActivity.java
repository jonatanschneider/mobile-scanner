package de.thm.scanman.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import de.thm.scanman.R;
import de.thm.scanman.util.ImageAdapter;
import de.thm.scanman.util.ImageList;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditDocumentActivity extends AppCompatActivity {

    private FloatingActionButton saveFab;
    GridView gridview;
    ImageAdapter ia;
    private Uri addImage = Uri.parse("android.resource://de.thm.scanman/drawable/ic_add_circle_outline_black_24dp");
    private ImageList<Uri> imagesList = new ImageList<>(addImage);
    private Context context = this;

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
        ia = new ImageAdapter(this, imagesList.getList(true));
        gridview.setAdapter(ia);

        // onClickListener for editing
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
    protected void onResume() {
        super.onResume();

        // setMultiChoice Listener
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            List<Uri> selectedImages = new ArrayList<>();
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
                            ia.notifyDataSetChanged();
                        });
                        builder.setNeutralButton(R.string.cancel, null);
                        builder.show();
                        mode.finish();
                        return true;
                    case R.id.menu_share:
                        sendImage(selectedImages);
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
                        selectedPictures++;
                        mode.setTitle(selectedPictures + " " + getResources().getString(R.string.selected));
                    } else {
                        gridview.getChildAt(position).setBackground(null);
                        selectedImages.remove(uri);
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
                if (imagesList.size() >= 1) {
                    shareDocument(FirebaseAuth.getInstance().getUid());       // send one or more photos
                    return true;
                }
                else return true;
            case R.id.action_export:
                if (imagesList.size() >= 1) {
                    sendImage(imagesList.getList(false));       // send one or more photos
                    return true;
                }
                else return true;                       // there are no photos to send
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

    /**
     * Starts intent to send all pictures addressed by uris in list
     * @param urisIn contains addresses of pictures to send
     */
    private void sendImage(List<Uri> urisIn) {
        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> urisOut = new ArrayList<>();
        urisIn.forEach(uri -> {
            Uri contentUri = convertToContent(uri);
            urisOut.add(contentUri);
        });
        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urisOut);
        i.setType("image/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(i, getResources().getText(R.string.send_with)));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.action_not_possible + i.getType());
            builder.setMessage(R.string.no_application);
            builder.setNeutralButton(R.string.cancel, (dialog, which) -> { });
            builder.show();
        }
    }

    private Uri convertToContent(Uri uri) {
        File f = new File(uri.getPath());
        String authority = context.getApplicationContext().getPackageName() + ".de.thm.scanman.provider";
        return FileProvider.getUriForFile(context, authority, f);
    }

    private void shareDocument(String uid) {
        // TODO ask if document is saved -> save document
        uid = "F0LLCpoaaXXVROa1DeNzfYUXIpk2";
        String documentID = "-LVNVfwik49cXLoKESd1";
        String uri = "scanman://" + uid + "/" + documentID;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.your_link);
        builder.setMessage(uri);
        builder.setPositiveButton(R.string.send_with, (dialog, which) -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, uri);
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, getResources().getText(R.string.send_with)));
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> { });
        builder.show();
        // TODO solver
    }
}
