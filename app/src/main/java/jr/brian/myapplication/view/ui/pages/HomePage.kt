package jr.brian.myapplication.view.ui.pages

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import jr.brian.myapplication.data.model.remote.firebase.Auth
import jr.brian.myapplication.util.*
import jr.brian.myapplication.util.theme.BlueishIDK
import jr.brian.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

val additionalInfo =
    listOf(
        "RED",
        "PINK",
        "PURPLE",
        "NAVY",
        "BLUE",
        "AQUA",
        "GREEN",
        "LIME",
        "YELLOW",
        "ORANGE",
        "RANDOM",
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
    val isInternetConnected by viewModel.isConnected.collectAsState()
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val isShowingInfo = remember { mutableStateOf(false) }
    val isShowingButtons = remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    val dataStore = MyDataStore(context)

    InfoDialog(dataStore = dataStore, isShowing = isShowingInfo, onNavigateToStartUp)

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
                    !additionalInfo.contains(colorInput.uppercase())
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
                        SearchButton(
                            context,
                            isInternetConnected
                        ) { searchOnClick.invoke() }
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

                        SearchButton(
                            context,
                            isInternetConnected
                        ) { searchOnClick.invoke() }

                        MyButton(
                            onClick = {
                                focusManager.clearFocus()
                                if (isInternetConnected) {
                                    val random = Random.nextInt(2, 51)
                                    colorInput = "Random"
                                    numOfColorsInput = random.toString()
                                    viewModel.getColors("random", random)
                                } else {
                                    makeToast(
                                        context,
                                        "Please check your Internet connection and try again."
                                    )
                                }
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
                FAB(onClick = { isShowingButtons.value = !isShowingButtons.value }) {
                    if (isShowingButtons.value) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Hide Menu",
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Reveal Menu",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    )

    val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            scope.launch {
                viewModel.isConnected.emit(true)
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            scope.launch {
                viewModel.isConnected.emit(false)
            }
        }
    }

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivityManager.requestNetwork(networkRequest, networkCallback)
}

@Composable
private fun SearchButton(
    context: Context,
    isInternetConnected: Boolean,
    searchOnClick: () -> Unit
) {
    MyButton(onClick = {
        if (isInternetConnected) {
            searchOnClick.invoke()
        } else makeToast(
            context,
            "Please check your Internet connection and try again."
        )
    }) {
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
                                makeToast(context, "Copied ${color.hex} to Clipboard")
                            },
                            onLongPress = {
                                dao.insertFavColor(color)
                                makeToast(context, "Saved ${color.hex} to Favorites")
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
private fun InfoDialog(
    dataStore: MyDataStore,
    isShowing: MutableState<Boolean>,
    onNavigateToStartUp: () -> Unit
) {
    val scope = rememberCoroutineScope()
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
                    Auth.signOut()
                    scope.launch {
                        dataStore.saveStartUpPassStatus(false)
                    }
                    onNavigateToStartUp()
                },
            ) {
                Text(text = "Sign Out", color = Color.White)
            }
        }, isShowing = isShowing
    )
}