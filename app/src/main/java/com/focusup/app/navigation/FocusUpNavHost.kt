package com.focusup.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.focusup.feature.timer.TimerScreen
import com.focusup.feature.timer.TimerViewModel
import com.focusup.feature.stickerbook.StickerBookScreen

@Composable
fun FocusUpNavHost(
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "timer",
        modifier = modifier
    ) {
        composable("timer") {
            TimerScreen(
                onNavigateToStickerBook = {
                    navController.navigate("stickerbook")
                },
                viewModel = timerViewModel
            )
        }

        composable("stickerbook") {
            StickerBookScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
