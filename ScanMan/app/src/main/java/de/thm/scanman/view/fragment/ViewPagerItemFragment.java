package de.thm.scanman.view.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import de.thm.scanman.R;
import de.thm.scanman.model.Document;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.UserDAO;
import de.thm.scanman.util.DocumentArrayAdapter;

import android.util.Log;
import android.view.LayoutInflater;
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
    List<Document> allDocuments;
    List<Document> createdDocuments;
    List<Document> sharedDocuments;

    public ViewPagerItemFragment(){}

    public static ViewPagerItemFragment getInstance(int page){
        ViewPagerItemFragment fragment = new ViewPagerItemFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_INDEX, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.page = getArguments().getInt("idx");
        } else {
            Log.d("TAG", "Error: no arguments!");
        }
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
                            allDocuments = new ArrayList<>();   // ohne Neuerzeugung funktioniert es nicht!
                            allDocuments.addAll(user.getCreatedDocuments());
                            allDocuments.addAll(user.getSharedDocuments());
                            for (Document d : allDocuments) {
                                System.out.println("all " + d.getName() + " " + d.getId());
                            }
                            if (adapter == null) {
                                adapter = new DocumentArrayAdapter(getContext(), allDocuments);
                                documentsListView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(allDocuments);
                            }
                            break;
                        case 1:
                            createdDocuments = new ArrayList<>();   // ohne Neuerzeugung funktioniert es nicht!
                            createdDocuments.addAll(user.getCreatedDocuments());
                            for (Document d : createdDocuments) {
                                System.out.println("created " + d.getName() + " " + d.getId());
                            }
                            if (adapter == null) {
                                adapter = new DocumentArrayAdapter(getContext(), createdDocuments);
                                documentsListView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(createdDocuments);
                            }
                            break;
                        case 2:
                            sharedDocuments = new ArrayList<>();    // ohne Neuerzeugung funktioniert es nicht!
                            sharedDocuments.addAll(user.getSharedDocuments());
                            for (Document d : sharedDocuments) {
                                System.out.println("shared " + d.getName() + " " + d.getId());
                            }
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

}
