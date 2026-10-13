package com.example.rastreadordegastos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class ConfiguracionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_configuracion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val budgetInput = view.findViewById<TextInputEditText>(R.id.budget_input)
        val alertsSwitch = view.findViewById<MaterialSwitch>(R.id.alerts_switch)
        val encryptionSwitch = view.findViewById<MaterialSwitch>(R.id.encryption_switch)
        val saveButton = view.findViewById<MaterialButton>(R.id.save_budget)

        // Load current settings
        budgetInput.setText(String.format(Locale.US, "%.2f", ExpenseRepository.budget()))
        alertsSwitch.isChecked = ExpenseRepository.isAlertsEnabled()
        encryptionSwitch.isChecked = ExpenseRepository.isEncryptionEnabled()

        saveButton.setOnClickListener {
            val budgetText = budgetInput.text?.toString()?.trim()?.replace(',', '.').orEmpty()
            val value = budgetText.toDoubleOrNull()

            if (value == null || value <= 0) {
                Toast.makeText(requireContext(), "Ingresa un monto de presupuesto válido mayor a 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ExpenseRepository.setBudget(value)
            ExpenseRepository.setAlertsEnabled(alertsSwitch.isChecked)
            ExpenseRepository.setEncryptionEnabled(encryptionSwitch.isChecked)

            Toast.makeText(requireContext(), "Configuración guardada correctamente", Toast.LENGTH_SHORT).show()
        }

        alertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            ExpenseRepository.setAlertsEnabled(isChecked)
            val msg = if (isChecked) "Alertas activadas" else "Alertas desactivadas"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        encryptionSwitch.setOnCheckedChangeListener { _, isChecked ->
            ExpenseRepository.setEncryptionEnabled(isChecked)
            val msg = if (isChecked) "Cifrado local simulado activado" else "Cifrado desactivado"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}
