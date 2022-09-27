package jr.brian.myapplication.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.myapplication.util.theme.BlueishIDK
import jr.brian.myapplication.util.theme.Teal200
import kotlinx.coroutines.launch

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
            title = { Text(title, fontSize = 18.sp, color = BlueishIDK) },
            text = content,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            onDismissRequest = { isShowing.value = false },
        )
    }
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

@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    kbOptions: KeyboardOptions
) {
    val focusRequester = remember { FocusRequester() }
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
fun FAB(isShowingButtons: MutableState<Boolean>, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
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

@Composable
fun LazyListState.isScrollingUp(): Boolean {
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

@Composable
fun LazyListState.calculateDelayAndEasing(index: Int, columnCount: Int): Pair<Int, Easing> {
    val row = index / columnCount
    val column = index % columnCount
    val firstVisibleRow = firstVisibleItemIndex
    val visibleRows = layoutInfo.visibleItemsInfo.count()
    val scrollingToBottom = firstVisibleRow < row
    val isFirstLoad = visibleRows == 0
    val rowDelay = 200 * when {
        isFirstLoad -> row // initial load
        scrollingToBottom -> visibleRows + firstVisibleRow - row // scrolling to bottom
        else -> 1 // scrolling to top
    }
    val scrollDirectionMultiplier = if (scrollingToBottom || isFirstLoad) 1 else -1
    val columnDelay = column * 150 * scrollDirectionMultiplier
    val easing = if (scrollingToBottom || isFirstLoad)
        LinearOutSlowInEasing else FastOutSlowInEasing
    return rowDelay + columnDelay to easing
}

@Composable
fun SkipButton(context: Context, launchHome: () -> Unit) {
    val dataStore = MyDataStore(context)
    val scope = rememberCoroutineScope()
    return OutlinedButton(
        onClick = {
            scope.launch {
                dataStore.saveStartUpPassStatus(true)
            }
            launchHome()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK)
    ) {
        Text(text = "Skip", color = Color.White)
    }
}