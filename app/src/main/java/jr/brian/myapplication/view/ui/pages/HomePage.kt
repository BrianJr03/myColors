package jr.brian.myapplication.view.ui.pages

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import jr.brian.myapplication.data.model.local.ScaleAndAlphaArgs
import jr.brian.myapplication.data.model.local.scaleAndAlpha
import jr.brian.myapplication.data.model.remote.MyColorResponse
import jr.brian.myapplication.util.*
import jr.brian.myapplication.util.theme.BlueishIDK
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

    val focusManager = LocalFocusManager.current

    val isShowingInfo = remember { mutableStateOf(false) }
    val isShowingButtons = remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    InfoDialog(isShowing = isShowingInfo, onNavigateToStartUp)

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
                        onValueChange = { if (it.length <= 15) colorInput = it },
                        labelText = "Color | Hue",
                        kbOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    MyTextField(
                        value = numOfColorsInput,
                        onValueChange = { if (it.length <= 2) numOfColorsInput = it },
                        labelText = "#",
                        kbOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    AnimatedVisibility(visible = !isShowingButtons.value) {
                        SearchButton { searchOnClick.invoke() }
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

                        SearchButton { searchOnClick.invoke() }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                val random = Random.nextInt(2, 51)
                                colorInput = "Random"
                                numOfColorsInput = random.toString()
                                viewModel.getColors("random", random)
                            }) { Text(text = "Random", color = Color.White) }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                isShowingInfo.value = true
                            }) { Text(text = "Info", color = Color.White) }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                onNavigateToFavorites()
                            }) { Text(text = "Favs", color = Color.White) }
                    }
                }

                if (loading) {
                    Spacer(modifier = Modifier.height(50.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }

                flowResponse?.let { ColorsList(context, dao, colors = it, lazyListState) }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (lazyListState.isScrollingUp()) {
                FAB(isShowingButtons = isShowingButtons) {
                    isShowingButtons.value = !isShowingButtons.value
                }
            }
        }

    )
}

@Composable
private fun SearchButton(searchOnClick: () -> Unit) {
    MyButton(onClick = { searchOnClick.invoke() }) {
        Text(text = "Search", color = Color.White)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColorsList(
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
        items(colors.count()) { index ->
            val (delay, easing) = state.calculateDelayAndEasing(index, 3)
            val animation = tween<Float>(durationMillis = 500, delayMillis = delay, easing = easing)
            val args = ScaleAndAlphaArgs(fromScale = 2f, toScale = 1f, fromAlpha = 0f, toAlpha = 1f)
            val (scale, alpha) = scaleAndAlpha(args = args, animation = animation)
            val color = colors[index]
            Box(
                modifier = Modifier
                    .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
                    .indication(interactionSource, LocalIndication.current)
                    .animateItemPlacement()
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
private fun InfoDialog(isShowing: MutableState<Boolean>, onNavigateToStartUp: () -> Unit) {
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
            MyButton(onClick = { isShowing.value = false }) {
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