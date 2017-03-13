package com.example.pyojihye.translateprogram.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;

import static com.example.pyojihye.translateprogram.Movement.Const.delete;
import static com.example.pyojihye.translateprogram.Movement.Const.delete_num;

public class ViewerModeOptionListFragment extends android.support.v4.app.Fragment {
    private final String TAG = "ViewerModeOptionListFragment";

    private static ViewerModeOptionListFragment instance = new ViewerModeOptionListFragment();
    ArrayAdapter<String> adapter;
    ListView listViewDelete;


    @Override
    public void onResume() {
//        Log.d(TAG,"onResume()");

        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG,"onCreateView()");

        View v = inflater.inflate(R.layout.fragment_viewer_mode_option_list, container, false);
        listViewDelete = (ListView) v.findViewById(R.id.listViewDeleteWordList);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, delete);
        listViewDelete.setAdapter(adapter);
        listViewDelete.setLongClickable(true);
        listViewDelete.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delete.remove(position);
                delete_num--;
                onResume();
                return true;
            }
        });
        return v;
    }

    public static synchronized ViewerModeOptionListFragment getInstance() {
        return instance;
    }
}
