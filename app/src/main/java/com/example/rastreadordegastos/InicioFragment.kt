package com.example.rastreadordegastos

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import java.util.Locale

class InicioFragment : Fragment() {

    private val refreshListener: () -> Unit = {
        if (isAdded && view != null) render(requireView())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.open_explorer).setOnClickListener {
            (activity as? MainActivity)?.openExplorer()
        }

        render(view)
        ExpenseRepository.observe(refreshListener)
    }

    override fun onDestroyView() {
        ExpenseRepository.removeObserver(refreshListener)
        super.onDestroyView()
    }

    private fun render(view: View) {
        val expenses = ExpenseRepository.all()
        val spent = expenses.sumOf { it.amount }
        val budget = ExpenseRepository.budget()
        val available = budget - spent

        // Available & Summary
        val availableText = view.findViewById<TextView>(R.id.available_amount)
        availableText.text = String.format(Locale.getDefault(), "$ %.2f", available)

        val summaryText = view.findViewById<TextView>(R.id.budget_summary)
        summaryText.text = String.format(Locale.getDefault(), "Gastado $ %.2f de $ %.2f", spent, budget)

        val progressBar = view.findViewById<ProgressBar>(R.id.budget_progress)
        val progressPercent = if (budget <= 0.0) 0 else ((spent / budget) * 100).coerceIn(0.0, 100.0).toInt()
        progressBar.progress = progressPercent

        // Change progress bar color if over budget
        if (spent > budget) {
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5252"))
        } else {
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E87561"))
        }

        // Category Quick Summary
        val categoryListContainer = view.findViewById<LinearLayout>(R.id.category_summary_list)
        categoryListContainer.removeAllViews()

        val categoriesGroup = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        if (categoriesGroup.isEmpty()) {
            val emptyTx = TextView(requireContext()).apply {
                text = "No hay gastos registrados aún"
                setTextColor(Color.parseColor("#718087"))
                setPadding(0, 8, 0, 8)
            }
            categoryListContainer.addView(emptyTx)
        } else {
            categoriesGroup.forEach { (cat, amount) ->
                val row = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 10, 0, 10)
                }

                val catText = TextView(requireContext()).apply {
                    text = cat
                    setTextColor(Color.parseColor("#17242A"))
                    textSize = 15f
                    layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
                }

                val amtText = TextView(requireContext()).apply {
                    text = String.format(Locale.getDefault(), "$ %.2f", amount)
                    setTextColor(Color.parseColor("#007F82"))
                    textSize = 15f
                    setTypeface(null, Typeface.BOLD)
                }

                row.addView(catText)
                row.addView(amtText)
                categoryListContainer.addView(row)
            }
        }

        // Recent Expenses List
        val recentListContainer = view.findViewById<LinearLayout>(R.id.recent_expenses)
        recentListContainer.removeAllViews()

        val recent = expenses.take(3)
        if (recent.isEmpty()) {
            val emptyTx = TextView(requireContext()).apply {
                text = "No hay gastos recientes"
                setTextColor(Color.parseColor("#718087"))
                setPadding(0, 8, 0, 8)
            }
            recentListContainer.addView(emptyTx)
        } else {
            recent.forEach { expense ->
                val row = layoutInflater.inflate(R.layout.item_gasto, recentListContainer, false)
                row.findViewById<TextView>(R.id.expense_description).text = expense.description
                row.findViewById<TextView>(R.id.expense_meta).text = "${expense.category}  •  ${expense.date}"
                row.findViewById<TextView>(R.id.expense_amount).text = String.format(Locale.getDefault(), "$ %.2f", expense.amount)
                recentListContainer.addView(row)
            }
        }
    }
}
