package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme
import com.jetpackcomposeexecise.dishinventory.ui.utils.saveAndShareImage
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishListScreen(
    modifier: Modifier = Modifier,
    viewModel: DishListViewModel = hiltViewModel(),
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    onNaviToAddDishScreen: () -> Unit
) {
    val context = LocalContext.current
    val allDishes by viewModel.allDishes.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {// 使用最外层 Box 方便share时叠加 Loading 蒙层
        Scaffold( //嵌套Scaffold，实现FloatingActionButton
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.title_dishes)) },
                    actions = {
                        IconButton(onClick = {
                            shareDishMenu(context, allDishes)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "分享今天的菜单"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNaviToAddDishScreen) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Item"
                    )
                }
            }
        ) { innerpadding ->
            if (allDishes.isEmpty()) {
                DishListEmptyScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerpadding.calculateTopPadding())
                )
            } else {
                DishListNotEmptyScreen(
                    allDishes = allDishes,
                    onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerpadding.calculateTopPadding())
                )
            }
        }
    }
}

@Composable
fun DishListEmptyScreen(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MenuBook,
            contentDescription = "null",
            modifier = Modifier.size(138.dp),
            tint = Color.LightGray,
        )
        Text(
            text = stringResource(R.string.tap_to_add_dishes),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 300.dp)
        )
    }
}

@Composable
fun DishListNotEmptyScreen(
    allDishes: List<DishEntity>,
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = allDishes,
            key = { it.dishId }
        ) { item ->
            DishCard(
                modifier = Modifier.fillMaxWidth(),
                onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                dish = item
            )
        }
    }
}

@Composable
fun DishCard(
    modifier: Modifier = Modifier,
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    dish: DishEntity
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = { onNaviToDishDetailsScreen(dish.dishId) }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dish.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dish.medicine,
                    style = MaterialTheme.typography.bodyLarge,

                    )
            }
            Text(
                text = stringResource(R.string.mins, dish.time),
                style = MaterialTheme.typography.bodyMedium,

                )
        }
    }
}

//分享文本：将菜式列表转换为文字并唤起系统分享
fun shareDishMenu(context: Context, dishes: List<DishEntity>) {
    // 1. 组装要分享的文字
    val shareText = buildString {
        append("长禾私房菜单：")
        append("\n")
        if (dishes.isEmpty()) {
            append("暂无菜式，快去添加吧！")
        } else {
            dishes.forEach { dish ->
                append("- ${dish.name}\n")
            }
        }
    }

    // 2. 创建发送 Intent
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain" // 告诉系统我们要分享的是纯文本
    }

    // 3. 包装成选择器 Intent（强制弹出系统的分享面板）
    val shareIntent = Intent.createChooser(sendIntent, "分享菜单到...")

    // 4. 启动分享
    context.startActivity(shareIntent)
}

//UI测试
@Preview(showBackground = true)
@Composable
fun DishListScreenPreview() {
    DishInventoryTheme {
        // 模拟数据
        val fakeDish1 = DishEntity(1, "Apple", 20.0, "中性", "中饭", "黄体期")
        val fakeDish2 = DishEntity(2, "meat", 50.0, "温补", "中饭", "黄体期")
        val fakeDishes = listOf(fakeDish1, fakeDish2)

        DishListNotEmptyScreen(
            modifier = Modifier.fillMaxSize(),
            allDishes = fakeDishes,
            onNaviToDishDetailsScreen = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DishCardPreview() {
    DishInventoryTheme {
        DishCard(
            modifier = Modifier.fillMaxWidth(),
            onNaviToDishDetailsScreen = {},
            dish = DishEntity(2, "韭菜炒鸡蛋", 50.0, "温补", "中饭", "黄体期")
        )
    }
}