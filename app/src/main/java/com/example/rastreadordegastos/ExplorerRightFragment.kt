package com.example.rastreadordegastos

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.Locale

data class PhotoItem(val title: String, val description: String, val imageResId: Int)

class ExplorerRightFragment : Fragment() {

    private val mode: String by lazy { requireArguments().getString(KEY, "perfil") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_derecha, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.right_title)
        val body = view.findViewById<TextView>(R.id.right_body)
        val avatar = view.findViewById<ImageView>(R.id.profile_avatar)
        val container = view.findViewById<LinearLayout>(R.id.right_content)

        when (mode) {
            "perfil" -> showProfile(title, body, avatar)
            "fotos" -> showPhotos(container, title, body, avatar)
            "video" -> showVideo(container, title, body, avatar)
            "web" -> showWeb(container, title, body, avatar)
            "botones" -> showButtons(container, title, body, avatar)
            else -> showProfile(title, body, avatar)
        }
    }

    private fun showProfile(title: TextView, body: TextView, avatar: ImageView) {
        title.text = "Perfil del Usuario"
        avatar.visibility = View.VISIBLE

        val profileText = """
            Alex Rivera
            Desarrollador de Aplicaciones Móviles
            
            🎓 ESTUDIOS
            • Ingeniería de Sistemas y Computación
            • Especialización en Desarrollo Móvil para Android (Kotlin, Jetpack, Clean Architecture)
            • Diplomado en Diseño de Interfaces M3 y Experiencia de Usuario (UI/UX)
            
            💼 EXPERIENCIA LABORAL
            • Desarrollador Senior Android: Creación de aplicaciones financieras y de productividad con Kotlin y Room.
            • Arquitecto de Software Móvil: Diseño de soluciones sin dependencias externas pesadas, enfocadas en rendimiento y persistencia local eficiente.
            • Consultor UI/UX: Implementación de componentes nativos Material Design 3 y flujos dinámicos.
            
            📝 BIOGRAFÍA Y OBJETIVOS
            Apasionado por la tecnología móvil y la creación de herramientas digitales intuitivas. Este proyecto "Rastreador de Gastos" busca proporcionar un control financiero personal sencillo, rápido y sin complejidad en la nube.
        """.trimIndent()

        body.text = profileText
    }

    private fun showPhotos(container: LinearLayout, title: TextView, body: TextView, avatar: ImageView) {
        title.text = "Galería de Fotos"
        body.text = "Selecciona una foto para ampliar su detalle y descripción:"
        avatar.visibility = View.GONE

        val photos = listOf(
            PhotoItem("Escritorio de Trabajo", "Estación de desarrollo móvil donde se programa la app Rastreador de Gastos en Kotlin.", R.drawable.img_foto1),
            PhotoItem("Caminata de Montaña", "Momento de desconexión en la naturaleza para recargar ideas creativas.", R.drawable.img_foto2),
            PhotoItem("Boceto de la App", "Esquema inicial de la arquitectura dual y las pantallas de gestión de presupuesto.", R.drawable.img_foto3),
            PhotoItem("Espacio de Café", "Café matutino acompañando las sesiones de pruebas unitarias e interfaz.", R.drawable.img_foto4)
        )

        // Photo Detail Display Card
        val detailCard = MaterialCardView(requireContext()).apply {
            radius = 16f
            strokeWidth = 2
            setStrokeColor(Color.parseColor("#007F82"))
            setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 16 }
        }

        val detailLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val detailImg = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 140)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(photos.first().imageResId)
        }

        val detailTitle = TextView(requireContext()).apply {
            text = photos.first().title
            setTextColor(Color.parseColor("#17242A"))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 12, 0, 4)
        }

        val detailDesc = TextView(requireContext()).apply {
            text = photos.first().description
            setTextColor(Color.parseColor("#506168"))
            textSize = 13f
        }

        detailLayout.addView(detailImg)
        detailLayout.addView(detailTitle)
        detailLayout.addView(detailDesc)
        detailCard.addView(detailLayout)

        // RecyclerView for photos
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 12 }
            layoutManager = LinearLayoutManager(requireContext())
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                    val view = layoutInflater.inflate(R.layout.item_foto, parent, false)
                    return object : RecyclerView.ViewHolder(view) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val item = photos[position]
                    holder.itemView.findViewById<TextView>(R.id.photo_title).text = item.title
                    holder.itemView.findViewById<ImageView>(R.id.photo_image).setImageResource(item.imageResId)

                    holder.itemView.setOnClickListener {
                        detailImg.setImageResource(item.imageResId)
                        detailTitle.text = item.title
                        detailDesc.text = item.description
                    }
                }

                override fun getItemCount(): Int = photos.size
            }
        }

        container.addView(recyclerView)
        container.addView(detailCard)
    }

    private fun showVideo(container: LinearLayout, title: TextView, body: TextView, avatar: ImageView) {
        title.text = "Reproductor de Video"
        body.text = "Video interactivo con controles de reproducción (Play/Pausa/Navegación):"
        avatar.visibility = View.GONE

        val videoView = VideoView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 220).apply { topMargin = 16 }
        }

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Sample video stream URL
        val videoUri = Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
        videoView.setVideoURI(videoUri)

        val statusText = TextView(requireContext()).apply {
            text = "Video: For Bigger Escapes (Muestra de streaming)\nPresiona el video para ver los controles."
            setTextColor(Color.parseColor("#718087"))
            textSize = 12f
            setPadding(0, 12, 0, 0)
        }

        container.addView(videoView)
        container.addView(statusText)

        videoView.start()
    }

    private fun showWeb(container: LinearLayout, title: TextView, body: TextView, avatar: ImageView) {
        title.text = "Navegador Web Integradora"
        body.text = "Ingresa cualquier dirección URL para cargar el sitio web dentro de la app:"
        avatar.visibility = View.GONE

        val urlInput = EditText(requireContext()).apply {
            hint = "https://developer.android.com"
            setText("https://developer.android.com")
            setSingleLine(true)
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 12 }
        }

        val loadBtn = MaterialButton(requireContext()).apply {
            text = "Cargar sitio"
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 8 }
        }

        val webView = WebView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 380).apply { topMargin = 12 }
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        loadBtn.setOnClickListener {
            val input = urlInput.text.toString().trim()
            if (input.isNotBlank()) {
                val formattedUrl = if (input.startsWith("http://") || input.startsWith("https://")) {
                    input
                } else {
                    "https://$input"
                }
                webView.loadUrl(formattedUrl)
            }
        }

        // Auto load initial URL
        webView.loadUrl("https://developer.android.com")

        container.addView(urlInput)
        container.addView(loadBtn)
        container.addView(webView)
    }

    private fun showButtons(container: LinearLayout, title: TextView, body: TextView, avatar: ImageView) {
        title.text = "Demostración de Eventos"
        body.text = "Interactúa con los siguientes botones para probar eventos de UI:"
        avatar.visibility = View.GONE

        var clickCount = 0

        val feedbackCard = MaterialCardView(requireContext()).apply {
            radius = 16f
            strokeWidth = 2
            setStrokeColor(Color.parseColor("#007F82"))
            setCardBackgroundColor(Color.parseColor("#F4FAF9"))
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 16 }
        }

        val feedbackText = TextView(requireContext()).apply {
            text = "Estado actual: Sin interacciones aún."
            setTextColor(Color.parseColor("#17242A"))
            textSize = 14f
            setPadding(20, 20, 20, 20)
        }
        feedbackCard.addView(feedbackText)

        val btn1 = MaterialButton(requireContext()).apply {
            text = "Cambiar Mensaje"
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 16 }
            setOnClickListener {
                feedbackText.text = "¡Mensaje actualizado a las ${System.currentTimeMillis() % 100000}!"
                Toast.makeText(requireContext(), "Evento 'Cambiar Mensaje' ejecutado", Toast.LENGTH_SHORT).show()
            }
        }

        val btn2 = MaterialButton(requireContext()).apply {
            text = "Contador de Clics"
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 8 }
            setOnClickListener {
                clickCount++
                feedbackText.text = "Has presionado el botón $clickCount veces."
            }
        }

        val btn3 = MaterialButton(requireContext()).apply {
            text = "Mostrar Toast y Confirmación"
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 8 }
            setOnClickListener {
                Toast.makeText(requireContext(), "¡Interacción con botón exitosa!", Toast.LENGTH_LONG).show()
                feedbackText.text = "Última acción: Toast mostrado correctamente."
            }
        }

        val btn4 = MaterialButton(requireContext()).apply {
            text = "Alternar Color de Fondo"
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 8 }
            var toggled = false
            setOnClickListener {
                toggled = !toggled
                if (toggled) {
                    feedbackCard.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
                    feedbackCard.setStrokeColor(Color.parseColor("#E87561"))
                } else {
                    feedbackCard.setCardBackgroundColor(Color.parseColor("#F4FAF9"))
                    feedbackCard.setStrokeColor(Color.parseColor("#007F82"))
                }
                feedbackText.text = "Color de tarjeta alternado: ${if (toggled) "Naranja" else "Verde"}."
            }
        }

        container.addView(feedbackCard)
        container.addView(btn1)
        container.addView(btn2)
        container.addView(btn3)
        container.addView(btn4)
    }

    companion object {
        private const val KEY = "mode"
        fun newInstance(mode: String) = ExplorerRightFragment().apply {
            arguments = Bundle().apply { putString(KEY, mode) }
        }
    }
}
