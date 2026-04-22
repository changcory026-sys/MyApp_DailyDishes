package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme
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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
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
                                contentDescription = "分享菜谱"
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(138.dp),
                tint = Color.LightGray,
            )
            Text(
                text = stringResource(R.string.tap_to_add_dishes),
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DishListNotEmptyScreen(
    allDishes: List<DishEntity>,
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 按类型分组
    val groupedDishes = allDishes.groupBy { it.type }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 计算每个类型的起始索引，用于快速跳转
    val typeToIndex = remember(allDishes) {
        val map = mutableMapOf<String, Int>()
        var currentIndex = 0
        DishEntity.typeOptions.forEach { type ->
            val dishesInType = groupedDishes[type] ?: emptyList()
            if (dishesInType.isNotEmpty()) {
                map[type] = currentIndex
                currentIndex += 1 + dishesInType.size // 1个header + n个item
            }
        }
        map
    }

    Column(modifier = modifier) {
        // 顶部的分类快速导航标签
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DishEntity.typeOptions.forEach { type ->
                // 仅显示有菜品的分类按钮
                if (groupedDishes[type]?.isNotEmpty() == true) {
                    item {
                        AssistChip(
                            onClick = {
                                coroutineScope.launch {
                                    typeToIndex[type]?.let { index ->
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            },
                            label = { Text(type) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 按照 DishEntity 定义的 typeOptions 顺序进行遍历
            DishEntity.typeOptions.forEach { type ->
                val dishesInType = groupedDishes[type] ?: emptyList()
                
                // 只有当该类型下有菜品时才显示标签
                if (dishesInType.isNotEmpty()) {
                    item(key = "header_$type") {
                        Text(
                            text = "---- $type ----",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    
                    items(
                        items = dishesInType,
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

fun shareDishMenu(context: Context, dishes: List<DishEntity>) {
    val shareText = buildString {
        append("长禾私房菜单：\n\n")
        if (dishes.isEmpty()) {
            append("暂无菜式，快去添加吧！")
        } else {
            val grouped = dishes.groupBy { it.type }
            DishEntity.typeOptions.forEach { type ->
                val list = grouped[type] ?: emptyList()
                if (list.isNotEmpty()) {
                    append("【$type】\n")
                    list.forEach { append("- ${it.name}\n") }
                    append("\n")
                }
            }
        }
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "分享菜单到...")
    context.startActivity(shareIntent)
}

@Preview(showBackground = true)
@Composable
fun DishListScreenPreview() {
    DishInventoryTheme {
        val fakeDishes = listOf(
            DishEntity(1, "炒青菜", "10", "素菜", "平和", "全周期"),
            DishEntity(2, "红烧肉", "60", "大荤", "温补", "黄体期")
        )

        DishListNotEmptyScreen(
            modifier = Modifier.fillMaxSize(),
            allDishes = fakeDishes,
            onNaviToDishDetailsScreen = { },
        )
    }
}
