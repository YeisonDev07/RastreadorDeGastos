package com.example.rastreadordegastos

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroFragment : Fragment() {

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_registro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amountInput = view.findViewById<TextInputEditText>(R.id.amount_input)
        val categoryInput = view.findViewById<AutoCompleteTextView>(R.id.category_input)
        val dateInput = view.findViewById<TextInputEditText>(R.id.date_input)
        val dateLayout = view.findViewById<TextInputLayout>(R.id.date_layout)
        val descriptionInput = view.findViewById<TextInputEditText>(R.id.description_input)
        val saveButton = view.findViewById<MaterialButton>(R.id.save_expense)

        // Setup Category Exposed Dropdown
        val categories = listOf("Comida", "Transporte", "Hogar", "Ocio", "Salud", "Educación", "Otros")
        categoryInput.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories))
        categoryInput.setText(categories.first(), false)

        // Setup Initial Date
        dateInput.setText(dateFormat.format(calendar.time))

        // DatePicker Dialog setup
        val openDatePicker = {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dateInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dateInput.setOnClickListener { openDatePicker() }
        dateLayout.setEndIconOnClickListener { openDatePicker() }

        // Save Button Click Listener
        saveButton.setOnClickListener {
            val amountText = amountInput.text?.toString()?.trim()?.replace(',', '.').orEmpty()
            val category = categoryInput.text?.toString()?.trim().orEmpty()
            val date = dateInput.text?.toString()?.trim().orEmpty()
            val description = descriptionInput.text?.toString()?.trim().orEmpty()

            val amount = amountText.toDoubleOrNull()

            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Por favor ingresa un monto válido mayor a 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (category.isBlank()) {
                Toast.makeText(requireContext(), "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date.isBlank()) {
                Toast.makeText(requireContext(), "Selecciona una fecha válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isBlank()) {
                Toast.makeText(requireContext(), "Ingresa una descripción del gasto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save Expense
            val newExpense = Expense(
                id = System.currentTimeMillis(),
                amount = amount,
                category = category,
                date = date,
                description = description
            )

            ExpenseRepository.add(newExpense)
            Toast.makeText(requireContext(), "Gasto registrado con éxito ($ ${String.format(Locale.getDefault(), "%.2f", amount)})", Toast.LENGTH_SHORT).show()

            // Reset inputs
            amountInput.text?.clear()
            descriptionInput.text?.clear()
            categoryInput.setText(categories.first(), false)
        }
    }
}
