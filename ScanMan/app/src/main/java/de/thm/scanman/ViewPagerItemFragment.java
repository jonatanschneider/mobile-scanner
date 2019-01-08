package de.thm.scanman;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewPagerItemFragment extends Fragment {
    private static final String PAGE_TITLE = "PAGE_TITLE";

    private String pageTitle;

    //private ListView documentsListView;

    public ViewPagerItemFragment(){}

    public static ViewPagerItemFragment getInstance(String pageTitle){
        ViewPagerItemFragment fragment = new ViewPagerItemFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_TITLE, pageTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.pageTitle = getArguments().getString(PAGE_TITLE);
        } else {
            Log.d("TAG", "Error: no arguments!");
        }

        //documentsListView = findViewById(R.id.documents_list);
        //documentsListView.setEmptyView(findViewById(R.id.documents_list_empty));

        /*
        if(tab.getText().toString().equals(getString(R.string.all_documents))) {
            Fragment fragment = new ViewPagerItemFragment();
            //Bundle bundle = new Bundle();
            //bundle.putString("id", checkedRecord.getId());
            //fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, "details")
                    .commit();
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_item, container, false);

        //binding = FragmentRecordFormBinding.bind(view);
        //binding.save.setOnClickListener(this);
        //setHasOptionsMenu(true);

        TextView content = ((TextView)view.findViewById(R.id.lbl_pager_item_content));
        content.setText(pageTitle);

        return view;
    }

}
