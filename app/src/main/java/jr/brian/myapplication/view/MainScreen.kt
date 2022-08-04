package jr.brian.myapplication.view

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jr.brian.myapplication.model.remote.MyColorResponse
import jr.brian.myapplication.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    val flowResponse by viewModel.flowResponse.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .padding(5.dp)
            ) {
                Button(onClick = {
                        searchText.toIntOrNull()?.let { viewModel.getColors(it) }
                }) {
                    Text(text = "Search")
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
        cells = GridCells.Fixed(3)
    ) {
        items(colors) { color ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(parseColor(color.hex)))
            ) {}
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