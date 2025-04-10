package io.dala.pandanow.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.dala.pandanow.presentation.screens.home.HomeScreen
import io.dala.pandanow.presentation.screens.player.VideoPlayerScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PandaNowNavHost() {
    val navController: NavHostController = rememberNavController()
    NavHost(
    navController = navController,
    startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(navController)
        }
        composable<VideoPlayerRoute> {
            val details = it.toRoute<VideoPlayerRoute>()
            VideoPlayerScreen(details, navController)
        }
    }
}