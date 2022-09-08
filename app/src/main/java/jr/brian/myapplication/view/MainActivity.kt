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
import jr.brian.myapplication.data.model.local.FavColorsDao
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var dao: FavColorsDao? = null
        @Inject set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    dao?.let { AppUI(it) }
                }
            }
        }
    }
}

@Composable
fun AppUI(dao: FavColorsDao) {
    val navController = rememberNavController()
    val dest = if (dao.getStartUpPass().isNotEmpty()) "home_page" else "start_up_page"
    NavHost(navController = navController, startDestination = dest, builder = {
        composable(
            "home_page",
            content = {
                HomePage(
                    dao = dao,
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
            content = { FavColorPage(dao = dao) })
        composable(
            "start_up_page",
            content = {
                StartUpViewPager(onNavigateToHome = {
                    navController.navigate("home_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }, dao = dao)
            })
    })
}