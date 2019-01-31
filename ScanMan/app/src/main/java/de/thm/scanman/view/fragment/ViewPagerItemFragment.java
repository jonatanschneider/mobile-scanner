package de.thm.scanman.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.UserDAO;
import de.thm.scanman.util.DocumentArrayAdapter;
import de.thm.scanman.view.activity.EditDocumentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerItemFragment extends Fragment {
    private static final String PAGE_INDEX = "PAGE_INDEX";

    private int page;

    private ListView documentsListView;
    private TextView documentListEmpty;
    private DocumentArrayAdapter adapter;
    private UserDAO userDAO;
    private List<Document> allDocuments;
    private List<Document> createdDocuments;
    private List<Document> sharedDocuments;

    public ViewPagerItemFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.page = getArguments().getInt("idx");
        } else {
            Log.d("TAG", "Error: no arguments!");
        }

        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        //inflater.inflate(R.menu.documents_lists, menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // close keyboard
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter has a standard filter, which filters for words in the return from Document::toString() TODO: evtl. toString-Ausgabe anpassen oder eigenen Filter schreiben
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_item, container, false);

        documentsListView = view.findViewById(R.id.documents_list);
        documentListEmpty = view.findViewById(R.id.documents_list_empty);

        userDAO = new UserDAO();
        LiveData<User> userLiveData = userDAO.get(FirebaseAuth.getInstance().getUid());

        switch (page) {
            case 1: documentListEmpty.setText(getString(R.string.no_created_documents));
                    break;
            case 2: documentListEmpty.setText(getString(R.string.no_shared_documents));
                    break;
        }

        userLiveData.observe(this,
                user -> {
                    switch (page) {
                        case 0:
                            allDocuments = new ArrayList<>();   // without new creation here it does not work!
                            allDocuments.addAll(user.getCreatedDocuments());
                            allDocuments.addAll(user.getSharedDocuments());
                            if (adapter == null) {
                                adapter = new DocumentArrayAdapter(getContext(), allDocuments);
                                documentsListView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(allDocuments);
                            }
                            break;
                        case 1:
                            createdDocuments = new ArrayList<>();   // without new creation here it does not work!
                            createdDocuments.addAll(user.getCreatedDocuments());
                            if (adapter == null) {
                                adapter = new DocumentArrayAdapter(getContext(), createdDocuments);
                                documentsListView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(createdDocuments);
                            }
                            break;
                        case 2:
                            sharedDocuments = new ArrayList<>();    // without new creation here it does not work!
                            sharedDocuments.addAll(user.getSharedDocuments());
                            if (adapter == null) {
                                adapter = new DocumentArrayAdapter(getContext(), sharedDocuments);
                                documentsListView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(sharedDocuments);
                            }
                            break;
                    }
                });
        documentsListView.setEmptyView(documentListEmpty);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        documentsListView.setOnItemClickListener((parent, view, position, id) -> {
            Document d = adapter.getItem(position);

            Intent i = new Intent(getContext(), EditDocumentActivity.class);
            i.setData(Uri.parse(String.valueOf(d.getId())));
            startActivityForResult(i, 1);
        });
    }
}
