package com.lunatk.alisa.adapter

import android.bluetooth.BluetoothDevice
import android.provider.ContactsContract
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

import com.lunatk.alisa.R
import com.lunatk.alisa.`object`.NotificationObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by LunaTK on 2018. 1. 12..
 */

class NotificationListAdapter : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    var list = ArrayList<NotificationObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listitem_accident_history, parent, false)
        val holder = ViewHolder(v)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationObject = list[position]

        holder.tvType.text = when(notificationObject){
            NotificationObject.CRASH-> "접촉 사고"
            NotificationObject.FLIP-> "전복 사고"
            NotificationObject.TOW-> "견인"
        }
        holder.ivType.setImageResource(when(notificationObject){
            NotificationObject.CRASH-> R.drawable.ic_crash
            NotificationObject.FLIP-> R.drawable.ic_flip
            NotificationObject.TOW-> R.drawable.ic_tow
        })

        notificationObject.date?.let{
            holder.tvDate.text = SimpleDateFormat("yyyy.MM.dd").format(it)
        }

        notificationObject.msg?.let{
            holder.tvContent.text = it
        }

        holder.itemView.setTag(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(newItem: NotificationObject){
        list.add(newItem)
    }

    fun removeAllItems(){
        list.clear()
    }

    inner class ViewHolder: RecyclerView.ViewHolder {

        val ivType: ImageView
        val tvType: TextView
        val tvDate: TextView
        val tvContent: TextView

        constructor(itemView: View):super(itemView){
            ivType = itemView.findViewById(R.id.iv_type)
            tvType = itemView.findViewById(R.id.tv_type)
            tvDate = itemView.findViewById(R.id.tv_date)
            tvContent = itemView.findViewById(R.id.tv_content)
        }

    }
}
