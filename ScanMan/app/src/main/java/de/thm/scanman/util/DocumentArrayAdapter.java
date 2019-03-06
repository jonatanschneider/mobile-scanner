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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.persistence.FirebaseDatabase;
import de.thm.scanman.persistence.GlideApp;

public class DocumentArrayAdapter extends ArrayAdapter<Document> implements Filterable {
    private final static int VIEW_RESOURCE = R.layout.document_list_item;

    private final Object mLock = new Object();
    private final LayoutInflater mInflater;

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    private List<Document> mObjects;

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    private ArrayList<Document> mOriginalValues;
    private CustomFilter mFilter;

    public DocumentArrayAdapter(Context ctx, List<Document> documents) {
        super(ctx, VIEW_RESOURCE, documents);
        this.mObjects = new ArrayList<>(documents);
        mInflater = LayoutInflater.from(ctx);
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void add(Document object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            } else {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this list
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this list does not permit null
     *         elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this list
     */
    public void addAll(List<Document> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void addAll(Document ... items) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.addAll(mOriginalValues, items);
            } else {
                Collections.addAll(mObjects, items);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index The index at which the object must be inserted.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void insert(Document object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            } else {
                mObjects.add(index, object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void remove(Document object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            } else {
                mObjects.remove(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     *
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(Comparator<? super Document> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Document getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     *
     * @return The position of the specified item.
     */
    public int getPosition(Document item) {
        return mObjects.indexOf(item);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = mInflater.inflate(VIEW_RESOURCE, null);
        } else {
            view = convertView;
        }

        Document d = getItem(position);
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
    public @NonNull Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CustomFilter();
        }
        return mFilter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (constraint == null || constraint.length() == 0) {
                final List<Document> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            }
            else {
                final String constraintString = constraint.toString().toLowerCase();
                final List<Document> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                final ArrayList<Document> newValues = new ArrayList<>();
                boolean tagContainsConstraint;

                // get specific items
                for(int i = 0; i < list.size(); i++) {
                    final Document doc = list.get(i);
                    final String documentName = doc.getName().toLowerCase();
                    tagContainsConstraint = false;

                    for(String tag : doc.getTags()) {
                        if (tag.toLowerCase().contains(constraintString)) {
                            tagContainsConstraint = true;
                            break;
                        }
                    }
                    if(documentName.toLowerCase().contains(constraintString) || tagContainsConstraint) {
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
            //noinspection unchecked
            mObjects = (List<Document>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}