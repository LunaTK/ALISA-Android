package com.lunatk.alisa.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import com.lunatk.alisa.R
import com.lunatk.alisa.adapter.SpinnerAdapter

/**
 * Created by LunaTK on 2018. 2. 25..
 */
class AccidentConfigActivity : AppCompatActivity() {

    private val TAG = "AccidentConfigActivity"
    private val spinnerType by lazy { findViewById(R.id.spinner_type) as Spinner }
    private val spinnerMsg by lazy { findViewById(R.id.spinner_msg) as Spinner }
    private val spinnerSound by lazy { findViewById(R.id.spinner_sound) as Spinner }
    private val edittextMessage by lazy {findViewById(R.id.et_msg) as EditText}
    private val checkbox1 by lazy {findViewById(R.id.checkbox_1) as CheckBox}
    private val checkbox2 by lazy {findViewById(R.id.checkbox_2) as CheckBox}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_config)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        spinnerType.adapter = SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.accident_types))
        spinnerSound.adapter = SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.alert_sound_types))
        spinnerMsg.adapter = SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, replaceString(resources.getStringArray(R.array.alert_msg)))

        spinnerMsg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                edittextMessage.setText(parent?.adapter?.getItem(position) as? String)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceString(stringArray: Array<String>): Array<out String>{
        for(i in 0..stringArray.size-1){
            stringArray[i] = stringArray[i].replace("#user","(사용자)")
            stringArray[i] = stringArray[i].replace("#type","(사고)")
        }
        return stringArray
    }

    fun onCheckboxClick(view: View){
        if(view.id==R.id.checkbox_1){
            checkbox2
        } else {
            checkbox1
        }.isChecked = false
    }
}