package com.example.pyojihye.translateprogram.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pyojihye.translateprogram.Activity.ViewerActivity;
import com.example.pyojihye.translateprogram.Movement.Const;
import com.example.pyojihye.translateprogram.R;

public class ViewerModeOptionFragment extends Fragment {
    private final String TAG = "ViewerModeOptionFragment";

    private static ViewerModeOptionFragment instance = new ViewerModeOptionFragment();
    private EditText editTextDelete;
    private EditText editTextGap;
    private Button buttonDelete;
    private Button buttonPrint;
    Bundle arguments = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG,"onCreateView()");

        View v = inflater.inflate(R.layout.fragment_viewer_mode_option, container, false);

        editTextDelete = (EditText) v.findViewById(R.id.editTextDeleteWord);
        editTextGap = (EditText) v.findViewById(R.id.editTextOutputGap);
        buttonDelete = (Button) v.findViewById(R.id.ButtonDelete);

        buttonDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Log.d(TAG,"deleteButtonClick()");

                View parent = (View) view.getParent();
                if (editTextDelete.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.ButtonDelete), R.string.snack_bar_delete, Snackbar.LENGTH_LONG);

                    View v = snackbar.getView();
                    TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    boolean deleteEqual = false;
                    Log.v("buttonDelete : ", editTextDelete.getText().toString() + "");
                    for (int i = 0; i < Const.delete_num; i++) {
                        if (Const.delete.get(i).equals(editTextDelete.getText().toString())) {
                            Log.v("EQUAL!!", Const.delete.get(i));
                            deleteEqual = true;
                            Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.ButtonDelete), R.string.snack_bar_equal, Snackbar.LENGTH_LONG);
                            View v = snackbar.getView();
                            TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();
                        }
                    }
                    if (deleteEqual != true) {
                        Const.delete.add(editTextDelete.getText().toString());
                        Const.delete_num++;

                        downKeyboard(getContext(), editTextDelete);

                        Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.ButtonDelete), R.string.snack_bar_delete_success, Snackbar.LENGTH_LONG);
                        View v = snackbar.getView();
                        TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                        editTextDelete.setText("");
                    }
//                    arguments.putString(editTextDelete.getText().toString(),Const.delete[Const.delete_num]);
                }
            }
        });

        buttonPrint = (Button) v.findViewById(R.id.buttonPrint);

        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG,"PrintButtonClick()");

                View parent = (View) view.getParent();
                if (editTextGap.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(parent.findViewById(R.id.buttonPrint), R.string.snack_bar_gap, Snackbar.LENGTH_LONG);

                    View v = snackbar.getView();
                    TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    Const.gap = Integer.parseInt(editTextGap.getText().toString());
                    Intent intentViewer = new Intent(getActivity(), ViewerActivity.class);
                    startActivity(intentViewer);
                }
            }
        });
        return v;
    }

    public static synchronized ViewerModeOptionFragment getInstance() {
        return instance;
    }

    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
