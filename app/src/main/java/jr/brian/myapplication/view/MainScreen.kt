package jr.brian.myapplication.view

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import jr.brian.myapplication.model.remote.MyColorResponse
import jr.brian.myapplication.model.util.UserDataStore
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

    val liveData by viewModel.colorsLiveData.observeAsState()

    val scope = rememberCoroutineScope()
    val dataStore = UserDataStore(context)

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
                label = { Text("Color") }
            )
            TextField(
                value = numOfColorsInput,
                modifier = Modifier.padding(5.dp),
                onValueChange = { numOfColorsInput = it },
                label = { Text("Number") }
            )
        }

        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Button(modifier = Modifier
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
//                            viewModel.getColorsRx(colorInput.lowercase(), num)
                            viewModel.getColors(colorInput.lowercase(), num)
                        }
                    }
                }) {
                    Text(text = "View Colors", color = Color.White)
                }
                Button(modifier = Modifier
                    .fillMaxWidth(), onClick = {
                    scope.launch { dataStore.clear() }
                    navController.navigate("login_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }) {
                    Text(text = "Logout", color = Color.White)
                }
                liveData?.let {
                    ColorsList(it)
                }
                flowResponse?.let {
                    ColorsList(it)
                }
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
        cells = GridCells.Adaptive(100.dp)
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

@ColorInt
fun parseColor(@Size(min = 1) colorString: String): Int {
    if (colorString[0] == '#') {
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            color = color or -0x1000000
        } else require(colorString.length == 9) { "Unknown color" }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}