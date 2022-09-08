package jr.brian.myapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.logincompose.ui.theme.LoginComposeTheme
import jr.brian.myapplication.data.model.local.AppDatabase

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getInstance(this.applicationContext)!!
        setContent {
            LoginComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppUI(appDatabase)
                }
            }
        }
    }
}

@Composable
fun AppUI(appDatabase: AppDatabase) {
    val navController = rememberNavController()
    val dest = if (appDatabase.dao().getStartUpPass().isNotEmpty()) "home_page" else "start_up_page"
    NavHost(navController = navController, startDestination = dest, builder = {
        composable(
            "home_page",
            content = {
                HomePage(
                    appDB = appDatabase,
                    onNavigateToStartUp = {
                        navController.navigate("start_up_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    onNavigateToFavorites = {
                        navController.navigate("fav_color_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            })
        composable(
            "fav_color_page",
            content = { FavColorPage(appDB = appDatabase) })
        composable(
            "start_up_page",
            content = {
                StartUpViewPager(onNavigateToHome = {
                    navController.navigate("home_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }, appDB = appDatabase)
            })
    })
}