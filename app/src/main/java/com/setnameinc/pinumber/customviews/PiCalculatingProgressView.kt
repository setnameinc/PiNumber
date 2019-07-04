package com.setnameinc.pinumber.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.setnameinc.pinumber.R
import com.setnameinc.pinumber.viewmodels.PiCalculatingProgressViewViewModel
import kotlinx.coroutines.*
import kotlin.math.sqrt
import kotlin.random.Random

class PiCalculatingProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var viewModel: PiCalculatingProgressViewViewModel

    private val numberOfPointPerPeriod = 5

    private val TAG = this::class.java.simpleName

    private var job: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        viewModel =
            ViewModelProviders.of(context as FragmentActivity)[PiCalculatingProgressViewViewModel::class.java]

    }

    private var inTheAreaPointPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.inTheArea)
        style = Paint.Style.FILL
        strokeWidth = 8f
    }

    private var outTheAreaPointPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.outTheArea)
        style = Paint.Style.FILL
        strokeWidth = 8f
    }

    private fun randomInit() {

        val coordX = Random.nextInt(width).toFloat()
        val coordY = Random.nextInt(height).toFloat()

        val isInCircle = sqrt(
            Math.pow(
                coordX.toDouble() - width / 2,
                2.toDouble()
            )
                    + Math.pow(coordY.toDouble() - height / 2, 2.toDouble())
        ) <= Math.min(width, height) / 2

        if (!viewModel.setOfCoordinates.contains((coordX to coordY) to isInCircle)) {

            viewModel.setOfCoordinates.add((coordX to coordY) to isInCircle)

        }

    }

    fun drawPoints() {

        if (!viewModel.isDrawingNow) {

            Log.i(TAG, "DrawPoint | start")

            viewModel.setOfCoordinates.clear()

            job?.cancel()

            viewModel.isDrawingNow = true
            viewModel.isDrawn = false

            startDraw()

        }

    }

    fun detach() {

        job?.cancel()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        Log.i(TAG, "OnDetached | ")

    }

    private fun startDraw() {

        if (viewModel.isDrawingNow && job?.isCancelled == null) {

            Log.i(TAG, "StartDraw | new coroutine")


        }

        job = coroutineScope.launch {

            repeat(500) {

                for (i in 0..numberOfPointPerPeriod) {
                    randomInit()
                }

                invalidate()

                delay(50)

            }
        }


    }

    fun stopDrawing() {

        Log.i(TAG, "StopDrawing | stop")

        viewModel.isDrawingNow = false
        viewModel.isDrawn = true

        job?.cancel()

        Log.i(TAG, "StopDrawing | list size is ${viewModel.setOfCoordinates.size}")

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // if something was drawn
        if (viewModel.setOfCoordinates.size > 0) {

            val listSize = viewModel.setOfCoordinates.size

            Log.i(TAG, "OnLayout | list size is ${viewModel.setOfCoordinates.size}")

            viewModel.setOfCoordinates.clear()

            job?.cancel()

            if (viewModel.isDrawingNow) {

                //update and start
                CoroutineScope(Dispatchers.Main).launch {

                    for (i in 0..listSize) {

                        randomInit()

                    }

                    invalidate()

                }

                startDraw()

            } else if (viewModel.isDrawn) {

                CoroutineScope(Dispatchers.Main).launch {

                    for (i in 0 until listSize) {

                        randomInit()

                    }

                    invalidate()

                }

            }

        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (viewModel.isDrawingNow || viewModel.isDrawn) {

            for (item in viewModel.setOfCoordinates) {

                if (item.second) {
                    canvas?.drawPoint(item.first.first, item.first.second, inTheAreaPointPaint)
                } else {
                    canvas?.drawPoint(item.first.first, item.first.second, outTheAreaPointPaint)
                }

            }

        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var heightMeasureSpec = heightMeasureSpec

        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY
            )
            else -> {
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

}