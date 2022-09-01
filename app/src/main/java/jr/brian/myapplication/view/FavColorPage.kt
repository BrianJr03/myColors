package jr.brian.myapplication.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.myapplication.model.local.AppDatabase
import jr.brian.myapplication.model.remote.MyColor
import jr.brian.myapplication.model.util.theme.BlueishIDK

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavColorPage(
    appDB: AppDatabase,
) {
    val colors = appDB.dao().getFavColors()
    val list = remember { mutableStateListOf<MyColor>() }
    colors.forEach { list.add(it) }
    Column {
        Text(
            text = "Favorite Colors",
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        if (list.isNotEmpty()) {
            Column {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(), onClick = {
                        list.clear()
                        appDB.dao().removeAllFavColors()
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueishIDK
                    )
                ) {
                    Text(text = "Remove All", color = Color.White)
                }
                Spacer(modifier = Modifier.height(15.dp))
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    cells = GridCells.Adaptive(100.dp),
                ) {
                    // TODO - Find duplication bug
                    items(list.distinct()) { color ->
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    onLongClick = {
//                                      TODO - Find duplication bug
//                                    list.remove(color)
//                                        .dao()
//                                        .removeFavColor(color)

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

        } else {
            Text(
                text = "No Favorites Saved",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}