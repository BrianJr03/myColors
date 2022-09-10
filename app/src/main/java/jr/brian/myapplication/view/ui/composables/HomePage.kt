package jr.brian.myapplication.view.ui.composables

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import jr.brian.myapplication.data.model.local.FavColorsDao
import jr.brian.myapplication.data.model.remote.MyColorResponse
import jr.brian.myapplication.util.makeToast
import jr.brian.myapplication.util.parseColor
import jr.brian.myapplication.util.theme.BlueishIDK
import jr.brian.myapplication.util.theme.Teal200
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
        "Random",
        "\nHue Color Range: 0 - 359\n",
        "* Double-Click to Copy\n* Long-Press to Save"
    )


@Composable
fun HomePage(
    onNavigateToStartUp: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    dao: FavColorsDao,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var numOfColorsInput by remember { mutableStateOf("") }
    var colorInput by remember { mutableStateOf("") }

    val flowResponse by viewModel.flowResponse.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val shouldShowAdditionalInfo = remember { mutableStateOf(false) }
    val isShowingButtons = remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    EnableInfoDialog(isShowing = shouldShowAdditionalInfo, onNavigateToStartUp)

    val searchOnClick = {
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
                    colorInput = "Random"
                }
                viewModel.getColors(colorInput.lowercase().trim(), num)
            } else {
                makeToast(context, "Please Specify a Color")
            }
        } else {
            makeToast(context, "Please Specify a Count")
        }
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MyTextField(
                        value = colorInput,
                        focusRequester = focusRequester,
                        onValueChange = { if (it.length <= 15) colorInput = it },
                        labelText = "Color",
                        kbOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    MyTextField(
                        value = numOfColorsInput,
                        focusRequester = focusRequester,
                        onValueChange = { if (it.length <= 2) numOfColorsInput = it },
                        labelText = "#",
                        kbOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    AnimatedVisibility(visible = !isShowingButtons.value) {
                        MyButton(onClick = { searchOnClick.invoke() }) {
                            Text(text = "Search", color = Color.White)
                        }
                    }
                }

                AnimatedVisibility(isShowingButtons.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {

                        MyButton(onClick = { searchOnClick.invoke() }) {
                            Text(text = "Search", color = Color.White)
                        }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                val random = Random.nextInt(2, 51)
                                colorInput = "Random"
                                numOfColorsInput = random.toString()
                                viewModel.getColors("random", random)
                            },
                        ) {
                            Text(text = "Random", color = Color.White)
                        }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                shouldShowAdditionalInfo.value = true
                            }
                        ) {
                            Text(text = "Info", color = Color.White)
                        }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                onNavigateToFavorites()
                            }) {
                            Text(text = "Favs", color = Color.White)
                        }
                    }
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceEvenly
//                        ) {
//
//                        }
//                    }
                }

                if (loading) {
                    Spacer(modifier = Modifier.height(50.dp))
                    CircularProgressIndicator()
                }

                flowResponse?.let { ColorsList(context, dao, it, lazyListState) }

            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (lazyListState.isScrollingUp()) {
                FloatingActionButton(
                    onClick = { isShowingButtons.value = !isShowingButtons.value },
                    backgroundColor = BlueishIDK,
                ) {
                    if (isShowingButtons.value) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            "Toggle Buttons",
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            "Toggle Buttons",
                            tint = Color.White
                        )
                    }
                }
            }
        }

    )
}

@Composable
fun MyTextField(
    value: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    labelText: String,
    kbOptions: KeyboardOptions
) {
    val gradient = Brush.horizontalGradient(
        listOf(BlueishIDK, Teal200),
        startX = 0.0f,
        endX = 1000.0f,
        tileMode = TileMode.Repeated
    )
    TextField(
        value = value,
        modifier = Modifier
            .fillMaxWidth(.5f)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .focusRequester(focusRequester)
            .background(
                brush = gradient,
                shape = RoundedCornerShape(percent = 10)
            ),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        onValueChange = onValueChange,
        label = { Text(labelText, color = Color.White) },
        keyboardOptions = kbOptions,
        singleLine = true,
    )
}

@Composable
fun MyButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK),
    ) {
        content.invoke()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorsList(
    context: Context,
    dao: FavColorsDao,
    colors: MyColorResponse,
    state: LazyListState
) {
    val clipboardManager = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
        state = state
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .indication(interactionSource, LocalIndication.current)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                clipboardManager.setText(AnnotatedString(color.hex))
                                makeToast(context, "Copied ${color.hex}")
                            },
                            onLongPress = {
                                dao.insertFavColor(color)
                                makeToast(context, "Saved ${color.hex}")
                            },
                        )
                    }
                    .padding(8.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color(parseColor(color.hex))),
                contentAlignment = Alignment.Center
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
    dismissButton: @Composable () -> Unit,
    isShowing: MutableState<Boolean>
) {
    if (isShowing.value) {
        AlertDialog(
            onDismissRequest = { isShowing.value = false },
            title = { Text(title, fontSize = 18.sp, color = BlueishIDK) },
            text = content,
            confirmButton = confirmButton,
            dismissButton = dismissButton
        )
    }
}

@Composable
fun EnableInfoDialog(isShowing: MutableState<Boolean>, onNavigateToStartUp: () -> Unit) {
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
                onClick = { isShowing.value = false },
                colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK)
            ) {
                Text(text = "OK", color = Color.White)
            }

        },
        dismissButton = {
            MyButton(
                onClick = {
                    isShowing.value = false
                    onNavigateToStartUp()
                },
            ) {
                Text(text = "Intro", color = Color.White)
            }
        }, isShowing = isShowing
    )
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}