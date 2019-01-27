package de.thm.scanman.view.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import de.thm.scanman.R;
import de.thm.scanman.model.User;
import de.thm.scanman.persistence.UserDAO;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private ArrayAdapter<String> adapter;
    private UserDAO userDAO;
    private List<String> allDocuments;
    private List<String> createdDocuments;
    private List<String> sharedDocuments;

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

        /*userDAO = new UserDAO();
        //TODO: Debug-Zeug wieder entfernen (und aus <String> <Document> machen):
        allDocuments = new ArrayList<>();
        createdDocuments = new ArrayList<>();
        sharedDocuments = new ArrayList<>();
        allDocuments.add("Test1");
        createdDocuments.add("Test2");
        sharedDocuments.add("Test3");

        LiveData<User> userLiveData = userDAO.get(FirebaseAuth.getInstance().getUid());

        userLiveData.observe(this,
                user -> {
                    //allDocuments.clear();
                    //allDocuments.addAll(user.getCreatedDocuments());
                    //allDocuments.addAll(user.getSharedDocuments());
                    if(allDocumentsAdapter == null) {
                        allDocumentsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, allDocuments);
                        documentsListView.setAdapter(allDocumentsAdapter);
                    }
                    else {
                        allDocumentsAdapter.clear();
                        allDocumentsAdapter.addAll(allDocuments);
                    }
                    if(createdDocumentsAdapter == null) {
                        createdDocumentsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, createdDocuments);
                        documentsListView.setAdapter(createdDocumentsAdapter);
                    }
                    else {
                        createdDocumentsAdapter.clear();
                        createdDocumentsAdapter.addAll(allDocuments);
                    }
                    if(sharedDocumentsAdapter == null) {
                        sharedDocumentsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, sharedDocuments);
                        documentsListView.setAdapter(sharedDocumentsAdapter);
                    }
                    else {
                        sharedDocumentsAdapter.clear();
                        sharedDocumentsAdapter.addAll(sharedDocuments);
                    }
                }
        );*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_item, container, false);

        documentsListView = view.findViewById(R.id.documents_list);
        documentListEmpty = view.findViewById(R.id.documents_list_empty);

        userDAO = new UserDAO();
        //TODO: Debug-Zeug wieder entfernen (und aus <String> <Document> machen):
        allDocuments = new ArrayList<>();
        createdDocuments = new ArrayList<>();
        sharedDocuments = new ArrayList<>();
        allDocuments.add("Test1");
        createdDocuments.add("Test2");
        sharedDocuments.add("Test3");

        LiveData<User> userLiveData = userDAO.get(FirebaseAuth.getInstance().getUid());

        switch(page) {
            case 0: userLiveData.observe(this,
                            user -> {
                                //allDocuments.clear();
                                //allDocuments.addAll(user.getCreatedDocuments());
                                //allDocuments.addAll(user.getSharedDocuments());
                                if(adapter == null) {
                                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, allDocuments);
                                    documentsListView.setAdapter(adapter);
                                }
                                else {
                                    adapter.clear();
                                    adapter.addAll(allDocuments);
                                }
                            }
                    );
                    break;
            case 1: documentListEmpty.setText("Keine eigenen Dokumente");
                    userLiveData.observe(this,
                            user -> {
                                //createdDocuments = user.getCreatedDocuments();
                                if(adapter == null) {
                                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, createdDocuments);
                                    documentsListView.setAdapter(adapter);
                                }
                                else {
                                    adapter.clear();
                                    adapter.addAll(createdDocuments);
                                }
                            }
                    );
                    break;
            case 2: documentListEmpty.setText("Keine geteilten Dokumente");
                    userLiveData.observe(this,
                            user -> {
                                //sharedDocuments = user.getSharedDocuments();
                                if(adapter == null) {
                                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, sharedDocuments);
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
