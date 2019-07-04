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
import io.reactivex.disposables.CompositeDisposable
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

    private var job:Job? = null

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

        for (i in 0..numberOfPointPerPeriod) {

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

    }

    fun drawPoints() {

        Log.i(TAG, "DrawPoint | start")

        if (!viewModel.isAvailableForDrawing) {

            viewModel.setOfCoordinates.clear()

            startDraw()

            viewModel.isAvailableForDrawing = true

        }

    }

    private fun startDraw(){

        //it's faster than thread
        job = CoroutineScope(Dispatchers.Main).launch {

            val job = repeat(20) {

                randomInit()
                invalidate()

                delay(50)

            }

        }

    }

    fun stopDrawing() {

        Log.i(TAG, "StopDrawing | stop")

        job?.cancel()

        viewModel.isAvailableForDrawing = false

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // if something was drawn
        if (viewModel.setOfCoordinates.size > 0 /*&& !viewModel.isAvailableForDrawing*/) {

            Log.i(TAG, "onLayout | ")

            val listSize = viewModel.setOfCoordinates.size

            viewModel.setOfCoordinates.clear()

            CoroutineScope(Dispatchers.Main).launch {

                job?.cancel()

                for (i in 0..listSize / numberOfPointPerPeriod) {

                    randomInit()

                }

                invalidate()

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

}