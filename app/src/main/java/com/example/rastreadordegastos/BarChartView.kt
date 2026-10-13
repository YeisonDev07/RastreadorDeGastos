package com.example.rastreadordegastos

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.util.Locale

data class BarData(val label: String, val value: Double, val max: Double, val color: Int)

class BarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bars: List<BarData> = emptyList()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E4E8E5")
        style = Paint.Style.FILL
    }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#17242A")
        textSize = 28f
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#718087")
        textSize = 26f
        textAlign = Paint.Align.RIGHT
    }

    private val rect = RectF()

    fun setData(data: List<BarData>) {
        this.bars = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bars.isEmpty()) return

        val paddingLeft = 16f
        val paddingRight = 16f
        var currentY = 20f
        val barHeight = 24f
        val rowHeight = 70f
        val availableWidth = width - paddingLeft - paddingRight

        bars.forEach { bar ->
            // Draw Label and Value
            canvas.drawText(bar.label, paddingLeft, currentY + 24f, labelPaint)
            val valStr = String.format(Locale.getDefault(), "$%.2f", bar.value)
            canvas.drawText(valStr, width - paddingRight, currentY + 24f, valuePaint)

            // Draw Bar Background
            val barTop = currentY + 36f
            rect.set(paddingLeft, barTop, width - paddingRight, barTop + barHeight)
            canvas.drawRoundRect(rect, 12f, 12f, bgPaint)

            // Draw Filled Bar
            val ratio = (bar.value / bar.max.coerceAtLeast(1.0)).coerceIn(0.0, 1.0).toFloat()
            val filledWidth = availableWidth * ratio
            if (filledWidth > 0) {
                rect.set(paddingLeft, barTop, paddingLeft + filledWidth, barTop + barHeight)
                barPaint.color = bar.color
                canvas.drawRoundRect(rect, 12f, 12f, barPaint)
            }

            currentY += rowHeight
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = bars.size.coerceAtLeast(1)
        val desiredHeight = (count * 70 * resources.displayMetrics.density).toInt() + 20
        val width = resolveSize((280 * resources.displayMetrics.density).toInt(), widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}
