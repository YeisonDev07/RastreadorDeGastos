package com.example.rastreadordegastos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Locale

class HistorialFragment : Fragment() {

    private lateinit var adapter: ExpenseAdapter
    private var selectedCategory: String = "Todas"

    private val refreshListener: () -> Unit = {
        if (isAdded && view != null) updateList(requireView())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.expenses_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ExpenseAdapter(emptyList()) { expense ->
            showDeleteDialog(expense)
        }
        recyclerView.adapter = adapter

        val chipGroup = view.findViewById<ChipGroup>(R.id.filter_chip_group)
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds.first())
                selectedCategory = chip?.text?.toString() ?: "Todas"
            } else {
                selectedCategory = "Todas"
            }
            updateList(view)
        }

        updateList(view)
        ExpenseRepository.observe(refreshListener)
    }

    override fun onDestroyView() {
        ExpenseRepository.removeObserver(refreshListener)
        super.onDestroyView()
    }

    private fun updateList(view: View) {
        val allExpenses = ExpenseRepository.all()
        val filtered = if (selectedCategory == "Todas") {
            allExpenses
        } else {
            allExpenses.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        adapter.update(filtered)

        val total = filtered.sumOf { it.amount }
        val periodTotalView = view.findViewById<TextView>(R.id.period_total)
        periodTotalView.text = String.format(Locale.getDefault(), "Total del período: $ %.2f", total)

        val emptyState = view.findViewById<TextView>(R.id.empty_state)
        emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDeleteDialog(expense: Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Gasto")
            .setMessage("¿Deseas eliminar el gasto \"${expense.description}\" de $ ${String.format(Locale.getDefault(), "%.2f", expense.amount)}?")
            .setPositiveButton("Eliminar") { _, _ ->
                ExpenseRepository.delete(expense.id)
                Toast.makeText(requireContext(), "Gasto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
