package com.lunatk.alisa.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lunatk.mybluetooth.R;

import java.util.ArrayList;

/**
 * Created by LunaTK on 2018. 1. 12..
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    public ArrayList<BluetoothDevice> list = new ArrayList<>();

    public void updateList(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_list_component, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_macadd.setText(list.get(position).toString());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addDevice(BluetoothDevice device) {
        list.add(device);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_macadd;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_name = (TextView)itemView.findViewById(R.id.tv_name);
            tv_macadd = (TextView)itemView.findViewById(R.id.tv_macadd);
        }
    }
}
