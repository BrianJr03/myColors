package jr.brian.myapplication.view

import android.content.Context
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
import jr.brian.myapplication.model.local.AppDatabase

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
                    AppUI(applicationContext, appDatabase)
                }
            }
        }
    }
}

@Composable
fun AppUI(context: Context, appDatabase: AppDatabase) {
    val navController = rememberNavController()
    val dest = "home_page"
    NavHost(navController = navController, startDestination = dest, builder = {
        composable(
            "home_page",
            content = {
                MainScreen(
                    navController = navController,
                    context = context,
                    appDB = appDatabase
                )
            })
        composable(
            "fav_color_page",
            content = {
                FavColorScreen(
                    context = context,
                    appDB = appDatabase,
                )
            })
    })
}