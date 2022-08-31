package jr.brian.myapplication.view

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import jr.brian.myapplication.model.remote.MyColorResponse
import jr.brian.myapplication.model.util.UserDataStore
import jr.brian.myapplication.model.util.theme.BlueishIDK
import jr.brian.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    context: Context,
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var numOfColorsInput by remember { mutableStateOf("") }
    var colorInput by remember { mutableStateOf("") }
    val flowResponse by viewModel.flowResponse.collectAsState()

//    val liveData by viewModel.colorsLiveData.observeAsState()

    val shouldShowAvailColors = remember { mutableStateOf(false) }
    val shouldShowLogout = remember { mutableStateOf(false) }

    BackHandler {
        shouldShowLogout.value = true
    }

    EnableColorsDialog(bool = shouldShowAvailColors)
    EnableLogoutDialog(context = context, bool = shouldShowLogout, navController = navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TextField(
                value = colorInput,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(.5f),
                onValueChange = { colorInput = it },
                label = { Text("Color or Hue #") },
            )
            TextField(
                value = numOfColorsInput,
                modifier = Modifier.padding(5.dp),
                onValueChange = { numOfColorsInput = it },
                label = { Text("Count") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(), onClick = {
                        numOfColorsInput.toIntOrNull()?.let {
                            var num = it
                            if (num < 2) {
                                num = 2
                                numOfColorsInput = num.toString()
                            } else if (num > 51) {
                                num = 51
                                numOfColorsInput = num.toString()
                            }
                            if (colorInput.isNotEmpty()) {
//                            viewModel.getColorsRx(colorInput.lowercase().trim(), num)
                                viewModel.getColors(colorInput.lowercase().trim(), num)
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueishIDK
                    )
                ) {
                    Text(text = "Search Colors", color = Color.White)
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(), onClick = {
                        shouldShowAvailColors.value = true
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueishIDK
                    )
                ) {
                    Text(text = "Available Colors", color = Color.White)
                }
//                liveData?.let { ColorsList(it) }
                flowResponse?.let { ColorsList(it) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorsList(
    colors: MyColorResponse
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color(parseColor(color.hex))),
            ) {
                Text(color.hex, color = Color.Black)
            }
        }
    }
}

@Composable
fun ShowDialog(
    title: String, text: String, confirmButton: @Composable () -> Unit, bool: MutableState<Boolean>
) {
    if (bool.value) {
        AlertDialog(
            onDismissRequest = { bool.value = false },
            title = { Text(title) },
            text = { Text(text, color = Color.White, fontSize = 16.sp) },
            confirmButton = confirmButton
        )
    }
}

@Composable
fun EnableLogoutDialog(context: Context, bool: MutableState<Boolean>, navController: NavController) {
    val scope = rememberCoroutineScope()
    val dataStore = UserDataStore(context)
    ShowDialog(
        title = "Logout",
        text = "This will log you out.",
        confirmButton = {
            Button(
                onClick = {
                    bool.value = false
                    scope.launch { dataStore.clear() }
                    navController.navigate("login_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = BlueishIDK
                )
            ) {
                Text(text = "Logout", color = Color.White)
            }

        }, bool = bool
    )
}

@Composable
fun EnableColorsDialog(bool: MutableState<Boolean>) {
    val colors =
        listOf(
            "Red",
            "Pink",
            "Purple",
            "Navy",
            "Blue",
            "Aqua",
            "Green",
            "Lime",
            "Yellow",
            "Orange",
            "\nHue Color Range: 0 - 359"
        )
    ShowDialog(
        title = "Available Colors to Search",
        text = colors.joinToString("\n"),
        confirmButton = {
            Button(
                onClick = { bool.value = false },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = BlueishIDK
                )
            ) {
                Text(text = "OK", color = Color.White)
            }

        }, bool = bool
    )
}

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