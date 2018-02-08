package com.lunatk.alisa.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.lunatk.alisa.bluetooth.AlisaService;
import com.lunatk.alisa.fragment.DebugControlFragment;
import com.lunatk.alisa.fragment.DebugDisplayFragment;
import com.lunatk.mybluetooth.R;

import static com.lunatk.alisa.util.Utils.isServiceRunning;

public class DebugPannelActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private TabLayout tabLayout;

    private static final String TAG = "DebugPannelActivity";


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ActivityReceiver mReceiver;
    private boolean registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_pannel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        registerReceiver();

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!isServiceRunning(this)) startAlisaService();
    }


    private void registerReceiver() {
        Log.d(TAG,"registerReceiver");
        if(registered) return;
        if(mReceiver ==null) mReceiver = new ActivityReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.string_filter_action_send_to_activity));
        registerReceiver(mReceiver,filter);
        registered = true;
    }

    private void unregisterReceiver(){
        Log.d(TAG,"unregisterReceiver");
        if(mReceiver != null && registered) {
            unregisterReceiver(mReceiver);
            registered = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug_pannel, menu);
        return true;
    }

    public void setStatus(final String status){
        ((DebugDisplayFragment)mSectionsPagerAdapter.getItem(0)).setStatus(status);
    }

    public void setData(final String data){
        ((DebugDisplayFragment)mSectionsPagerAdapter.getItem(0)).setData(data);
    }

    public void startAlisaService(){
        Log.d(TAG,"start alisa service");
        Intent service = new Intent(this, AlisaService.class);
        startService(service);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private DebugDisplayFragment debugDisplayFragment;
        private DebugControlFragment debugControlFragment;

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                if(debugDisplayFragment==null) debugDisplayFragment = new DebugDisplayFragment();
                return debugDisplayFragment;
            } else {
                if(debugControlFragment==null) debugControlFragment = new DebugControlFragment();
                return debugControlFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position==0?"DISPLAY":"CONTROL";
        }
    }

    public void logout(View v){
        editor.remove("user_id");
        editor.remove("user_pass");
        editor.commit();
        stopService(new Intent(this, AlisaService.class));
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
            if(intent.getAction().equals(getResources().getString(R.string.string_filter_action_send_to_activity))){
                String status;
                int[] data_buffer;
                status = intent.getStringExtra("status");
                data_buffer = intent.getIntArrayExtra("data");
                if(status!=null){
                    setStatus(status);
                }
                if(data_buffer!=null){

                    String datastr = "홀센서 : " + (data_buffer[0] + data_buffer[1] * 256)
                            + "\n온도 : " + ((data_buffer[2] + data_buffer[3] * 256) * 0.0078125)
                            + "\n가속도x : " + (data_buffer[4] + data_buffer[5] * 256)
                            + "\n가속도y : " + (data_buffer[6] + data_buffer[7] * 256)
                            + "\n가속도z : " + (data_buffer[8] + data_buffer[9] * 256)
                            + "\n자이로x : " + (data_buffer[10] + data_buffer[11] * 256)
                            + "\n자이로y : " + (data_buffer[12] + data_buffer[13] * 256)
                            + "\n자이로z : " + (data_buffer[14] + data_buffer[15] * 256)
                            + "\n지자기x : " + (data_buffer[16] + data_buffer[17] * 256)
                            + "\n지자기y : " + (data_buffer[18] + data_buffer[19] * 256)
                            + "\n지자기z : " + (data_buffer[20] + data_buffer[21] * 256);
                    setData(datastr);
                    setStatus("Connected");
                }
            }
        }
    }

    public void triggerAccident(View view){

    }

    public void triggerAcceleration(View view){

    }

    public void triggerDecceleration(View view){

    }

    public void triggerFilterWarning(View view){

    }

    public void addComponent(View view){
        DebugControlFragment dcf = ((DebugControlFragment)(mSectionsPagerAdapter.getItem(1)));
        dcf.adapter.add(dcf.et_component.getText().toString());
    }

    public void triggerMileageExceed(View view){

    }
}
