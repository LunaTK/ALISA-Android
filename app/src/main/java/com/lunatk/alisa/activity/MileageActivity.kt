package com.lunatk.alisa.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.lunatk.alisa.fragment.DebugControlFragment
import com.lunatk.alisa.fragment.DebugDisplayFragment
import com.lunatk.alisa.fragment.MileageGraphFragment
import com.lunatk.mybluetooth.R
import android.graphics.drawable.GradientDrawable
import android.view.Menu
import android.view.MenuItem
import com.lunatk.alisa.customview.MileageConfigDialog


/**
 * Created by LunaTK on 2018. 2. 26..
 */
class MileageActivity : AppCompatActivity() {

    private val viewPager: ViewPager by lazy{ findViewById(R.id.viewpager) as ViewPager}
    private val tabLayout: TabLayout by lazy { findViewById(R.id.tablayout) as TabLayout }
    private val pagerAdapter by lazy { SectionsPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mileage)

        setupViewPager()

        setSupportActionBar(findViewById(R.id.toolbar))
    }

    fun setupViewPager(){
        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(pagerAdapter)
        tabLayout.setupWithViewPager(viewPager)

        val linearLayout = tabLayout.getChildAt(0) as LinearLayout
        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        val drawable = GradientDrawable()
        drawable.setColor(getColorStateList(R.color.borderColorSilver))
        drawable.setSize(6, 1)
        linearLayout.dividerPadding = 40
        linearLayout.dividerDrawable = drawable
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_simple_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_settings -> {
                MileageConfigDialog(this).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val fragments = Array(3) { MileageGraphFragment() }

        override fun getItem(position: Int): Fragment {
            return fragments[position] as Fragment
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when(position){
                0->"주별"
                1->"월별"
                2->"년별"
                else->"??"
            }
        }
    }
}