package com.lunatk.alisa.customview

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import com.lunatk.alisa.R
import android.view.WindowManager
import android.widget.Button


/**
 * Created by LunaTK on 2018. 2. 26..
 */
class MileageConfigDialog(context: Context) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {

    private val etInitialMileage by lazy{ findViewById(R.id.et_initial_mileage) as EditText}
    private val etAfterMileage by lazy{ findViewById(R.id.et_after_mileage) as EditText}
    private val btnOk by lazy {with(findViewById(R.id.btn_ok) as Button){
        setOnClickListener { dismiss() }
    }}
    private val btnCancel by lazy {with(findViewById(R.id.btn_cancel) as Button){
        setOnClickListener { dismiss() }
    }}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.8f
        window!!.attributes = lpWindow

        setContentView(R.layout.dialog_mileage_config)
    }

}