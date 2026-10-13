package com.example.rastreadordegastos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ExplorerLeftFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_izquierda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actions = mapOf(
            R.id.menu_profile to "perfil",
            R.id.menu_photos to "fotos",
            R.id.menu_video to "video",
            R.id.menu_web to "web",
            R.id.menu_buttons to "botones"
        )

        actions.forEach { (id, mode) ->
            view.findViewById<View>(id)?.setOnClickListener {
                (parentFragment as? ExplorerMenuListener)?.selectExplorer(mode)
            }
        }
    }
}
