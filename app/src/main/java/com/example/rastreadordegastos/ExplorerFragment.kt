package com.example.rastreadordegastos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ExplorerFragment : Fragment(), ExplorerMenuListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_explorer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.left_panel, ExplorerLeftFragment())
                .replace(R.id.right_panel, ExplorerRightFragment.newInstance("perfil"))
                .commit()
        }
    }

    override fun selectExplorer(mode: String) {
        childFragmentManager.beginTransaction()
            .replace(R.id.right_panel, ExplorerRightFragment.newInstance(mode))
            .commit()
    }
}

interface ExplorerMenuListener {
    fun selectExplorer(mode: String)
}
