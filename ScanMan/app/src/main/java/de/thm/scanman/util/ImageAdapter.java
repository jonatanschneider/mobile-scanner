package de.thm.scanman.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.StorageReference;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.thm.scanman.R;
import de.thm.scanman.persistence.FirebaseDatabase;
import de.thm.scanman.persistence.GlideApp;

/**
 * This class is used to display pictures in an Adapter
 * It is specified for EditDocumentsActivity
 * */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Uri> imagesList;

    public ImageAdapter(Context c, List<Uri> imagesList) {
        mContext = c;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 | position > imagesList.size()){
            return null;
        } else {
            return imagesList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        int SQUARE_SIZE = 480;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(SQUARE_SIZE, SQUARE_SIZE));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else {
            imageView = (ImageView) convertView;
        }

        setImage(imageView, position);
        return imageView;
    }

    private void setImage(ImageView view, int position) {
        Uri uri = imagesList.get(position);
        if (!uri.getScheme().equals("file")) {
            GlideApp.with(mContext)
                    .load(FirebaseDatabase.toStorageReference(uri))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
        }
        else { //Show local image
            view.setImageURI(uri);
        }
    }
}
