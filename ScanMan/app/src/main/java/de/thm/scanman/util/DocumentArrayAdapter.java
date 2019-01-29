package de.thm.scanman.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.thm.scanman.R;
import de.thm.scanman.model.Document;

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
        ImageView image = view.findViewById(R.id.document_image);
        image.setImageResource(R.mipmap.ic_launcher);  // TODO: d.getImages().get(0).getFile() statt icon

        TextView name = view.findViewById(R.id.name);
        name.setText(d.getName());

        TextView tags = view.findViewById(R.id.subtext);
        StringBuilder documentTags = new StringBuilder();
        for(String s : d.getTags()) {
            documentTags.append(s);
            documentTags.append(", ");
        }
        documentTags.delete(documentTags.length() - 2, documentTags.length());
        tags.setText(documentTags.toString());

        return view;
    }
}
