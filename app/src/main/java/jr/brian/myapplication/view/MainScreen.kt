package jr.brian.myapplication.view

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import jr.brian.myapplication.model.local.AppDatabase
import jr.brian.myapplication.model.remote.MyColorResponse
import jr.brian.myapplication.model.util.theme.BlueishIDK
import jr.brian.myapplication.viewmodel.MainViewModel

@Composable
fun MainScreen(
    context: Context,
    navController: NavController,
    appDB: AppDatabase,
    viewModel: MainViewModel = hiltViewModel()
) {
    var numOfColorsInput by remember { mutableStateOf("") }
    var colorInput by remember { mutableStateOf("") }
    val flowResponse by viewModel.flowResponse.collectAsState()
    val progress by viewModel.progress.collectAsState()

//    val liveData by viewModel.colorsLiveData.observeAsState()

    val shouldShowAvailColors = remember { mutableStateOf(false) }

    EnableColorsDialog(bool = shouldShowAvailColors)

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
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(.5f), onClick = {
                            if (numOfColorsInput.toIntOrNull() != null) {
                                var num = numOfColorsInput.toInt()
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
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please Specify a Color",
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
                                }
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Please Specify a Count",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = BlueishIDK
                        )
                    ) {
                        Text(text = "Search", color = Color.White)
                    }

                    Button(
                        onClick = {
                            colorInput = ""
                            if (numOfColorsInput.toIntOrNull() != null) {
                                var num = numOfColorsInput.toInt()
                                if (num < 2) {
                                    num = 2
                                    numOfColorsInput = num.toString()
                                } else if (num > 51) {
                                    num = 51
                                    numOfColorsInput = num.toString()
                                }
                                viewModel.getColors("random", num)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Please Specify a Count",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = BlueishIDK
                        )
                    ) {
                        Text(text = "Random", color = Color.White)
                    }
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

                Button(
                    modifier = Modifier
                        .fillMaxWidth(), onClick = {
                        navController.navigate("fav_color_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueishIDK
                    )
                ) {
                    Text(text = "View Favorites", color = Color.White)
                }

                if (progress) {
                    Spacer(modifier = Modifier.height(50.dp))
                    CircularProgressIndicator()
                }
//                liveData?.let { ColorsList(it) }
                flowResponse?.let { ColorsList(context, appDB, it) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorsList(
    context: Context,
    appDB: AppDatabase,
    colors: MyColorResponse
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            Toast
                                .makeText(context, "Saved", Toast.LENGTH_LONG)
                                .show()
                            appDB
                                .dao()
                                .insertFavColor(color)
                        }) {}
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