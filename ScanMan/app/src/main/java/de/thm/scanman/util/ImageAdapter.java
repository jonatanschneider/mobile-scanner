package de.thm.scanman.util;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

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
        int SQUARE_SIZE = 480;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(SQUARE_SIZE, SQUARE_SIZE));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageURI(imagesList.get(position));
        return imageView;
    }
}
