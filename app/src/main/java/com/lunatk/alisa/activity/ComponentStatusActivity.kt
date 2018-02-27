package com.lunatk.alisa.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.lunatk.alisa.R
import com.lunatk.alisa.`object`.ComponentInfo
import com.lunatk.alisa.adapter.ComponentStatusListAdapter

/**
 * Created by LunaTK on 2018. 2. 18..
 */
class ComponentStatusActivity : AppCompatActivity() {

    val recyclerView by lazy { findViewById(R.id.recycler_view) as? RecyclerView}
    val recyclerViewAdapter: ComponentStatusListAdapter
    init {
        recyclerViewAdapter = ComponentStatusListAdapter()
        recyclerViewAdapter.addItem(ComponentInfo("엔진오일", 20_000, 20_000))
        recyclerViewAdapter.addItem(ComponentInfo("에어컨 필터", 50_000, 14_000))
        recyclerViewAdapter.addItem(ComponentInfo("부동액", 40_000, 14_000))
        recyclerViewAdapter.addItem(ComponentInfo("에어필터", 15_000, 14_000))
        recyclerViewAdapter.addItem(ComponentInfo("브레이크 오일", 20_000, 14_000))
        recyclerViewAdapter.addItem(ComponentInfo("브레이크 패드", 30_000, 14_000))
        recyclerViewAdapter.addItem(ComponentInfo("배터리", 100_000, 14_000))
        recyclerViewAdapter.sortItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_component_status)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        setSupportActionBar(findViewById(R.id.toolbar))
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_simple_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_settings -> {
            }

            android.R.id.home -> {
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
                    addItemDecoration(DividerItemDecoration(this@ComponentStatusActivity, LinearLayoutManager.VERTICAL))
                }
                adapter = recyclerViewAdapter
            }
        }
    }
}