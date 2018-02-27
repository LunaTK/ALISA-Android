package com.lunatk.alisa.activity

import android.bluetooth.BluetoothDevice
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.lunatk.alisa.adapter.BLEDeviceListAdapter
import com.lunatk.alisa.bluetooth.AlisaService
import com.lunatk.alisa.util.Utils
import com.lunatk.alisa.R

class DeviceSearchActivity : AppCompatActivity() {

    val TAG = "DeviceSearchActivity"
    var mMessenger: Messenger? = null
    val deviceSet: HashSet<BluetoothDevice> = HashSet()
    val adapter = BLEDeviceListAdapter(this)
    val listView: RecyclerView by lazy { findViewById(R.id.recycler_view) as RecyclerView }
    val tvVehicleNumber: TextView by lazy{ findViewById(R.id.tv_vehicle_number) as TextView}
    val progressbar: ProgressBar by lazy{ findViewById(R.id.progressbar) as ProgressBar }
    val sharedPreferences: SharedPreferences by lazy { getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) }
    val editor: SharedPreferences.Editor by lazy { sharedPreferences.edit() }
    val btnSearch: Button by lazy{findViewById(R.id.btn_search)  as Button}

    var isSearching: Boolean = false

    var mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            mMessenger = Messenger(service)
            sendSetActivity()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_search)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        bindService(Intent(this, AlisaService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)

        with(listView){
            layoutManager = LinearLayoutManager(this@DeviceSearchActivity).apply { orientation = LinearLayoutManager.VERTICAL }
            adapter = this@DeviceSearchActivity.adapter
        }

        tvVehicleNumber.text = sharedPreferences.getString("device_name","연결된 차량이 없습니다")

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        sendStopSearching()
    }

    fun onSearchClick(view: View){
        if(isSearching){
            progressbar.visibility = View.INVISIBLE
            sendStopSearching()
            btnSearch.text = "찾기"
        } else {
            progressbar.visibility = View.VISIBLE
            sendStartSearching()
            btnSearch.text = "중지"
        }
        isSearching = !isSearching
    }

    fun onDeviceConnected(address: String, name: String){
//        Toast.makeText(this,"Device Connected : $name($address)",Toast.LENGTH_SHORT).show()
        sendStopSearching()
        Utils.setRegisteredDevice(editor, address, name)
        tvVehicleNumber.text = name
    }

    fun sendStartSearching(){
        var message = Message.obtain()
        message.what = AlisaService.START_SCANNING
        mMessenger?.send(message)
    }

    fun sendStopSearching(){
        var message = Message.obtain()
        message.what = AlisaService.STOP_SCANNING
        mMessenger?.send(message)
    }

    fun sendConnectDevice(device: BluetoothDevice){
        var message = Message.obtain()
        message.what = AlisaService.CONNECT_DEVICE
        message.obj = device
        mMessenger?.send(message)
    }

    fun sendSetActivity(){
        var message = Message.obtain()
        message.what = AlisaService.SET_ACTIVITY
        message.obj = this
        mMessenger?.send(message)
    }

    fun addSearchResult(device: BluetoothDevice){
//        Log.i(TAG, "device found : ${device.toString()}")
        if(device.name != null && !deviceSet.contains(device)){
            deviceSet.add(device)
            adapter.addItem(device)
            adapter.notifyDataSetChanged()
        }
    }

    fun clearSearchResult(){
        deviceSet.clear()
        adapter.removeAllItems()
        runOnUiThread { adapter.notifyDataSetChanged() }
    }

}
