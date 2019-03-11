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

/**
 * This class is used to display documents in a ListView with title, date,
 * tags and the first image and to perform a custom filtering. Several methods of
 * ArrayAdapter are overwritten here to can do that.
 * It is specified for DocumentsListsActivity.
 */
public class DocumentArrayAdapter extends ArrayAdapter<Document> implements Filterable {
    private final static int VIEW_RESOURCE = R.layout.document_list_item;

    /**
     * Lock used to modify the content of {@link #docObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();

    private final LayoutInflater inflater;

    /**
     * Contains the list of objects that represent the data of this DocumentArrayAdapter.
     */
    private List<Document> docObjects;

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called whenever
     * {@link #docObjects} is modified.
     */
    private boolean notifyOnChange = true;

    // A copy of the original docObjects array, initialized from and then used instead as soon as
    // the filter CustomFilter is used. docObjects will then only contain the filtered values.
    private ArrayList<Document> originalValues;
    private CustomFilter filter;

    /**
     * Constructor
     *
     * @param ctx The current context.
     * @param documents The documents to represent in the ListView.
     */
    public DocumentArrayAdapter(Context ctx, List<Document> documents) {
        super(ctx, VIEW_RESOURCE, documents);
        this.docObjects = new ArrayList<>(documents);
        inflater = LayoutInflater.from(ctx);
    }

    /**
     * Adds the specified document at the end of the array.
     *
     * @param document The document to add at the end of the array.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void add(Document document) {
        synchronized (mLock) {
            if (originalValues != null) {
                originalValues.add(document);
            } else {
                docObjects.add(document);
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
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
            if (originalValues != null) {
                originalValues.addAll(collection);
            } else {
                docObjects.addAll(collection);
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
    }

    /**
     * Adds the specified documents at the end of the array.
     *
     * @param documents The documents to add at the end of the array.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void addAll(Document ... documents) {
        synchronized (mLock) {
            if (originalValues != null) {
                Collections.addAll(originalValues, documents);
            } else {
                Collections.addAll(docObjects, documents);
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
    }

    /**
     * Inserts the specified document at the specified index in the array.
     *
     * @param document The document to insert into the array.
     * @param index The index at which the document must be inserted.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void insert(Document document, int index) {
        synchronized (mLock) {
            if (originalValues != null) {
                originalValues.add(index, document);
            } else {
                docObjects.add(index, document);
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
    }

    /**
     * Removes the specified document from the array.
     *
     * @param document The document to remove.
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void remove(Document document) {
        synchronized (mLock) {
            if (originalValues != null) {
                originalValues.remove(document);
            } else {
                docObjects.remove(document);
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     *
     * @throws UnsupportedOperationException if the underlying data collection is immutable
     */
    public void clear() {
        synchronized (mLock) {
            if (originalValues != null) {
                originalValues.clear();
            } else {
                docObjects.clear();
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(Comparator<? super Document> comparator) {
        synchronized (mLock) {
            if (originalValues != null) {
                Collections.sort(originalValues, comparator);
            } else {
                Collections.sort(docObjects, comparator);
            }
        }
        if (notifyOnChange) notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return docObjects.size();
    }

    @Override
    public Document getItem(int position) {
        return docObjects.get(position);
    }

    /**
     * Returns the position of the specified document in the array.
     *
     * @param document The document to retrieve the position of.
     *
     * @return The position of the specified document.
     */
    public int getPosition(Document document) {
        return docObjects.indexOf(document);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = inflater.inflate(VIEW_RESOURCE, null);
        } else {
            view = convertView;
        }

        Document d = getItem(position);
        Uri uri = Uri.parse("");
        if (d.getImages().size() > 0) uri = Uri.parse(d.getImages().get(0).getFile());
        ImageView image = view.findViewById(R.id.document_image);
        // load image into ImageView view
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

        // set name of the document into TextView name
        TextView name = view.findViewById(R.id.name);
        name.setText(d.getName());

        // set create date and tags of the document into TextView subtext
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
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    /**
     * <p>This custom filter constrains the content of the document array adapter
     * with a substring. Each item that does not contains the supplied substring
     * is removed from the list.</p>
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            if (originalValues == null) {
                synchronized (mLock) {
                    originalValues = new ArrayList<>(docObjects);
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
                final String constraintString = constraint.toString().toLowerCase();
                final List<Document> list;
                synchronized (mLock) {
                    list = new ArrayList<>(originalValues);
                }
                final ArrayList<Document> newValues = new ArrayList<>();
                boolean tagContainsConstraint;

                // get specific items from the document list
                for(int i = 0; i < list.size(); i++) {
                    final Document doc = list.get(i);
                    final String documentName = doc.getName().toLowerCase();
                    tagContainsConstraint = false;

                    // search constraint in the tags of the document ...
                    for(String tag : doc.getTags()) {
                        if (tag.toLowerCase().contains(constraintString)) {
                            tagContainsConstraint = true;
                            break;
                        }
                    }
                    // ... and search constraint in the name of the document
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
            docObjects = (List<Document>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}