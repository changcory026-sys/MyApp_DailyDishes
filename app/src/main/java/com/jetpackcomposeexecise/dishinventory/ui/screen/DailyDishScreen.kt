package com.jetpackcomposeexecise.dishinventory.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.room.DishItem
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDishScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyDishViewModel = hiltViewModel(),
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    onNaviToAddDishScreen: () -> Unit, //FAB按钮的回调
) {
    Scaffold( //嵌套Scaffold，实现FloatingActionButton
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.title_dailydish)) }
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
        val allDishes by viewModel.allDishes.collectAsStateWithLifecycle()

        if (allDishes.isEmpty()) {
            DailyDishEmptyScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerpadding.calculateTopPadding())
            )
        } else {
            DailyDishListScreen(
                allDishes = allDishes,
                onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerpadding.calculateTopPadding())
            )
        }
    }
}

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

@Composable
fun DailyDishListScreen(
    allDishes: List<DishItem>,
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = allDishes,
            key = { it.id }
        ) { item ->
            DishCard(
                onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                dish = item
            )
        }
    }
}

//UI测试
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DishInventoryTheme {
        // 模拟数据
        val fakeDish1 = DishItem(1, "Apple", 20.0, "中性", "中饭", "黄体期")
        val fakeDish2 = DishItem(2, "meat", 50.0, "温补", "中饭", "黄体期")
        val fakeDishes = listOf(fakeDish1, fakeDish2)

        DailyDishListScreen(
            modifier = Modifier.fillMaxSize(),
            allDishes = fakeDishes,
            onNaviToDishDetailsScreen = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyDishEmptyScreenPreview() {
    DailyDishEmptyScreen(
        modifier = Modifier.fillMaxSize(),
    )
}
