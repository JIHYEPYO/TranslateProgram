package com.example.pyojihye.translateprogram.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pyojihye.translateprogram.R;

public class PlaceholderFragment extends Fragment {
    private final String TAG="PlaceholderFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";


    public PlaceholderFragment() {
    }

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView()");

        View rootView = new View(this.getActivity());

        switch(getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                rootView = inflater.inflate(R.layout.fragment_viewer_mode_option, container, false);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_viewer_mode_option_list, container, false);
                break;
        }
        return rootView;
    }

}