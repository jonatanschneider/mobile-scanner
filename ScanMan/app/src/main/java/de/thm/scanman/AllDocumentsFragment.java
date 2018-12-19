package de.thm.scanman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AllDocumentsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_all_documents, container, false);
        //binding = FragmentRecordFormBinding.bind(view);
        //binding.save.setOnClickListener(this);
        setHasOptionsMenu(true);

        return view;
    }
}
