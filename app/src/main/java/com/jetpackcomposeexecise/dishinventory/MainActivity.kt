package com.jetpackcomposeexecise.dishinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jetpackcomposeexecise.dishinventory.ui.navigation.MyDailyDishApp
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        // 1. 告诉系统：我要自己接管窗口的系统级边距 (Edge-to-Edge)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        // 2. 获取 InsetsController
//        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
//
//        // 3. 隐藏顶部的状态栏
//        insetsController.hide(WindowInsetsCompat.Type.statusBars())
//
//        // 【可选】如果你想把底部的“三大金刚键”或“小白条”导航栏也隐藏掉，可以改成：
//        // insetsController.hide(WindowInsetsCompat.Type.systemBars())
//
//        // 4. 设置沉浸式行为：用户从屏幕边缘向内滑动时，状态栏会半透明地浮现出来，过一会儿自动消失
//        insetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        enableEdgeToEdge()
        setContent {
            DishInventoryTheme {
                MyDailyDishApp()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DishInventoryTheme {
        MyDailyDishApp()
    }
}