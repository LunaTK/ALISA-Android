package com.lunatk.alisa.adapter

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lunatk.alisa.`object`.ComponentInfo
import com.lunatk.alisa.activity.DeviceSearchActivity
import com.lunatk.alisa.customview.ComponentStatusBar

import com.lunatk.mybluetooth.R

import java.util.ArrayList

/**
 * Created by LunaTK on 2018. 1. 12..
 */

class BLEDeviceListAdapter : RecyclerView.Adapter<BLEDeviceListAdapter.ViewHolder> {

    var list = ArrayList<BluetoothDevice>()
    var mActivity: DeviceSearchActivity

    constructor(activity: DeviceSearchActivity):super(){
        mActivity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listitem_ble_device_info, parent, false)
        val holder = ViewHolder(v)
        v.setOnClickListener(){
            mActivity.sendConnectDevice(list[holder.adapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bleDevice = list[position]
        holder.tv_name.text = bleDevice.name ?: "No Name"
//        holder.tv_address.text = bleDevice.address
        holder.itemView.setTag(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(newItem: BluetoothDevice){
        list.add(newItem)
    }

    fun removeAllItems(){
        list.clear()
    }

    inner class ViewHolder: RecyclerView.ViewHolder {

        var tv_name: TextView
//        var tv_address: TextView

        constructor(itemView: View):super(itemView){
            tv_name = itemView.findViewById(R.id.tv_name)
//            tv_address = itemView.findViewById(R.id.tv_address)
        }

    }
}
