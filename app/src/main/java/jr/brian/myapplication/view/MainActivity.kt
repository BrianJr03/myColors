package jr.brian.myapplication.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.logincompose.ui.theme.LoginComposeTheme
import jr.brian.myapplication.model.util.UserDataStore

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppUI(applicationContext)
                }
            }
        }
    }
}

@Composable
fun AppUI(context: Context) {
    val navController = rememberNavController()
    var dest = "login_page"
    val dataStore = UserDataStore(context)
    val savedEmail = dataStore.getEmail.collectAsState(initial = "")
    val savedPass = dataStore.getPassword.collectAsState(initial = "")
    if (!savedEmail.value.isNullOrEmpty() && !savedPass.value.isNullOrEmpty()) {
        dest = "home_page"
    }
    NavHost(navController = navController, startDestination = dest, builder = {
        composable(
            "login_page",
            content = { LoginPage(navController = navController, context = context) })
        composable(
            "register_page",
            content = { RegistrationPage(navController = navController, context = context) })
        composable(
            "home_page",
            content = { MainScreen(navController = navController, context = context) })
    })
}