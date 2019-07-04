package com.setnameinc.pinumber.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.setnameinc.pinumber.R
import com.setnameinc.pinumber.viewmodels.PiCalculatingProgressViewViewModel
import kotlin.math.sqrt
import kotlin.random.Random

class PiCalculatingProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var piCalculatingProgressViewViewModel: PiCalculatingProgressViewViewModel

    private val TAG = this::class.java.simpleName

    private var isFirstInit = true

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        piCalculatingProgressViewViewModel =
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

    private val animator = ValueAnimator.ofInt(0, 1).apply {

        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE

        addUpdateListener {

            for (i in 0..Random.nextInt(10) + 1) {
                randomInit()
            }

            invalidate()

        }

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

        if (!piCalculatingProgressViewViewModel.setOfCoords.contains((coordX to coordY) to isInCircle)) {

            piCalculatingProgressViewViewModel.setOfCoords.add((coordX to coordY) to isInCircle)

        }


    }

    fun drawPoints() {

        piCalculatingProgressViewViewModel.setOfCoords.clear()

        isFirstInit = true

        animator.start()

    }

    fun stopDrawing() {

        animator.cancel()

        isFirstInit = false

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // if something was drawn
        if (piCalculatingProgressViewViewModel.setOfCoords.size > 0 && isFirstInit) {

            val listSize = piCalculatingProgressViewViewModel.setOfCoords.size

            piCalculatingProgressViewViewModel.setOfCoords.clear()

            for (i in 0..listSize) {

                randomInit()

            }

            invalidate()

        }

        isFirstInit = false

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (item in piCalculatingProgressViewViewModel.setOfCoords) {

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