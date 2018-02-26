package com.lunatk.alisa.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView

import com.lunatk.mybluetooth.R
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.lunatk.alisa.bluetooth.AlisaService
import com.lunatk.alisa.customview.CircularProgressBar
import com.lunatk.alisa.util.Utils
import java.sql.Timestamp


/**
 * Created by LunaTK on 2018. 2. 17..
 */

class DashboardActivity : AppCompatActivity() {

    private val largeTextRatio = 1.5F

    private val tv_mileage by lazy{ findViewById(R.id.tv_mileage_value) as? TextView }
    private val tv_driving_parking by lazy{ findViewById(R.id.tv_driving_parking_value) as? TextView }
    private val tv_accident by lazy{ findViewById(R.id.tv_accident_value) as? TextView }
    private val tv_battery by lazy{ findViewById(R.id.tv_battery_value) as? TextView }
    private val tv_temperature by lazy{ findViewById(R.id.tv_temperature_value) as? TextView }

    private val cp_change_period by lazy{findViewById(R.id.cp_change_period) as? CircularProgressBar }
    private val cp_mileage by lazy{findViewById(R.id.cp_mileage) as? CircularProgressBar }

    private val toolbar by lazy{findViewById(R.id.toolbar) as? Toolbar}

    private var mileage: Float = 0.0F
        set(value){
            field = value
            val spannableStringBuilder = SpannableStringBuilder("${field} Km")
            spannableStringBuilder.setSpan(RelativeSizeSpan(largeTextRatio), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv_mileage?.text = spannableStringBuilder
        }
    private var pd_hour: Int = 0
        set(value){
            field = value
            pd_minute = pd_minute
        }
    private var pd_minute: Int = 0
        set(value) {
            field = value
            var secondIdx: Int
            val spannableStringBuilder = SpannableStringBuilder("${field}")
            spannableStringBuilder.setSpan(RelativeSizeSpan(largeTextRatio), 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.append("h ")
            secondIdx = spannableStringBuilder.length
            spannableStringBuilder.append("$field")
            spannableStringBuilder.setSpan(RelativeSizeSpan(largeTextRatio), secondIdx, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.append("m")
            tv_driving_parking?.text = spannableStringBuilder
        }
    private var accidentCount: Int = 0
        set(value) {
            field = value
            val spannableStringBuilder = SpannableStringBuilder("${field}")
            spannableStringBuilder.setSpan(RelativeSizeSpan(largeTextRatio), 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.append("건")
            tv_accident?.text = spannableStringBuilder
        }
    private var battery: Int = 0
        set(value) {
            field = value
            val spannableStringBuilder = SpannableStringBuilder("${field}")
            spannableStringBuilder.setSpan(RelativeSizeSpan(largeTextRatio), 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.append("%")
            tv_battery?.text = spannableStringBuilder
        }
    private var temperature: Int = 0
        set(value) {
            field = value
            val spannableStringBuilder = SpannableStringBuilder("$field")
            spannableStringBuilder.setSpan(RelativeSizeSpan(largeTextRatio), 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.append("°C")
            tv_temperature?.text = spannableStringBuilder
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setSupportActionBar(toolbar)

        loadStatus()

        Utils.startAlisaService(this)

    }

    fun loadStatus(){
        mileage = 21.4F
        temperature = 48
        battery = 32
        pd_hour = 2
        pd_minute = 37
        accidentCount = 2

        cp_change_period?.setValue(70f)
        cp_mileage?.setValue(40f)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
            R.id.action_bluetooth -> {
                startActivity(Intent(this, DeviceSearchActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onChangePeriodClick(view: View){
        startActivity(Intent(this, ComponentStatusActivity::class.java))
    }

    fun onAccidentClick(view: View){
        startActivity(Intent(this, AccidentActivity::class.java))
    }

    fun onMileageClick(view: View){
        startActivity(Intent(this, MileageActivity::class.java))
    }

    fun onTrackingClick(view: View){

        startActivity(Intent(this, TrackingActivity::class.java))
    }

    fun showDebugPannel(view: View){
        startActivity(Intent(this, DebugPannelActivity::class.java))
    }

}
