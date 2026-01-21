package com.literead.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.literead.databinding.ActivitySettingsBinding
import com.literead.theme.ThemeManager
import com.literead.utils.PreferencesManager
import com.literead.viewmodel.ReaderViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * SettingsActivity - Écran des paramètres
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: ReaderViewModel by viewModel()
    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val preferences = preferencesManager.getPreferences()

        // Thème
        when (preferences.theme) {
            com.literead.theme.ThemeManager.THEME_LIGHT -> binding.lightTheme.isChecked = true
            com.literead.theme.ThemeManager.THEME_DARK -> binding.darkTheme.isChecked = true
            com.literead.theme.ThemeManager.THEME_SEPIA -> binding.sepiaTheme.isChecked = true
            com.literead.theme.ThemeManager.THEME_AMOLED -> binding.amoledTheme.isChecked = true
        }

        // Taille police
        binding.fontSizeSeekBar.progress = preferences.fontSize

        // Mode lecture
        val isPageMode = preferences.readingMode == "page"
        if (isPageMode) {
            binding.pageMode.isChecked = true
        } else {
            binding.scrollMode.isChecked = true
        }
    }

    private fun setupClickListeners() {
        // Changement de thème
        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.lightTheme.id -> {
                    viewModel.setTheme(com.literead.theme.ThemeManager.THEME_LIGHT)
                    com.literead.theme.ThemeManager.applyTheme(this, com.literead.theme.ThemeManager.THEME_LIGHT)
                }
                binding.darkTheme.id -> {
                    viewModel.setTheme(com.literead.theme.ThemeManager.THEME_DARK)
                    com.literead.theme.ThemeManager.applyTheme(this, com.literead.theme.ThemeManager.THEME_DARK)
                }
                binding.sepiaTheme.id -> {
                    viewModel.setTheme(com.literead.theme.ThemeManager.THEME_SEPIA)
                    com.literead.theme.ThemeManager.applyTheme(this, com.literead.theme.ThemeManager.THEME_SEPIA)
                }
                binding.amoledTheme.id -> {
                    viewModel.setTheme(com.literead.theme.ThemeManager.THEME_AMOLED)
                    com.literead.theme.ThemeManager.applyTheme(this, com.literead.theme.ThemeManager.THEME_AMOLED)
                }
            }
        }

        // Taille police
        binding.fontSizeSeekBar.setOnSeekBarChangeListener(
            object : android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: android.widget.SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        binding.fontSizePreview.textSize = progress.toFloat()
                        viewModel.setFontSize(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar) {}
                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar) {}
            }
        )

        // Mode lecture
        binding.readingModeGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = if (checkedId == binding.pageMode.id) "page" else "scroll"
            viewModel.setReadingMode(mode)
        }
    }
}
