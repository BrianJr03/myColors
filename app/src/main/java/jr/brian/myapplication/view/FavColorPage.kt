package jr.brian.myapplication.view

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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
import jr.brian.myapplication.util.makeToast
import jr.brian.myapplication.util.parseColor
import jr.brian.myapplication.util.theme.BlueishIDK

@Composable
fun FavColorPage(dao: FavColorsDao) {
    val colors = remember { dao.getFavColors().toMutableStateList() }
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth(.50f), onClick = {
                        colors.clear()
                        dao.removeAllFavColors()
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueishIDK
                    )
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
fun FavColorsList(dao: FavColorsDao, list: SnapshotStateList<MyColor>) {
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
            ) {
                Text(color.hex, color = Color.Black)
            }
        }
    }
}