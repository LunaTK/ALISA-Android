package com.lunatk.alisa.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lunatk.alisa.bluetooth.AlisaService
import com.lunatk.alisa.util.Utils

import com.lunatk.mybluetooth.R

class MyPageActivity : AppCompatActivity() {


    internal val sharedPreferences: SharedPreferences by lazy{ getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)}
    internal val editor: SharedPreferences.Editor by lazy{sharedPreferences.edit()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
    }

    fun testfunc(view: View){

    }

    fun logout(v: View) {
        Utils.removeLoginInfo(editor)
        stopService(Intent(this, AlisaService::class.java))
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
