package com.lunatk.alisa.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.lunatk.alisa.R

/**
 * Created by LunaTK on 2018. 2. 18..
 */

class ComponentStatusBar : View {

    companion object {
        private val animationDuration: Long = 1700
        val maxBlockNum = 10
        private val blockOffsetX = 1
        private val blockOffsetY = 1
        private val TAG = "PeriodStatusBar"
    }

    var useAnimation: Boolean = true
    private var value_anim: Int = 100
    var value: Int = 100 // in percentage form(%)
        set(value){
            field = value;
            if(useAnimation) {
                with(object : Animation() {
                    var beforeBlockCount = maxBlockNum
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        super.applyTransformation(interpolatedTime, t)
                        value_anim = 100 - ((100-value) * interpolatedTime).toInt()
                        if (beforeBlockCount != (maxBlockNum.toDouble() * value_anim / 100).toInt()) {
                            invalidate()
                            beforeBlockCount = (maxBlockNum.toDouble() * value_anim / 100).toInt()
                        }
                    }
                }) {
                    duration = animationDuration
                    setInterpolator(android.view.animation.DecelerateInterpolator())
                    startAnimation(this)
                }
            } else {
                invalidate()
            }
        }

    private lateinit var blockRed: Bitmap
    private lateinit var blockYellow: Bitmap
    private lateinit var blockGreen: Bitmap

    private lateinit var mPaint: Paint

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    constructor(context: Context) : super(context) {
        _init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        _init(context)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        Log.d(TAG, "width : $viewWidth, height : $viewHeight")
        for (i in 0..(maxBlockNum.toDouble()*value_anim/100).toInt()){
            canvas?.drawBitmap(
                    if(value<=20) {blockRed} else if(value<=50) {blockYellow} else {blockGreen},
                    null,
                    rectForIthBlock(i),
                    mPaint)
        }

    }

    fun rectForIthBlock(i: Int): Rect{
        val startX = ( viewWidth.toDouble() / maxBlockNum * i ).toInt() + blockOffsetX
        val endX = ( viewWidth.toDouble() / maxBlockNum * (i+1) ).toInt() - blockOffsetX
        val startY = blockOffsetY
        val endY = height - blockOffsetY
        return Rect(startX, startY, endX, endY)
    }

    fun _init(context: Context){
        blockRed = BitmapFactory.decodeResource(getResources(), R.drawable.block_red)
        blockYellow = BitmapFactory.decodeResource(getResources(), R.drawable.block_yellow)
        blockGreen = BitmapFactory.decodeResource(getResources(), R.drawable.block_green)
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    private fun getMeasurementSize(measureSpec: Int, defaultSize: Int): Int {
        val mode = View.MeasureSpec.getMode(measureSpec)
        val size = View.MeasureSpec.getSize(measureSpec)
        when (mode) {
            View.MeasureSpec.EXACTLY -> return size

            View.MeasureSpec.AT_MOST -> return Math.min(defaultSize, size)

            View.MeasureSpec.UNSPECIFIED -> return defaultSize
            else -> return defaultSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getMeasurementSize(widthMeasureSpec, 300)
        val height = getMeasurementSize(heightMeasureSpec, 50)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
    }

    class chargeAnimation: Animation{

        constructor(duration: Long):super(){
            setDuration(duration)
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
        }
    }

}
