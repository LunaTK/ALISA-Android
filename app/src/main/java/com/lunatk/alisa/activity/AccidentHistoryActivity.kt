package com.lunatk.alisa.activity

import android.app.Notification
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.lunatk.alisa.R
import com.lunatk.alisa.`object`.NotificationObject
import com.lunatk.alisa.adapter.NotificationListAdapter
import java.util.*

class AccidentHistoryActivity : AppCompatActivity() {

    val recyclerView by lazy {findViewById(R.id.recycler_view) as RecyclerView}
    val recyclerViewAdapter = NotificationListAdapter()

    init{
        recyclerViewAdapter.addItem(NotificationObject.CRASH.apply{ date = Date();msg = "다 꼬라박아 범퍼카~!"})
        recyclerViewAdapter.addItem(NotificationObject.FLIP.apply{ date = Date();msg = "차량이 뒤집어져서 쉬고있습니다"})
        recyclerViewAdapter.addItem(NotificationObject.TOW.apply{ date = Date();msg = "차량이 의문의 불순한 세력에게 납치당하였습니다"})
        recyclerViewAdapter.addItem(NotificationObject.CRASH.apply{ date = Date();msg = "차량이 또 박살났습니다"})
        recyclerViewAdapter.addItem(NotificationObject.CRASH.apply{ date = Date();msg = "차량이 박살났습니다"})
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_history)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        setupRecyclerView()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_simple_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, AccidentConfigActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun setupRecyclerView(){
        recyclerView?.let {
            with(it){
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.VERTICAL

                }
                adapter = recyclerViewAdapter
            }
        }
    }
}
