package com.setnameinc.pinumber.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import com.setnameinc.pinumber.R
import kotlin.math.sqrt
import kotlin.random.Random

class PiCalculatingProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TAG = this::class.java.simpleName

    private val setOfCoords = mutableSetOf<Pair<Pair<Float, Float>, Boolean>>()

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

                val coordX = Random.nextInt(width).toFloat()
                val coordY = Random.nextInt(height).toFloat()

                val isInCircle = sqrt(
                    Math.pow(
                        coordX.toDouble() - width / 2,
                        2.toDouble()
                    ) + Math.pow(coordY.toDouble() - height / 2, 2.toDouble())
                ) <= height / 2

                if (!setOfCoords.contains((coordX to coordY) to isInCircle)) {

                    setOfCoords.add((coordX to coordY) to isInCircle)

                }

            }

            invalidate()

        }

    }

    fun drawPoints() {

        setOfCoords.clear()

        animator.start()

    }

    fun stopDrawing() {

        animator.cancel()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (item in setOfCoords) {

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

    private fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

}