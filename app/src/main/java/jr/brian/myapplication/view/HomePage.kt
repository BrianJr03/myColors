package jr.brian.myapplication.view

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import jr.brian.myapplication.model.local.AppDatabase
import jr.brian.myapplication.model.remote.MyColorResponse
import jr.brian.myapplication.model.util.theme.BlueishIDK
import jr.brian.myapplication.model.util.theme.Teal200
import jr.brian.myapplication.model.util.theme.Transparent
import jr.brian.myapplication.viewmodel.MainViewModel
import kotlin.random.Random


val additionalInfo =
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
        "\nHue Color Range: 0 - 359\n",
        "* Double-Click to Copy\n* Long-Press to Save"
    )

@Composable
fun HomePage(
    context: Context,
    navController: NavController,
    appDB: AppDatabase,
    viewModel: MainViewModel = hiltViewModel()
) {
    var numOfColorsInput by remember { mutableStateOf("") }
    var colorInput by remember { mutableStateOf("") }
    val flowResponse by viewModel.flowResponse.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val shouldShowAvailColors = remember { mutableStateOf(false) }

    val gradient = Brush.horizontalGradient(
        listOf(BlueishIDK, Teal200),
        startX = 0.0f,
        endX = 1000.0f,
        tileMode = TileMode.Repeated
    )

    EnableInfoDialog(bool = shouldShowAvailColors)

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
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .focusRequester(focusRequester)
                    .fillMaxWidth(.5f)
                    .background(
                        brush = gradient,
                        shape = RoundedCornerShape(percent = 10)
                    ),
                onValueChange = { colorInput = it },
                label = { Text("Color or Hue #", color = Color.White) },
            )

            TextField(
                value = numOfColorsInput,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .focusRequester(focusRequester)
                    .background(
                        brush = gradient,
                        shape = RoundedCornerShape(percent = 10)
                    ),
                onValueChange = { numOfColorsInput = it },
                label = { Text("Count", color = Color.White) },
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
                        onClick = {
                            focusManager.clearFocus()
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
                                    if (
                                        colorInput !in additionalInfo
                                        && colorInput.toIntOrNull() !in 0..359
                                    ) {
                                        makeToast(context, "Displaying Random Colors")
                                    }
                                    viewModel.getColors(colorInput.lowercase().trim(), num)
                                } else {
                                    makeToast(context, "Please Specify a Color")
                                }
                            } else {
                                makeToast(context, "Please Specify a Count")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK),
                    ) {
                        Text(text = "Search", color = Color.White)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val random = Random.nextInt(2, 51)
                            colorInput = "Random"
                            numOfColorsInput = random.toString()
                            viewModel.getColors("random", random)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK),
                    ) {
                        Text(text = "Random", color = Color.White)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            navController.navigate("start_up_page") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK),
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "info",
                            tint = Color.White
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth(), onClick = {
                        focusManager.clearFocus()
                        shouldShowAvailColors.value = true
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK)
                ) {
                    Text(text = "View Additional Info", color = Color.White)
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        navController.navigate("fav_color_page") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK),
                ) {
                    Text(text = "View Favorites", color = Color.White)
                }

                if (loading) {
                    Spacer(modifier = Modifier.height(50.dp))
                    CircularProgressIndicator()
                }

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
    val clipboardManager = LocalClipboardManager.current
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                clipboardManager.setText(AnnotatedString(color.hex))
                                makeToast(context, "Copied ${color.hex}")
                            },
                            onLongPress = {
                                makeToast(context, "Saved ${color.hex}")
                                appDB
                                    .dao()
                                    .insertFavColor(color)
                            },
                        )
                    }
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
    title: String,
    content: @Composable (() -> Unit)?,
    confirmButton: @Composable () -> Unit,
    bool: MutableState<Boolean>
) {
    if (bool.value) {
        AlertDialog(
            onDismissRequest = { bool.value = false },
            title = { Text(title, fontSize = 18.sp, color = BlueishIDK) },
            text = content,
            confirmButton = confirmButton
        )
    }
}

@Composable
fun EnableInfoDialog(bool: MutableState<Boolean>) {
    ShowDialog(
        title = "Available Colors to Search",
        content = {
            Text(
                additionalInfo.joinToString("\n"),
                fontSize = 16.sp,
                color = BlueishIDK
            )
        },
        confirmButton = {
            Button(
                onClick = { bool.value = false },
                colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK)
            ) {
                Text(text = "OK", color = Color.White)
            }

        }, bool = bool
    )
}