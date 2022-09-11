package jr.brian.myapplication.view.ui.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.myapplication.data.model.local.FavColorsDao
import jr.brian.myapplication.data.model.remote.MyColor
import jr.brian.myapplication.util.MyButton
import jr.brian.myapplication.util.ShowDialog
import jr.brian.myapplication.util.makeToast
import jr.brian.myapplication.util.parseColor
import jr.brian.myapplication.util.theme.BlueishIDK

@Composable
fun FavColorPage(dao: FavColorsDao) {
    val colors = remember { dao.getFavColors().toMutableStateList() }
    val isShowingDeleteDialog = remember { mutableStateOf(false) }

    DeleteAllDialog(isShowing = isShowingDeleteDialog) {
        colors.clear()
        dao.removeAllFavColors()
    }

    Column {
        Text(
            text = "Favorite Colors",
            fontSize = 30.sp,
            color = BlueishIDK,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        if (colors.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Long-Press to Delete", color = BlueishIDK)
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "OR", color = BlueishIDK)
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(.50f),
                    onClick = {
                        isShowingDeleteDialog.value = true

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK)
                ) {
                    Text(text = "Remove All", color = Color.White)
                }
                Spacer(modifier = Modifier.height(15.dp))
                FavColorsList(dao = dao, list = colors)
            }

        } else {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "No Favorites Saved",
                    color = BlueishIDK,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavColorsList(dao: FavColorsDao, list: SnapshotStateList<MyColor>) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
    ) {
        items(list.reversed()) { color ->
            Box(
                modifier = Modifier
                    .indication(interactionSource, LocalIndication.current)
                    .combinedClickable(
                        onDoubleClick = {
                            clipboardManager.setText(AnnotatedString(color.hex))
                            makeToast(context, "Copied ${color.hex}")
                        },
                        onLongClick = {
                            list.remove(color)
                            dao.removeFavColor(color)

                        }) {}
                    .padding(8.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color(parseColor(color.hex))),
                contentAlignment = Alignment.Center
            ) { Text(color.hex, color = Color.Black) }
        }
    }
}

@Composable
private fun DeleteAllDialog(isShowing: MutableState<Boolean>, onConfirm: () -> Unit) {
    ShowDialog(
        title = "Remove All Favorites",
        content = {
            Text(
                "This will remove all saved Favorites and cannot be undone.",
                fontSize = 16.sp,
                color = BlueishIDK
            )
        },
        confirmButton = {
            MyButton(onClick = {
                onConfirm.invoke()
                isShowing.value = false
            }) {
                Text(text = "OK", color = Color.White)
            }
        },
        dismissButton = {
            MyButton(onClick = { isShowing.value = false }) {
                Text(text = "Cancel", color = Color.White)
            }
        },
        isShowing = isShowing
    )
}