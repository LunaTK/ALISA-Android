package com.lunatk.alisa.activity;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lunatk.alisa.bluetooth.BluetoothService;
import com.lunatk.alisa.network.NetworkThread;
import com.lunatk.mybluetooth.R;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private final int REQUEST_ENABLE_BT = 1;

    private TextView tv_status, tv_data, tv_lat, tv_long;
    private ActivityReceiver mReceiver;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;

    protected LocationManager locationManager;
    private LocationListener locationListener;

    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        tv_status = findViewById(R.id.tv_status);
        tv_data = findViewById(R.id.tv_data);
        tv_lat = findViewById(R.id.tv_lat);
        tv_long = findViewById(R.id.tv_long);

        registerReceiver();

        locationManager =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // GPS 정보 가져오기
        isGPSEnabled = locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER);

        // 현재 네트워크 상태 값 알아오기
        isNetworkEnabled = locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG," onLocationChanged : " + location.getLatitude() + ", " + location.getLongitude());
                lat = location.getLatitude();
                lon = location.getLongitude();

                tv_lat.setText("" + lat);
                tv_long.setText("" + lon);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        updateGPS();

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

    }

    public void updateGPS(View v){
        updateGPS();
    }

    @SuppressLint("MissingPermission")
    private void updateGPS() {
        this.isGetLocation = true;
// 네트워크 정보로 부터 위치값 가져오기
        if (isNetworkEnabled) {
            Log.d(TAG,"networking");
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    // 위도 경도 저장
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Log.d(TAG,"Networking success");
                }
            }
        }

        if (isGPSEnabled) {
            Log.d(TAG,"gps");
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        Log.d(TAG,"GPS success");
                    }
                }
            }
        }
        tv_lat.setText("" + lat);
        tv_long.setText("" + lon);

    }

    private void registerReceiver() {
        if(mReceiver !=null) return;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.string_filter_action_send_to_activity));

        mReceiver = new ActivityReceiver();
        registerReceiver(mReceiver,filter);
    }

    private void unregisterReceiver(){
        if(mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    public void setStatus(final String str, final int color){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText(str);
                tv_status.setTextColor(color);
            }
        });
    }

    public void setStatus(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText(str);
            }
        });
    }

    public void setData(final String data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_data.setText(data);
            }
        });

    }
    //서비스 시작.
    public void startServiceMethod(View v){
        Intent service = new Intent(this, BluetoothService.class);
        startService(service);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                //TODO : 서비스 시작
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        unregisterReceiver();
    }

    class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,intent.getAction());
            if(intent.getAction().equals(getResources().getString(R.string.string_filter_action_send_to_activity))){
                String status, data;
                status = intent.getStringExtra("status");
                data = intent.getStringExtra("data");
                Log.d(TAG,"status : " + status + ", data : " + data);
                if(status!=null){
                    setStatus(status);
                }
                if(data!=null){
                    setData(data);
                    NetworkThread.sendSensorData(mainHandler,data);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                    updateGPS();
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                return;
        }

        // GPS 정보 가져오기
        isGPSEnabled = locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER);

        // 현재 네트워크 상태 값 알아오기
        isNetworkEnabled = locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

}
