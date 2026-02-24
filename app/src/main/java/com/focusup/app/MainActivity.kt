package com.focusup.app
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
import android.telecom.TelecomManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.focusup.core.ui.theme.FocusUpTheme
import com.focusup.app.navigation.FocusUpNavHost
import com.focusup.feature.timer.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var timerViewModel: TimerViewModel
    private var isScreenOn = true

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d("MainActivity", "Screen turned OFF")
                    isScreenOn = false
                }
                Intent.ACTION_SCREEN_ON -> {
                    Log.d("MainActivity", "Screen turned ON")
                    isScreenOn = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        // Get ViewModel instance early
        timerViewModel = ViewModelProvider(this)[TimerViewModel::class.java]

        // Register screen state receiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)

        // Check initial screen state
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        isScreenOn = powerManager.isInteractive
        Log.d("MainActivity", "Initial screen state: isScreenOn=$isScreenOn")

        setContent {
            FocusUpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FocusUpNavHost(
                        modifier = Modifier.padding(innerPadding),
                        timerViewModel = timerViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called - isFinishing: $isFinishing, isChangingConfigurations: $isChangingConfigurations")
    }

    override fun onStop() {
        super.onStop()

        // Check screen state directly from PowerManager (more reliable than broadcast timing)
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenCurrentlyOn = powerManager.isInteractive

        Log.d("MainActivity", "onStop called - isFinishing: $isFinishing, isChangingConfigurations: $isChangingConfigurations, isScreenOn (cached): $isScreenOn, isScreenOn (actual): $isScreenCurrentlyOn, isInPhoneCall: ${isInPhoneCall()}")

        // Only fail timer if:
        // 1. Not finishing (app closing)
        // 2. Not configuration change (rotation, etc.)
        // 3. Screen is ON according to PowerManager (user actually switched apps, not just locked screen)
        // 4. Not in a phone call
        val shouldFailTimer = !isFinishing &&
                              !isChangingConfigurations &&
                              isScreenCurrentlyOn &&  // Use actual screen state, not cached
                              !isInPhoneCall()

        if (shouldFailTimer) {
            Log.d("MainActivity", "Calling failTimer() - user switched apps while screen was on")
            timerViewModel.failTimer()
        } else {
            Log.d("MainActivity", "Not failing timer - screen is off (locked) or other valid reason")
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")

        // Unregister screen receiver
        try {
            unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error unregistering receiver", e)
        }
    }

    private fun isInPhoneCall(): Boolean {
        return try {
            val telecomManager = getSystemService(TELECOM_SERVICE) as? TelecomManager
            telecomManager?.isInCall ?: false
        } catch (_: Exception) {
            false
        }
    }
}
