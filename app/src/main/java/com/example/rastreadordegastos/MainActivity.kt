package com.example.rastreadordegastos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExpenseRepository.init(this)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_dual_screen) {
                openExplorer()
                true
            } else {
                false
            }
        }

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            showScreen(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            showScreen(R.id.nav_inicio)
        }
    }

    private fun showScreen(itemId: Int) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        val fragment: Fragment = when (itemId) {
            R.id.nav_historial -> HistorialFragment()
            R.id.nav_registro -> RegistroFragment()
            R.id.nav_reportes -> ReportesFragment()
            R.id.nav_configuracion -> ConfiguracionFragment()
            else -> InicioFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .commit()
    }

    fun openExplorer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, ExplorerFragment())
            .addToBackStack(null)
            .commit()
    }
}
