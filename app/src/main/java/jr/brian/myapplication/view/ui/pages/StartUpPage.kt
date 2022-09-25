package jr.brian.myapplication.view.ui.pages

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import jr.brian.myapplication.data.model.local.ScaleAndAlphaArgs
import jr.brian.myapplication.data.model.local.pagerData
import jr.brian.myapplication.data.model.local.scaleAndAlpha
import jr.brian.myapplication.data.model.remote.MyColor
import jr.brian.myapplication.util.MyDataStore
import jr.brian.myapplication.util.calculateDelayAndEasing
import jr.brian.myapplication.util.parseColor
import jr.brian.myapplication.util.theme.BlueishIDK
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StartUpPage(onNavigateToHome: () -> Unit, signIn: () -> Unit) {
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

                when (currentPage) {
//                    2 -> SignUpPage(context = , navController = )
                    3 -> SignInPage(context, signIn = signIn)
                    else -> SampleColorsList()
                }
            }
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
private fun SampleColorsList() {
    val lazyListState = rememberLazyListState()
    val colors = listOf(
        MyColor(hex = "#78B5D3"),
        MyColor(hex = "#F36D91"),
        MyColor(hex = "#E48762"),
        MyColor(hex = "#CEF8B0"),
        MyColor(hex = "#77ECFE"),
        MyColor(hex = "#C2F0E0"),
        MyColor(hex = "#F5BCF1"),
        MyColor(hex = "#CCE1F0"),
        MyColor(hex = "#FB9556"),
        MyColor(hex = "#ABE175"),
        MyColor(hex = "#96DFED"),
        MyColor(hex = "#FEE5D7")
    )
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(100.dp),
        state = lazyListState
    ) {
        items(colors.count()) { index ->
            val (delay, easing) = lazyListState.calculateDelayAndEasing(index, 3)
            val animation = tween<Float>(durationMillis = 500, delayMillis = delay, easing = easing)
            val args = ScaleAndAlphaArgs(fromScale = 2f, toScale = 1f, fromAlpha = 0f, toAlpha = 1f)
            val (scale, alpha) = scaleAndAlpha(args = args, animation = animation)
            val color = colors[index]
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color(parseColor(color.hex)))
                    .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale),
                contentAlignment = Alignment.Center
            ) { Text(color.hex, color = Color.Black) }
        }
    }
}