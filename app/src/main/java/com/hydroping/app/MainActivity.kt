package com.hydroping.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.hydroping.app.ui.MainScreen
import com.hydroping.app.ui.MainViewModel
import com.hydroping.app.ui.theme.HydroPingTheme
import com.hydroping.app.utils.CsvExportHelper
import kotlinx.coroutines.launch

import android.content.Intent

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission handled
    }

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val csvContent = viewModel.getExportCsvData()
                val success = CsvExportHelper.writeCsvToUri(this@MainActivity, uri, csvContent)
                if (success) {
                    Toast.makeText(this@MainActivity, "Hydration records exported successfully! 💧", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to export records.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndRequestPermissions()
        handleIntent(intent)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            HydroPingTheme(themeMode = uiState.themeMode) {
                MainScreen(
                    viewModel = viewModel,
                    onExportCsv = {
                        createDocumentLauncher.launch("hydraping_hydration_records.csv")
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("EXTRA_SHOW_REMINDER", false) == true) {
            viewModel.showReminderModal(true)
        }
        if (intent?.getBooleanExtra("EXTRA_OPEN_LOG_DIALOG", false) == true) {
            viewModel.showCustomLogModal(true)
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
