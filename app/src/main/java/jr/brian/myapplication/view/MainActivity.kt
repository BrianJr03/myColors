package jr.brian.myapplication.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.ColorInt
import androidx.annotation.Size
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
    var dest = "start_up_page"
    if (appDatabase.dao().getStartUpPass().isNotEmpty()) {
        dest = "home_page"
    }
    NavHost(navController = navController, startDestination = dest, builder = {
        composable(
            "home_page",
            content = {
                HomePage(
                    navController = navController,
                    context = context,
                    appDB = appDatabase
                )
            })
        composable(
            "fav_color_page",
            content = { FavColorPage(appDB = appDatabase) })
        composable(
            "start_up_page",
            content = { StartUpViewPager(navController = navController, appDB = appDatabase) })
    })
}

fun makeToast(context: Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

@ColorInt
fun parseColor(@Size(min = 1) colorString: String): Int {
    val error = "Unknown Color"
    if (colorString[0] == '#') {
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            color = color or -0x1000000
        } else require(colorString.length == 9) { error }
        return color.toInt()
    }
    throw IllegalArgumentException(error)
}