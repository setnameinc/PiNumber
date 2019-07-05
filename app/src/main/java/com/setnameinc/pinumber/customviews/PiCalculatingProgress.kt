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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random

class PiCalculatingProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), PiCalculatingProgressView {

    private lateinit var viewModel: PiCalculatingProgressViewViewModel

    private val TAG = this::class.java.simpleName

    private val POINTS_PER_FRAME = 5
    private val FRAMES_PER_SECOND = 12
    private val MAX_AMOUNT_OF_SET = 3000
    private val COROUTINES_COUNT = 5

    private val inTheAreaPointPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.inTheArea)
        style = Paint.Style.FILL
        strokeWidth = 6f
    }
    private val outTheAreaPointPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.outTheArea)
        style = Paint.Style.FILL
        strokeWidth = 6f
    }

    private fun startDraw() {

        for (i in 0..COROUTINES_COUNT) {

            Log.i(TAG, "StartDraw | new coroutine")

            viewModel.compositeJob.add(CoroutineScope(Dispatchers.Main).launch {

                repeat(1500) {

                    for (i in 0..POINTS_PER_FRAME) {
                        generateRandomPoint()
                    }

                    checkForOverflow()

                    invalidate()

                    delay(1000L / FRAMES_PER_SECOND)

                }

            })

        }

    }

    override fun checkForOverflow() {

        if (!viewModel.isSetMax) {

            if (viewModel.setOfCoordinates.size > MAX_AMOUNT_OF_SET) {

                viewModel.isSetMax = true

            }

        }

        if (viewModel.isSetMax) {

            viewModel.setOfCoordinates.removeAll(viewModel.setOfCoordinates.filter { !it.second }.take(POINTS_PER_FRAME))

        }
    }

    override fun stopDrawing() {

        Log.i(TAG, "StopDrawing | stop")

        viewModel.isDrawingNow = false

        viewModel.compositeJob.cancel()

    }

    override fun drawPoints() {

        Log.i(TAG, "DrawPoint | start")

        viewModel.compositeJob.cancel()

        viewModel.setOfCoordinates.clear()

        startDraw()

        viewModel.isDrawingNow = true

    }

    //used to recalculate the previous screen mode X, Y
    private fun recalculateCoordinates() {

        val setOfCoordinatesSize = viewModel.setOfCoordinates.size

        Log.i(TAG, "list six = ${viewModel.setOfCoordinates.size}")

        viewModel.setOfCoordinates.clear()

        for (i in 0..setOfCoordinatesSize) {

            generateRandomPoint()

        }

        invalidate()


    }

    private fun generateRandomPoint() {

        val coordX = 2 * Random.nextInt(width).toFloat()
        val coordY = 2 * Random.nextInt(height).toFloat()

        // speed medium
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

    override fun onLayout(changedOrientation: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changedOrientation, left, top, right, bottom)

        if (viewModel.setOfCoordinates.size > 0 && changedOrientation) {

            recalculateCoordinates()

            if (viewModel.isDrawingNow) {

                startDraw()

            }


        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (item in viewModel.setOfCoordinates) {

            if (item.second) {
                canvas?.drawPoint(item.first.first, item.first.second, inTheAreaPointPaint)
            } else {
                canvas?.drawPoint(item.first.first, item.first.second, outTheAreaPointPaint)
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

    override fun onDetachedFromWindow() {
        viewModel.compositeJob.cancel()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        viewModel =
            ViewModelProviders.of(context as FragmentActivity)[PiCalculatingProgressViewViewModel::class.java]

    }

}

interface PiCalculatingProgressView {

    fun drawPoints()
    fun stopDrawing()

    fun checkForOverflow()

}
