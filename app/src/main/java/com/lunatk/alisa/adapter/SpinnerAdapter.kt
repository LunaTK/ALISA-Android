package com.lunatk.alisa.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lunatk.alisa.R
import android.view.Gravity




/**
 * Created by LunaTK on 2018. 2. 27..
 */
class SpinnerAdapter(context: Context?, resource: Int, objects: Array<out String>?) : ArrayAdapter<String>(context, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setCentered(super.getView(position, convertView, parent))
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setCentered(super.getDropDownView(position, convertView, parent))
    }

    private fun setCentered(view: View): View {
        val textView = view.findViewById<View>(android.R.id.text1) as TextView
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        textView.setTextColor(context.getColor(R.color.textGray))
        textView.maxLines = 5
        textView.setSingleLine(false)
        return view
    }
}
