package de.thm.scanman.util;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.FirebaseDatabase;
import de.thm.scanman.persistence.GlideApp;

public class DocumentArrayAdapter extends ArrayAdapter<Document> implements Filterable {
    private final static int VIEW_RESOURCE = R.layout.document_list_item;
    private CustomFilter filter;
    private List<Document> originalValues;
    private List<Document> documents;

    public DocumentArrayAdapter(Context ctx, List<Document> documents) {
        super(ctx, VIEW_RESOURCE, documents);
        this.documents = new ArrayList<>(documents);
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

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    private final Object mLock = new Object();

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            if (originalValues == null) {
                synchronized (mLock) {
                    originalValues = new ArrayList<>(documents);
                }
            }

            if (constraint == null || constraint.length() == 0) {
                final List<Document> list;
                synchronized (mLock) {
                    list = new ArrayList<>(originalValues);
                }
                results.values = list;
                results.count = list.size();
            }
            else {
                final String constraintString = constraint.toString().toUpperCase();
                final List<Document> list;
                synchronized (mLock) {
                    list = new ArrayList<>(originalValues);
                }
                final ArrayList<Document> newValues = new ArrayList<>();
                boolean tagContainsConstraint;

                // get specific items
                for(int i = 0; i < list.size(); i++) {
                    final Document doc = list.get(i);
                    final String documentName = doc.getName().toUpperCase();
                    tagContainsConstraint = false;

                    for(String tag : doc.getTags()) {
                        if (tag.toUpperCase().contains(constraintString)) {
                            tagContainsConstraint = true;
                            break;
                        }
                    }
                    if(documentName.toUpperCase().contains(constraintString) || tagContainsConstraint) {
                        newValues.add(doc);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            documents = (List<Document>) results.values;
            clear();
            addAll(documents);
        }
    }
}
