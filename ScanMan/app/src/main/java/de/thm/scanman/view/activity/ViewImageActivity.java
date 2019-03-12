package de.thm.scanman.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.thm.scanman.R;
import de.thm.scanman.persistence.FirebaseDatabase;
import de.thm.scanman.persistence.GlideApp;

public class ViewImageActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView image;
    private Uri uri;
    private boolean wasEdited = false;

    private static final String TAG = "Touch";
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF start = new PointF();
    private static PointF mid = new PointF();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static int mode = NONE;

    private float oldDist;

    private boolean firstTouch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        // Show up button in action bar
        Optional.ofNullable(getSupportActionBar())
                .ifPresent(
                        actionBar -> actionBar.setDisplayHomeAsUpEnabled(true)
                );

        uri = getIntent().getData();
        image = findViewById(R.id.image);

        if (uri.getScheme().equals("file")) {
            image.setImageURI(uri);
        } else {
            GlideApp.with(this)
                    .load(FirebaseDatabase.toStorageReference(uri))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(image);
        }
        image.setOnTouchListener(this);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        firstTouch = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit:
                Activity activity = this;
                wasEdited = true;
                if (uri.getScheme().equals("file")) {
                    CropImage.activity(uri).start(activity);
                } else {
                    GlideApp.with(this)
                            .asFile()
                            .load(FirebaseDatabase.toStorageReference(uri))
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(new SimpleTarget<File>() {
                                @Override
                                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                    CropImage.activity(Uri.parse(resource.toURI().toString())).start(activity);
                                }
                            });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for zooming in and out and moving an image.
     *
     * @param v The ImageView which contains the image.
     * @param event Object used to report movement (mouse, pen, finger, trackball) events.
     * @return true if event was handled.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;

        if (firstTouch) {
            view.setScaleType(ImageView.ScaleType.MATRIX);
            float imageWidth = image.getDrawable().getIntrinsicWidth();
            float imageHeight = image.getDrawable().getIntrinsicHeight();
            RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
            RectF viewRect = new RectF(0, 0, image.getWidth(),
                    image.getHeight());
            matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
            savedMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

            view.setImageMatrix(savedMatrix);
            firstTouch = false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {

                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                }
                else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
        }
        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    /**
     * Calculates the distance between two fingers on the screen.
     *
     * @param event Object used to report movement (mouse, pen, finger, trackball) events.
     * @return The distance between two fingers.
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculates the midpoint (two coordinates) between two points.
     *
     * @param point The variable in which the resultant point is written.
     * @param event Object used to report movement (mouse, pen, finger, trackball) events.
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null){
                if (resultCode == RESULT_OK) {
                    uri = result.getUri();
                    image.setImageURI(uri);
                    // image.
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    System.out.println(result.getError());
                }
            } else {
                System.out.println("result is null!");
            }
        }
    }

    //TODO when "wasEdited" update picture in EditDocumentsActivity
}
