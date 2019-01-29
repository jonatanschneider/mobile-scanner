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
    private List<Document> allDocuments;
    private List<Document> createdDocuments;
    private List<Document> sharedDocuments;

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
            //this.page = getArguments().getInt(PAGE_INDEX);
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
        allDocuments = new ArrayList<>();
        createdDocuments = new ArrayList<>();
        sharedDocuments = new ArrayList<>();

        LiveData<User> userLiveData = userDAO.get(FirebaseAuth.getInstance().getUid());

        switch(page) {
            case 0: userLiveData.observe(this,
                            user -> {
                                allDocuments.clear();
                                allDocuments.addAll(user.getCreatedDocuments());
                                allDocuments.addAll(user.getSharedDocuments());
                                if(adapter == null) {
                                    adapter = new DocumentArrayAdapter(getContext(), allDocuments);
                                    documentsListView.setAdapter(adapter);
                                }
                                else {
                                    adapter.clear();
                                    adapter.addAll(allDocuments);
                                }
                            }
                    );
                    break;
            case 1: documentListEmpty.setText(getString(R.string.no_created_documents));
                    userLiveData.observe(this,
                            user -> {
                                createdDocuments = user.getCreatedDocuments();
                                if(adapter == null) {
                                    adapter = new DocumentArrayAdapter(getContext(), createdDocuments);
                                    documentsListView.setAdapter(adapter);
                                }
                                else {
                                    adapter.clear();
                                    adapter.addAll(createdDocuments);
                                }
                            }
                    );
                    break;
            case 2: documentListEmpty.setText(getString(R.string.no_shared_documents));
                    userLiveData.observe(this,
                            user -> {
                                sharedDocuments = user.getSharedDocuments();
                                if(adapter == null) {
                                    adapter = new DocumentArrayAdapter(getContext(), sharedDocuments);
                                    documentsListView.setAdapter(adapter);
                                }
                                else {
                                    adapter.clear();
                                    adapter.addAll(sharedDocuments);
                                }
                            }
                    );
                    break;
        }
        documentsListView.setEmptyView(documentListEmpty);

        return view;
    }

}
