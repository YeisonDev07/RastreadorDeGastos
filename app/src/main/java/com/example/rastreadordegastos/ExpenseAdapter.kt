package com.example.rastreadordegastos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onItemLongClick: ((Expense) -> Unit)? = null
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.expense_description)
        val meta: TextView = view.findViewById(R.id.expense_meta)
        val amount: TextView = view.findViewById(R.id.expense_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gasto, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.description.text = expense.description
        holder.meta.text = "${expense.category}  •  ${expense.date}"
        holder.amount.text = String.format(Locale.getDefault(), "$ %.2f", expense.amount)

        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(expense)
            true
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun update(items: List<Expense>) {
        expenses = items
        notifyDataSetChanged()
    }
}
