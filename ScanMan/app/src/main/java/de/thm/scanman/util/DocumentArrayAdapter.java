package de.thm.scanman.util;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.FirebaseDatabase;
import de.thm.scanman.persistence.GlideApp;

public class DocumentArrayAdapter extends ArrayAdapter<Document> {
    private final static int VIEW_RESOURCE = R.layout.document_list_item;

    public DocumentArrayAdapter(Context ctx, List<Document> documents) {
        super(ctx, VIEW_RESOURCE, documents);
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(VIEW_RESOURCE, null);
        }

        Document d = getItem(pos);
        Uri uri = Uri.parse("");
        if (d.getImages().size() > 0) uri = Uri.parse(d.getImages().get(0).getFile());
        ImageView image = view.findViewById(R.id.document_image);
        GlideApp.with(view.getContext())
                .load(FirebaseDatabase.toStorageReference(uri))
                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                .error(
                        GlideApp
                                .with(view.getContext())
                                .load(R.drawable.ic_camera_alt_black_24dp)
                                .override(150)
                                .centerCrop()
                )
                .override(150)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(image);

        TextView name = view.findViewById(R.id.name);
        name.setText(d.getName());

        TextView subtext = view.findViewById(R.id.subtext);
        StringBuilder dateAndTags = new StringBuilder();
        dateAndTags.append(new Date(d.getCreatedAt()).toString());
        dateAndTags.append("\n");
        for(String s : d.getTags()) {
            dateAndTags.append(s);
            dateAndTags.append(", ");
        }
        if(dateAndTags.toString().endsWith(", ")) {
            dateAndTags.delete(dateAndTags.length() - 2, dateAndTags.length());
        }
        else {
            dateAndTags.append("-");
        }
        subtext.setText(dateAndTags.toString());

        return view;
    }

    @Override
    public void sort(@NonNull Comparator<? super Document> comparator){
        int i = this.getCount();
        for (int j = 0; j < i; j++) {
            System.out.println("lolol" + getItem(j));
        }

        List<Document> list = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            list.add(getItem(j));
        }
        list.sort(comparator);
        clear();
        addAll(list);

        for (int j = 0; j < i; j++) {
            System.out.println("lolol" + getItem(j));
        }
    }
}
