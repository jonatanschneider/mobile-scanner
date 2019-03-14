package de.thm.scanman.util;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

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
        if (position < 0 | position >= imagesList.size()){
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

        // get the display size of the device
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        int SQUARE_SIZE = displaySize.x / 2 - 32;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(SQUARE_SIZE, SQUARE_SIZE));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
                    .error(
                            GlideApp
                                    .with(view.getContext())
                                    .load(R.drawable.ic_camera_alt_black_24dp)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
        }
        else { //Show local image
            view.setImageURI(uri);
        }
    }
}
