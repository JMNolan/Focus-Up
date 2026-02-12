package com.focusup.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.focusup.core.ui.theme.FocusUpTheme
import com.focusup.app.navigation.FocusUpNavHost
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusUpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FocusUpNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
