package com.focusup.app
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.focusup.core.ui.theme.FocusUpTheme
import com.focusup.app.navigation.FocusUpNavHost
import com.focusup.feature.timer.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var timerViewModel: TimerViewModel
    private var isScreenOn = true
    private var hasPhonePermission = false
    private var showPermissionRationale by mutableStateOf(false)

    // Register for permission result
    private val requestPhonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPhonePermission = isGranted
        Log.d("MainActivity", "Phone permission granted: $isGranted")
        if (!isGranted) {
            Log.w("MainActivity", "Phone permission denied - phone calls will still fail timer")
        }
        showPermissionRationale = false // Hide dialog after permission result
    }

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

        // Check and request phone permission if needed
        checkAndRequestPhonePermission()

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

                // Show permission rationale dialog if needed
                if (showPermissionRationale) {
                    PermissionRationaleDialog(
                        onAccept = {
                            Log.d("MainActivity", "User accepted rationale, requesting READ_PHONE_STATE permission")
                            requestPhonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        },
                        onDecline = {
                            Log.d("MainActivity", "User declined permission - phone calls will fail timer")
                            hasPhonePermission = false
                            showPermissionRationale = false
                        }
                    )
                }
            }
        }
    }

    private fun checkAndRequestPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPhonePermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPhonePermission) {
                // Show rationale dialog explaining why we need permission
                showPermissionRationale = true
            } else {
                Log.d("MainActivity", "Phone permission already granted")
            }
        } else {
            // Pre-Android 6.0, permissions are granted at install time
            hasPhonePermission = true
            Log.d("MainActivity", "Pre-M device, permission granted at install")
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

        // Log permission status before checking phone
        Log.d("MainActivity", "onStop - hasPhonePermission: $hasPhonePermission")

        val isPhoneRelated = isPhoneActivityActive()

        Log.d("MainActivity", "onStop called - isFinishing: $isFinishing, isChangingConfigurations: $isChangingConfigurations, isScreenOn (actual): $isScreenCurrentlyOn, hasPhonePermission: $hasPhonePermission, isPhoneRelated: $isPhoneRelated")

        // Only fail timer if:
        // 1. Not finishing (app closing)
        // 2. Not configuration change (rotation, etc.)
        // 3. Screen is ON according to PowerManager (user actually switched apps, not just locked screen)
        // 4. Not phone related (call, dialer, contacts)
        val shouldFailTimer = !isFinishing &&
                              !isChangingConfigurations &&
                              isScreenCurrentlyOn &&
                              !isPhoneRelated

        if (shouldFailTimer) {
            Log.d("MainActivity", "Calling failTimer() - user switched to non-phone app")
            timerViewModel.failTimer()
        } else {
            Log.d("MainActivity", "Not failing timer - screen off, phone activity, or valid reason")
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

    private fun isPhoneActivityActive(): Boolean {
        Log.d("MainActivity", "isPhoneActivityActive() called - hasPhonePermission: $hasPhonePermission")

        // If we don't have permission, we can't check phone state
        // Return false so timer behaves normally (may fail, but safe)
        if (!hasPhonePermission) {
            Log.d("MainActivity", "No phone permission, assuming not phone activity")
            return false
        }

        return try {
            // Check if actively in a call using TelecomManager
            val telecomManager = getSystemService(TELECOM_SERVICE) as? TelecomManager
            val isInCall = telecomManager?.isInCall ?: false
            Log.d("MainActivity", "TelecomManager.isInCall: $isInCall")

            // Check phone state using TelephonyManager (ringing, off-hook, etc.)
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as? TelephonyManager
            val callState = telephonyManager?.callState ?: TelephonyManager.CALL_STATE_IDLE
            val isPhoneActive = callState != TelephonyManager.CALL_STATE_IDLE

            val stateStr = when (callState) {
                TelephonyManager.CALL_STATE_RINGING -> "RINGING"
                TelephonyManager.CALL_STATE_OFFHOOK -> "OFFHOOK"
                else -> "IDLE"
            }
            Log.d("MainActivity", "TelephonyManager.callState: $stateStr, isPhoneActive: $isPhoneActive")

            val result = isInCall || isPhoneActive

            if (result) {
                Log.d("MainActivity", "Phone activity detected - isInCall: $isInCall, callState: $stateStr")
            } else {
                Log.d("MainActivity", "No phone activity detected")
            }

            result
        } catch (e: SecurityException) {
            Log.e("MainActivity", "SecurityException checking phone state - permission not granted", e)
            hasPhonePermission = false  // Update our flag
            false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking phone state", e)
            false
        }
    }
}

@androidx.compose.runtime.Composable
fun PermissionRationaleDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Prevent dismissal by tapping outside */ },
        title = {
            Text("Phone Call Detection")
        },
        text = {
            Text(
                "Focus Up needs permission to detect phone calls so your timer won't fail when you receive or make calls.\n\n" +
                "This permission is ONLY used to detect if you're in a call - we cannot and do not:\n" +
                "• Make calls for you\n" +
                "• Read your call history\n" +
                "• Access your contacts\n" +
                "• See who you're calling"
            )
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text("No Thanks")
            }
        }
    )
}
