package com.lunatk.alisa.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lunatk.alisa.`object`.ComponentInfo
import com.lunatk.alisa.customview.ComponentStatusBar

import com.lunatk.mybluetooth.R

import java.util.ArrayList

/**
 * Created by LunaTK on 2018. 1. 12..
 */

class ComponentStatusListAdapter : RecyclerView.Adapter<ComponentStatusListAdapter.ViewHolder>() {

    var list = ArrayList<ComponentInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listitem_component_info, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val componentInfo = list[position]
        holder.tv_name.text = componentInfo.name
        holder.tv_mileage.text = "${componentInfo.currentMileage} km / ${componentInfo.maxMileage} km"
        holder.componentStatusbar.value = (componentInfo.currentMileage.toDouble() / componentInfo.maxMileage * 100).toInt()
        holder.iv_status.setImageResource(
                when((componentInfo.currentMileage.toDouble() / componentInfo.maxMileage * 3).toInt()){
                    0->R.drawable.status_red
                    1->R.drawable.status_yellow
                    else->R.drawable.status_green
                }
        )
        holder.itemView.setTag(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(newItem: ComponentInfo){
        list.add(newItem)
    }

    inner class ViewHolder: RecyclerView.ViewHolder {

        var tv_name: TextView
        var tv_mileage: TextView
        var componentStatusbar: ComponentStatusBar
        var iv_status: ImageView

        constructor(itemView: View):super(itemView){
            Log.d("ComponentStatus", "hi")
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_mileage = itemView.findViewById(R.id.tv_mileage)
            componentStatusbar = itemView.findViewById(R.id.component_status_bar)
            iv_status = itemView.findViewById(R.id.iv_status)
        }

    }
}
