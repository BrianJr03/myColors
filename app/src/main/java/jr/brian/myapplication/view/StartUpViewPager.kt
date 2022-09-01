package jr.brian.myapplication.view

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import jr.brian.myapplication.model.local.AppDatabase
import jr.brian.myapplication.model.local.StartUpIntro
import jr.brian.myapplication.model.local.pagerData
import jr.brian.myapplication.model.remote.MyColor
import jr.brian.myapplication.model.util.theme.BlueishIDK

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StartUpViewPager(navController: NavController, appDB: AppDatabase) {
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
                appDB.dao().passStartUp(StartUpIntro(true))
                navController.navigate("home_page") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
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
    val colors = mutableListOf<MyColor>().apply {
        add(MyColor(hex = "#78B5D3", hsl = "", rgb = ""))
        add(MyColor(hex = "#F36D91", hsl = "", rgb = ""))
        add(MyColor(hex = "#E48762", hsl = "", rgb = ""))
        add(MyColor(hex = "#CEF8B0", hsl = "", rgb = ""))
        add(MyColor(hex = "#77ECFE", hsl = "", rgb = ""))
        add(MyColor(hex = "#C2F0E0", hsl = "", rgb = ""))
        add(MyColor(hex = "#F5BCF1", hsl = "", rgb = ""))
        add(MyColor(hex = "#CCE1F0", hsl = "", rgb = ""))
        add(MyColor(hex = "#FB9556", hsl = "", rgb = ""))
        add(MyColor(hex = "#ABE175", hsl = "", rgb = ""))
        add(MyColor(hex = "#96DFED", hsl = "", rgb = ""))
        add(MyColor(hex = "#FEE5D7", hsl = "", rgb = ""))
    }
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
            ) {
                Text(color.hex, color = Color.Black)
            }
        }
    }
}