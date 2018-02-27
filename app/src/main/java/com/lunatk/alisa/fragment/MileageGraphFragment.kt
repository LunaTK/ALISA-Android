package com.lunatk.alisa.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.lunatk.alisa.R
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry



/**
 * Created by LunaTK on 2018. 2. 26..
 */
class MileageGraphFragment: Fragment() {
    private lateinit var barChart: BarChart

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_mileage_graph, container,false)
        barChart = view?.findViewById<BarChart>(R.id.bar_chart)!!

        setData(12, 50F)

        return view
    }

    private fun setData(count: Int, range: Float) {

        val start = 1f

        val yVals1 = ArrayList<BarEntry>()

        var i = start.toInt()
        while (i < start + count.toFloat() + 1f) {
            val mult = range + 1
            val `val` = (Math.random() * mult).toFloat()

            yVals1.add(BarEntry(i.toFloat(), `val`))
            i++
        }

        val set1: BarDataSet

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = barChart.getData().getDataSetByIndex(0) as BarDataSet
            set1.values = yVals1
            barChart.getData().notifyDataChanged()
            barChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(yVals1, "The year 2017")

            set1.setDrawIcons(false)

            set1.setColors(context.getColor(R.color.colorPrimary))

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.5f

            barChart.setData(data)
        }
        barChart.setPinchZoom(false)
        barChart
        barChart.animateY(1000)
    }
}