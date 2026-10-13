package com.example.rastreadordegastos

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object ExpenseRepository {
    private const val PREFS = "expense_store"
    private const val EXPENSES = "expenses"
    private const val BUDGET = "budget"
    private const val ALERTS = "alerts_enabled"
    private const val ENCRYPTION = "encryption_enabled"

    private var initialized = false
    private lateinit var context: Context
    private val listeners = mutableSetOf<() -> Unit>()

    fun init(appContext: Context) {
        if (initialized) return
        context = appContext.applicationContext
        initialized = true
        if (all().isEmpty()) {
            saveAll(
                listOf(
                    Expense(1, 42.50, "Comida", "2026-10-05", "Almuerzo de trabajo"),
                    Expense(2, 18.90, "Transporte", "2026-10-04", "Metro y autobus"),
                    Expense(3, 65.00, "Ocio", "2026-10-02", "Entrada al cine"),
                    Expense(4, 120.00, "Hogar", "2026-10-01", "Servicios de internet")
                )
            )
        }
    }

    fun all(): List<Expense> {
        if (!initialized) return emptyList()
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(EXPENSES, "[]") ?: "[]"
        val json = JSONArray(raw)
        return (0 until json.length()).map { index ->
            val item = json.getJSONObject(index)
            Expense(
                item.getLong("id"),
                item.getDouble("amount"),
                item.getString("category"),
                item.getString("date"),
                item.getString("description")
            )
        }.sortedByDescending { it.date }
    }

    fun add(expense: Expense) {
        saveAll(all() + expense)
        notifyListeners()
    }

    fun delete(id: Long) {
        saveAll(all().filterNot { it.id == id })
        notifyListeners()
    }

    fun budget(): Double =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getFloat(BUDGET, 1200f).toDouble()

    fun setBudget(value: Double) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putFloat(BUDGET, value.toFloat()).apply()
        notifyListeners()
    }

    fun isAlertsEnabled(): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(ALERTS, true)

    fun setAlertsEnabled(enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(ALERTS, enabled).apply()
        notifyListeners()
    }

    fun isEncryptionEnabled(): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(ENCRYPTION, false)

    fun setEncryptionEnabled(enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(ENCRYPTION, enabled).apply()
        notifyListeners()
    }

    fun observe(listener: () -> Unit) { listeners += listener }
    fun removeObserver(listener: () -> Unit) { listeners -= listener }

    private fun saveAll(expenses: List<Expense>) {
        val json = JSONArray()
        expenses.forEach { expense ->
            json.put(JSONObject().apply {
                put("id", expense.id)
                put("amount", expense.amount)
                put("category", expense.category)
                put("date", expense.date)
                put("description", expense.description)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(EXPENSES, json.toString()).apply()
    }

    private fun notifyListeners() = listeners.toList().forEach { it() }
}
