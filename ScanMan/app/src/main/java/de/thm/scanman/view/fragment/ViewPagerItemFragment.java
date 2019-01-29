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
import java.util.Date;
import java.util.List;

public class ViewPagerItemFragment extends Fragment {
    private static final String PAGE_INDEX = "PAGE_INDEX";

    private int page;

    private ListView documentsListView;
    private TextView documentListEmpty;
    private DocumentArrayAdapter adapter;
    private DocumentArrayAdapter adapter2;
    private DocumentArrayAdapter adapter3;
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

        /* // TODO: Debug-Code entfernen!
        ArrayList<Document.Image> images = new ArrayList<>();
        Document document = new Document();
        Document.Image im = new Document.Image("file.jpeg", new Date().getTime());
        images.add(im);
        document.setName("Tester Doc");
        document.setImages(images);
        document.setCreatedAt(new Date().getTime());
        List<String> tags = new ArrayList<>();
        tags.add("tagA");
        tags.add("tagB");
        tags.add("tagC");
        document.setTags(tags);*/

        LiveData<User> userLiveData = userDAO.get(FirebaseAuth.getInstance().getUid());

        switch(page) {
            case 0: userLiveData.observe(this,
                            user -> {
                                allDocuments = new ArrayList<>();
                                allDocuments.addAll(user.getCreatedDocuments());
                                allDocuments.addAll(user.getSharedDocuments());
                                for(Document d : allDocuments) {
                                    System.out.println("all " + d.getName() + " " + d.getId());
                                }
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
                                for(Document d : createdDocuments) {
                                    System.out.println("created " + d.getName() + " " + d.getId());
                                }
                                if(adapter2 == null) {
                                    adapter2 = new DocumentArrayAdapter(getContext(), createdDocuments);
                                    documentsListView.setAdapter(adapter2);
                                }
                                else {
                                    adapter2.clear();
                                    adapter2.addAll(createdDocuments);
                                }
                            }
                    );
                    /* // TODO: Debug-Code entfernen!
                    createdDocuments = new ArrayList<>();
                    createdDocuments.add(document);
                    if(adapter == null) {
                        adapter = new DocumentArrayAdapter(getContext(), createdDocuments);
                        documentsListView.setAdapter(adapter);
                    }
                    else {
                        adapter.clear();
                        adapter.addAll(createdDocuments);
                    }*/
                    break;
            case 2: documentListEmpty.setText(getString(R.string.no_shared_documents));
                    userLiveData.observe(this,
                            user -> {
                                sharedDocuments = user.getSharedDocuments();
                                for(Document d : sharedDocuments) {
                                    System.out.println("shared " + d.getName() + " " + d.getId());
                                }
                                if(adapter3 == null) {
                                    adapter3 = new DocumentArrayAdapter(getContext(), sharedDocuments);
                                    documentsListView.setAdapter(adapter3);
                                }
                                else {
                                    adapter3.clear();
                                    adapter3.addAll(sharedDocuments);
                                }
                            }
                    );
                    break;
        }
        documentsListView.setEmptyView(documentListEmpty);

        return view;
    }

}
