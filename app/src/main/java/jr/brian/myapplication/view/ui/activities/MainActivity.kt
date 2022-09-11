package jr.brian.myapplication.view.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.myapplication.data.model.local.FavColorsDao
import jr.brian.myapplication.util.MyDataStore
import jr.brian.myapplication.view.ui.composables.FavColorPage
import jr.brian.myapplication.view.ui.composables.HomePage
import jr.brian.myapplication.view.ui.composables.StartUpViewPager
import jr.brian.myapplication.view.ui.theme.ComposeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var dao: FavColorsDao? = null
        @Inject set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    dao?.let {
                        AppUI(it, MyDataStore(this))
                    }
                }
            }
        }
    }
}

@Composable
fun AppUI(dao: FavColorsDao, dataStore: MyDataStore) {
    val navController = rememberNavController()
    val didPassStartUp = dataStore.getStartUpPassStatus.collectAsState(initial = false)
    val dest = if (didPassStartUp.value == true) "home_page" else "start_up_page"
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
                StartUpViewPager {
                    navController.navigate("home_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            })
    })
}