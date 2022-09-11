package jr.brian.myapplication.view.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import jr.brian.myapplication.data.model.local.pagerData
import jr.brian.myapplication.data.model.remote.MyColor
import jr.brian.myapplication.util.MyDataStore
import jr.brian.myapplication.util.parseColor
import jr.brian.myapplication.util.theme.BlueishIDK
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StartUpViewPager(onNavigateToHome: () -> Unit) {
    val context = LocalContext.current
    val dataStore = MyDataStore(context)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        val items = pagerData()
        val pagerState = rememberPagerState()

        HorizontalPager(
            count = items.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { currentPage ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = items[currentPage].title,
                    style = MaterialTheme.typography.h4,
                    color = BlueishIDK
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = items[currentPage].subtitle,
                    style = MaterialTheme.typography.h4,
                    color = BlueishIDK
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = items[currentPage].description,
                    style = MaterialTheme.typography.body1,
                    color = BlueishIDK
                )
                Spacer(modifier = Modifier.height(10.dp))
                SampleColorsList()
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                scope.launch {
                    dataStore.saveStartUpPassStatus(true)
                }
                onNavigateToHome()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(backgroundColor = BlueishIDK)
        ) {
            Text(text = "Let's Go!", color = Color.White)
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SampleColorsList() {
    val colors = listOf(
        MyColor(hex = "#78B5D3", hsl = "", rgb = ""),
        MyColor(hex = "#F36D91", hsl = "", rgb = ""),
        MyColor(hex = "#E48762", hsl = "", rgb = ""),
        MyColor(hex = "#CEF8B0", hsl = "", rgb = ""),
        MyColor(hex = "#77ECFE", hsl = "", rgb = ""),
        MyColor(hex = "#C2F0E0", hsl = "", rgb = ""),
        MyColor(hex = "#F5BCF1", hsl = "", rgb = ""),
        MyColor(hex = "#CCE1F0", hsl = "", rgb = ""),
        MyColor(hex = "#FB9556", hsl = "", rgb = ""),
        MyColor(hex = "#ABE175", hsl = "", rgb = ""),
        MyColor(hex = "#96DFED", hsl = "", rgb = ""),
        MyColor(hex = "#FEE5D7", hsl = "", rgb = "")
    )
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color(parseColor(color.hex))),
                contentAlignment = Alignment.Center
            ) { Text(color.hex, color = Color.Black) }
        }
    }
}