package com.example.rastreadordegastos

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.util.Locale

class DonutChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var categories: Map<String, Double> = emptyMap()
    private val colors = intArrayOf(
        Color.parseColor("#007F82"), // Teal
        Color.parseColor("#E87561"), // Coral
        Color.parseColor("#4A90E2"), // Blue
        Color.parseColor("#F5A623"), // Amber
        Color.parseColor("#7ED321"), // Green
        Color.parseColor("#9013FE")  // Purple
    )

    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#17242A")
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#718087")
        textSize = 26f
        textAlign = Paint.Align.CENTER
    }

    fun setData(data: Map<String, Double>) {
        this.categories = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (categories.isEmpty()) return

        val total = categories.values.sum().coerceAtLeast(1.0)
        val width = width.toFloat()
        val height = height.toFloat()
        val size = minOf(width, height)
        val strokeWidth = size * 0.18f

        slicePaint.strokeWidth = strokeWidth
        val padding = strokeWidth / 2f + 16f
        val rectF = RectF(
            (width - size) / 2f + padding,
            (height - size) / 2f + padding,
            (width + size) / 2f - padding,
            (height + size) / 2f - padding
        )

        var startAngle = -90f
        var colorIndex = 0

        categories.forEach { (_, amount) ->
            val sweepAngle = ((amount / total) * 360f).toFloat()
            slicePaint.color = colors[colorIndex % colors.size]
            canvas.drawArc(rectF, startAngle + 1f, sweepAngle - 2f, false, slicePaint)
            startAngle += sweepAngle
            colorIndex++
        }

        // Draw center text
        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawText(String.format(Locale.getDefault(), "$%.2f", total), centerX, centerY + 8f, textPaint)
        canvas.drawText("Total Gastado", centerX, centerY + 40f, subTextPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (220 * resources.displayMetrics.density).toInt()
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}
