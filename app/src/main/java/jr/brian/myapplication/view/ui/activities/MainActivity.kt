package jr.brian.myapplication.view.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.myapplication.data.model.local.FavColorsDao
import jr.brian.myapplication.util.MyDataStore
import jr.brian.myapplication.view.ui.pages.FavColorPage
import jr.brian.myapplication.view.ui.pages.HomePage
import jr.brian.myapplication.view.ui.pages.StartUpPage
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppUI(dao: FavColorsDao, dataStore: MyDataStore) {
    val navController = rememberAnimatedNavController()
    val didPassStartUp = dataStore.getStartUpPassStatus.collectAsState(initial = false)
    val dest = if (didPassStartUp.value == true) "home_page" else "start_up_page"
    AnimatedNavHost(
        navController = navController,
        startDestination = dest,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        },
        builder = {
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
                    StartUpPage {
                        navController.navigate("home_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                })
        })
}