package com.lunatk.alisa.fragment;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lunatk.alisa.activity.MainActivity;
import com.lunatk.mybluetooth.R;

/**
 * Created by LunaTK on 2018. 2. 6..
 */

public class DebugDisplayFragment extends Fragment {

    private String TAG = "DebugDisplayFragment";

    private TextView tv_status, tv_data, tv_lat, tv_long;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);

        tv_status = v.findViewById(R.id.tv_status);
        tv_data = v.findViewById(R.id.tv_data);
        tv_lat = v.findViewById(R.id.tv_lat);
        tv_long = v.findViewById(R.id.tv_long);
        return v;
    }

    public void setStatus(final String str){
        if(tv_status!=null)tv_status.setText(str);
    }

    public void setData(final String data){
        if(tv_data!=null)tv_data.setText(data);
    }
}
