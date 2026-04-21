package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.DishCard
import com.jetpackcomposeexecise.dishinventory.ui.utils.saveAndShareImage
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDishScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyDishViewModel = hiltViewModel(),
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    onNaviToAddDailyDishScreen: (mealDate: String) -> Unit, //FAB按钮的回调
) {
    val selectedDate by viewModel.selectedDateText.collectAsStateWithLifecycle()
    val dailyDishes by viewModel.dailyDishes.collectAsStateWithLifecycle()
    val dateOptions by viewModel.dateOptions.collectAsStateWithLifecycle()
    val isScreenshotLoading by viewModel.isScreenshotLoading.collectAsStateWithLifecycle()// 获取 Loading 状态
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // 1. 初始化截图控制器
    val captureController = rememberCaptureController()
    // 2. 状态：标记是否处于“截图预备”模式（用于强制全量渲染列表）
    var isPreparingForCapture by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {// 使用最外层 Box 方便叠加 Loading 蒙层
        Scaffold( //嵌套Scaffold1：标题文本、FloatingActionButton
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.title_dailydish)) },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                // 步骤 A：开启 Loading，并切换为截图模式
                                viewModel.setScreenshotLoading(true)
                                isPreparingForCapture = true

                                // 步骤 B：给 Compose 200毫秒时间把隐藏的菜品全部绘制出来
                                delay(200)

                                // 步骤 C：执行截图！
                                captureController.capture()
                            }
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
                FloatingActionButton(onClick = { onNaviToAddDailyDishScreen(selectedDate) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Item"
                    )
                }
            }
        ) { innerpadding ->
            Scaffold(
                //嵌套Scaffold2：顶部日期栏
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerpadding.calculateTopPadding()),
                topBar = {
                    MealDayBar(
                        selectedDate = selectedDate,
                        onYesterdayBtnClick = { viewModel.moveStepBack() },
                        onTomorrowBtnClick = { viewModel.moveStepForward() },
                        dateOptions = dateOptions,
                        onDateSelected = { viewModel.onDateSelected(it) }
                    )
                },
            ) { innerpadding ->
                // ----- 核心截图区 -----
                // 使用 Capturable 包裹我们需要生成图片的区域
                Capturable(
                    controller = captureController,
                    onCaptured = { imageBitmap, error ->
                        if (imageBitmap != null) {
                            // 将 Compose Bitmap 转为原生 Bitmap 并分享
                            saveAndShareImage(context, imageBitmap.asAndroidBitmap())
                        }

                        // 步骤 D：分享结束，恢复界面状态
                        isPreparingForCapture = false
                        viewModel.setScreenshotLoading(false)
                    }
                ) {
                    // 这是最后图片生成的内容主体
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface) // 必须设置纯色底，防止截图变透明或黑底
                            .padding(vertical = 8.dp)
                    ) {
                        if (dailyDishes.isEmpty()) {
                            Log.e("DailyDishScreen", "DailyDishScreen: empty")
                            DailyDishEmptyScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = innerpadding.calculateTopPadding())
                            )
                        } else {
                            DailyDishListScreen(
                                allDishes = dailyDishes,
                                onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = innerpadding.calculateTopPadding())
                            )
                        }
                    }
                }
            }
        }
        // ----- 全屏 Loading 遮罩 -----
        if (isScreenshotLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)), // 半透明黑色背景
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDayBar(
    modifier: Modifier = Modifier,
    onYesterdayBtnClick: () -> Unit,
    onTomorrowBtnClick: () -> Unit,
    selectedDate: String,
    dateOptions: List<String>,
    onDateSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = onYesterdayBtnClick) { Text("前一天") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,   // 展开菜单（聚焦）时的边框颜色
                    unfocusedBorderColor = Color.Transparent, // 平时（未聚焦）的边框颜色
                    focusedContainerColor = Color.Transparent,   // 👈 聚焦时的背景透明
                    unfocusedContainerColor = Color.Transparent,  // 👈 平时的背景透明
                    disabledBorderColor = Color.Transparent   // 禁用状态下的边框颜色（以防万一）
                ),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                dateOptions.forEach { date ->
                    DropdownMenuItem(
                        text = { Text(text = date) },
                        onClick = {
                            onDateSelected(date)
                            expanded = false
                        }
                    )
                }
            }
        }
        OutlinedButton(onClick = onTomorrowBtnClick) { Text("后一天") }

    }
}

//当日菜单界面（无数据时）
@Composable
fun DailyDishEmptyScreen(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Restaurant,
            contentDescription = "null",
            tint = Color.LightGray,
            modifier = Modifier.size(138.dp)
        )
        Text(
            text = stringResource(R.string.tap_to_add_dishes),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 300.dp)
        )
    }
}

//当日菜单界面（有数据时）
@Composable
fun DailyDishListScreen(
    allDishes: List<DishEntity>,
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        // 顶部操作区
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 列表展示区
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = allDishes,
                key = { it.dishId }
            ) { item ->
                DishCard(
                    onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                    dish = item
                )
            }
        }
    }
}

//分享文字：将菜式列表转换为文字并唤起系统分享
fun shareMenu(context: Context, date: String, dishes: List<DishEntity>) {
    // 1. 组装要分享的文字
    val shareText = buildString {
        append("【$date】的菜单：\n")
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
fun DailyDishEmptyScreenPreview() {
    DailyDishEmptyScreen(
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview(showBackground = true)
@Composable
fun MealDayBarPreview() {
    MealDayBar(
        modifier = Modifier.fillMaxWidth(),
        onYesterdayBtnClick = {},
        onTomorrowBtnClick = {},
        selectedDate = "2025-12-16",
        dateOptions = listOf("2024-1-1", "2024-1-1", "2024-1-1", "2024-1-1", "2024-1-1"),
        onDateSelected = {}
    )
}
