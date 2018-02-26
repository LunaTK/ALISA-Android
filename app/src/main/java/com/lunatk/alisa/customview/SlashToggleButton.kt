package com.lunatk.alisa.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.lunatk.mybluetooth.R

/**
 * Created by LunaTK on 2018. 2. 25..
 */

class SlashToggleButton(context: Context, attrs: AttributeSet?) : View (context, attrs){

    val TAG = "DashToggleButton"

    //Color, Width Config
    private val BACKGROUND_COLOR = context.getColor(android.R.color.white)
    private val BACKGROUND_COLOR_ON = context.getColor(R.color.toggleOn)
    private val BACKGROUND_COLOR_OFF = context.getColor(R.color.toggleOff)
    private val BORDER_COLOR = context.getColor(R.color.borderColorSilver)
    private val LINE_WIDTH = 10f
    private var TEXT_SIZE = 80f

    private val DASH_GRADIENT = 3

    private var viewWidth:Int = 0
    private var viewHeight:Int = 0

    private val borderRect:Rect = Rect()
    private val onTextPoint = Point()
    private val offTextPoint = Point()

    private val fillRect = object {
        var leftTop = Point()
        var leftBottom = Point()
        var rightTop = Point()
        var rightBottom = Point()
        override fun toString(): String{
            return "${leftTop.toString()}, ${leftBottom.toString()}, ${rightTop.toString()}, ${rightBottom.toString()}"
        }
    }

    private var dragged: Boolean = false
    private val MAX_DRAG_VALUE:Int = 100
    private var originalDragValue:Int = 0
    private var dragValue:Int = 0 // 0~100
        set(value){
            Log.i(TAG,"set drag value to : $value")
            if(value<0) field = 0 else if(value>100) field = 100 else field = value
            calcFillRect()
            if(field!= value) dragged = true
            invalidate()
        }
    private val touchPoint = Point()

    private var onoff: Boolean = false

    private val borderPaint = Paint()
    private val fillPaint = Paint()

    init{
        borderPaint.strokeWidth = LINE_WIDTH
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = BORDER_COLOR

        fillPaint.style = Paint.Style.FILL
        fillPaint.textSize = TEXT_SIZE
        fillPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        fillPaint.textAlign = Paint.Align.CENTER

        setBackgroundColor(BACKGROUND_COLOR)
        isClickable = true


    }

    fun Path.moveTo(point: Point){
        moveTo(point.x.toFloat(), point.y.toFloat())
    }

    fun Path.lineTo(point: Point){
        lineTo(point.x.toFloat(), point.y.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val path = Path()

        canvas?.run {
            drawRect(borderRect, borderPaint)

            with(fillRect){
                path.moveTo(leftTop)
                path.lineTo(leftBottom)
                path.lineTo(rightBottom)
                path.lineTo(rightTop)
                path.close()
            }

            fillPaint.color = if(onoff) BACKGROUND_COLOR_ON else BACKGROUND_COLOR_OFF
            drawPath(path, fillPaint)

            fillPaint.color = if(onoff) Color.WHITE else Color.BLACK
            drawText("On", onTextPoint.x.toFloat(), onTextPoint.y.toFloat() , fillPaint)
            fillPaint.color = if(onoff) Color.BLACK else Color.WHITE
            drawText("Off", offTextPoint.x.toFloat(), offTextPoint.y.toFloat(), fillPaint)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean{
//        Log.i(TAG,"${event?.action}")
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                touchPoint.set(event.x.toInt(), event.y.toInt())
                originalDragValue = dragValue
            }
            MotionEvent.ACTION_MOVE->{
                dragValue = originalDragValue + ((event.x - touchPoint.x).toDouble()/viewWidth*100).toInt()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL->{
                if(dragged) { // drag animation
//                    Log.i(TAG,"drag animation")
                    with(ObjectAnimator.ofInt(this, "dragValue", dragValue, if (dragValue < (MAX_DRAG_VALUE / 2)) {
                        onoff = false; 0
                    } else {
                        onoff = true;MAX_DRAG_VALUE
                    })) {
                        setDuration(100)
                        setInterpolator(DecelerateInterpolator())
                        start()
                    }
                } else { // click animation
//                    Log.i(TAG,"click animation")
                    with(ObjectAnimator.ofInt(this, "dragValue", dragValue, if (event.x < viewWidth/2) {
                        onoff = false; 0
                    } else {
                        onoff = true;MAX_DRAG_VALUE
                    })) {
                        setDuration(100)
                        setInterpolator(DecelerateInterpolator())
                        start()
                    }
                }
                dragged = false
            }
        }
        return true
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

        borderRect.set(0,0,w,h)
        with(fillRect){
            leftTop.set(0,0)
            leftBottom.set(0,0)
            rightTop.set(w,0)
            rightBottom.set(w,h)
        }
        offTextPoint.set(w/4, h/2 - ((fillPaint.descent() + fillPaint.ascent()) / 2).toInt())
        onTextPoint.set(w/4*3, h/2 - ((fillPaint.descent() + fillPaint.ascent()) / 2).toInt())
//        TEXT_SIZE = viewHeight*3/4.toFloat()
        calcFillRect()
    }

    fun calcFillRect(){
        var widthOffset = viewWidth/2 * dragValue/MAX_DRAG_VALUE
        var offsetRight = (viewHeight/DASH_GRADIENT/2) * (MAX_DRAG_VALUE-dragValue)/MAX_DRAG_VALUE
        var offsetLeft = (viewHeight/DASH_GRADIENT/2) * dragValue/MAX_DRAG_VALUE
        with(fillRect){
            leftTop.set(0 + widthOffset + offsetLeft,0)
            leftBottom.set(0 + widthOffset - offsetLeft, viewHeight)
            rightTop.set(viewWidth/2 + widthOffset + offsetRight, 0)
            rightBottom.set(viewWidth/2 + widthOffset - offsetRight, viewHeight)
        }
        Log.i(TAG,"$fillRect")
    }
}
