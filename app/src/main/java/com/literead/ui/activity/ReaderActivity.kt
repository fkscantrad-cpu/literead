package com.literead.ui.activity

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.literead.databinding.ActivityReaderBinding
import com.literead.reader.DocumentReaderFactory
import com.literead.theme.ThemeManager
import com.literead.utils.PreferencesManager
import com.literead.viewmodel.ReaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * ReaderActivity - Écran de lecture des documents
 */
class ReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReaderBinding
    private val viewModel: ReaderViewModel by viewModel()
    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration de la fenêtre
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Appliquer le thème
        val preferences = preferencesManager.getPreferences()
        ThemeManager.applyTheme(this, preferences.theme)
        applyBrightness(preferences.brightness)

        // Ouvrir le fichier
        openDocument()
        setupObservers()
        setupClickListeners()
    }

    private fun openDocument() {
        val uri: Uri? = intent.data
        val documentId: Long? = intent.getLongExtra("document_id", -1).takeIf { it != -1L }

        if (uri != null) {
            // Ouvert depuis une autre app
            openFromUri(uri)
        } else if (documentId != null) {
            // Ouvert depuis la bibliothèque
            viewModel.loadDocument(documentId)
        }
    }

    private fun openFromUri(uri: Uri) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                binding.readerContainer.removeAllViews()
            }

            try {
                val mimeType = contentResolver.getType(uri) ?: "application/pdf"
                val reader = DocumentReaderFactory.createReader(mimeType)
                val cacheFile = getCacheDir().absolutePath + "/temp_doc"
                
                // Copier le fichier en cache
                contentResolver.openInputStream(uri)?.use { input ->
                    java.io.File(cacheFile).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                if (reader.openDocument(this@ReaderActivity, cacheFile)) {
                    withContext(Dispatchers.Main) {
                        // Afficher le document
                        displayDocument()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displayDocument() {
        // Implementation du rendu du document
        val uri = intent.data
        if (uri != null) {
            // Rendu basé sur le type MIME
            val mimeType = contentResolver.getType(uri) ?: "application/pdf"
            when {
                mimeType.contains("pdf", ignoreCase = true) -> {
                    // Logic for PDF display
                }
                mimeType.contains("epub", ignoreCase = true) -> {
                    // Logic for EPUB display
                }
                else -> {
                    // Logic for Text/MOBI display
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.currentPage.observe(this) { page ->
            binding.pageCounter.text = "Page: $page"
        }

        viewModel.preferences.observe(this) { preferences ->
            applyBrightness(preferences.brightness)
        }
    }

    private fun setupClickListeners() {
        binding.searchBtn.setOnClickListener {
            // Implémentation simple de la recherche (toggle UI ou dialogue)
        }

        binding.bookmarkBtn.setOnClickListener {
            viewModel.currentPage.value?.let { page ->
                viewModel.toggleBookmark(page)
            }
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }
    }

    private fun applyBrightness(brightness: Float) {
        val params = window.attributes
        params.screenBrightness = brightness
        window.attributes = params
    }

    override fun onDestroy() {
        super.onDestroy()
        // Nettoyer les ressources
    }
}
