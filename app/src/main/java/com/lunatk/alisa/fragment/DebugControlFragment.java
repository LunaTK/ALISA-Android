package com.lunatk.alisa.fragment;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.lunatk.mybluetooth.R;

/**
 * Created by LunaTK on 2018. 2. 6..
 */

public class DebugControlFragment extends Fragment {

    public View v;
    public EditText et_component;
    public ArrayAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_debug_control,container,false);
        et_component = v.findViewById(R.id.et_component);
        Spinner spinner = v.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        return v;
    }
}
