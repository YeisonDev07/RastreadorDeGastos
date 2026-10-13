package com.example.rastreadordegastos

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Locale

class ReportesFragment : Fragment() {
    private val refreshListener = {
        if (isAdded && view != null) render(requireView())
    }

    private val palette = intArrayOf(
        Color.parseColor("#007F82"),
        Color.parseColor("#E87561"),
        Color.parseColor("#4A90E2"),
        Color.parseColor("#F5A623"),
        Color.parseColor("#7ED321"),
        Color.parseColor("#9013FE")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_reportes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render(view)
        ExpenseRepository.observe(refreshListener)
    }

    override fun onDestroyView() {
        ExpenseRepository.removeObserver(refreshListener)
        super.onDestroyView()
    }

    private fun render(view: View) {
        val expenses = ExpenseRepository.all()
        val totalSpent = expenses.sumOf { it.amount }
        val budget = ExpenseRepository.budget()

        val categoryGroup = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Donut Chart
        val donutChart = view.findViewById<DonutChartView>(R.id.donut_chart)
        donutChart.setData(if (categoryGroup.isEmpty()) mapOf("Sin gastos" to 1.0) else categoryGroup)

        // Legend
        val legendContainer = view.findViewById<LinearLayout>(R.id.category_legend)
        legendContainer.removeAllViews()

        val denominator = totalSpent.coerceAtLeast(1.0)
        var colorIdx = 0

        categoryGroup.forEach { (cat, amount) ->
            val percentage = (amount / denominator) * 100.0
            val color = palette[colorIdx % palette.size]
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 10, 0, 10)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(28, 28).apply { rightMargin = 16 }
                background = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(color)
                }
            }

            val nameTx = TextView(requireContext()).apply {
                text = cat
                setTextColor(Color.parseColor("#17242A"))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            }

            val detailTx = TextView(requireContext()).apply {
                text = String.format(Locale.getDefault(), "%.1f%%  ($%.2f)", percentage, amount)
                setTextColor(Color.parseColor("#718087"))
                textSize = 14f
            }

            row.addView(dot)
            row.addView(nameTx)
            row.addView(detailTx)
            legendContainer.addView(row)
            colorIdx++
        }

        // Bar Chart (Tendencia)
        val barChart = view.findViewById<BarChartView>(R.id.bar_chart)
        val maxVal = maxOf(budget, totalSpent, 1.0)

        val barEntries = mutableListOf(
            BarData("Presupuesto", budget, maxVal, Color.parseColor("#007F82")),
            BarData("Gastado Octub.", totalSpent, maxVal, Color.parseColor("#E87561"))
        )

        categoryGroup.forEach { (cat, amt) ->
            val color = palette[colorIdx % palette.size]
            barEntries.add(BarData(cat, amt, maxVal, color))
            colorIdx++
        }

        barChart.setData(barEntries)
    }
}
