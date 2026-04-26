package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.dailydish

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.dishlist.DishCard
import com.jetpackcomposeexecise.dishinventory.ui.utils.SwipeRevealItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDishScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyDishViewModel = hiltViewModel(),
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    onNaviToAddDailyDishScreen: (mealDate: String) -> Unit,
    onNaviToTodayIngredientListScreen: (mealDate: String) -> Unit,
    onNaviToGenerateMenuScreen: (mealDate: String) -> Unit,
) {
    val selectedDate by viewModel.selectedDateText.collectAsStateWithLifecycle()
    val dailyDishes by viewModel.dailyDishes.collectAsStateWithLifecycle()
    val dateOptions by viewModel.dateOptions.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isFutureOrToday = remember(selectedDate) {
        val today = LocalDate.now().toString()
        selectedDate >= today
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.title_dailydish)) },
                    navigationIcon = {
                        IconButton(onClick = { onNaviToTodayIngredientListScreen(selectedDate) }) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBasket,
                                contentDescription = "今日食材清单"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { shareMenu(context, selectedDate, dailyDishes) }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "分享")
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isFutureOrToday) {
                        SmallFloatingActionButton(
                            onClick = { onNaviToGenerateMenuScreen(selectedDate) },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(imageVector = Icons.Default.Casino, contentDescription = "随机生成今日菜单")
                        }
                    }
                    FloatingActionButton(onClick = { onNaviToAddDailyDishScreen(selectedDate) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Item")
                    }
                }
            }
        ) { innerpadding ->
            Scaffold(
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerpadding.calculateTopPadding())
                ) {
                    if (dailyDishes.isEmpty()) {
                        DailyDishEmptyScreen(modifier = Modifier.fillMaxSize())
                    } else {
                        DailyDishListScreen(
                            allDishes = dailyDishes,
                            onNaviToDishDetailsScreen = onNaviToDishDetailsScreen,
                            removeDishFromCurrentDate = viewModel::deleteDishFromCurrentDate,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyDishListScreen(
    allDishes: Map<String, List<DishEntity>>,
    onNaviToDishDetailsScreen: (dishId: Long) -> Unit,
    removeDishFromCurrentDate: (dishId: Long, mealTime: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MealDateDishCrossRef.mealTimeOptions.forEach { period ->
            val dishesInPeriod = allDishes[period] ?: emptyList()
            if (dishesInPeriod.isNotEmpty()) {
                stickyHeader(key = "header_$period") {
                    val timeIcon = when (period) {
                        "早饭" -> "🌅"
                        "中饭" -> "☀️"
                        "下午茶" -> "🍵"
                        "晚饭" -> "🌙"
                        else -> "🌑"
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "---- $timeIcon $period ----",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                items(
                    items = dishesInPeriod,
                    key = { dish -> "${period}_${dish.dishId}" }
                ) { dish ->
                    SwipeRevealItem(
                        menuWidth = 64.dp,
                        backgroundContent = { isSliding ->
                            val backgroundColor by animateColorAsState(
                                targetValue = if (isSliding) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                                label = "deleteBg"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(64.dp)
                                    .background(backgroundColor),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = { removeDishFromCurrentDate(dish.dishId, period) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        content = {
                            DishCard(
                                dish = dish,
                                onNaviToDishDetailsScreen = onNaviToDishDetailsScreen
                            )
                        }
                    )
                }
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                dateOptions.forEach { date ->
                    DropdownMenuItem(
                        text = { Text(text = date) },
                        onClick = { onDateSelected(date); expanded = false }
                    )
                }
            }
        }
        OutlinedButton(onClick = onTomorrowBtnClick) { Text("后一天") }
    }
}

@Composable
fun DailyDishEmptyScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(138.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.daily_dishes_default_text),
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

private fun shareMenu(context: Context, date: String, dishesMap: Map<String, List<DishEntity>>) {
    val shareContent = StringBuilder("📅 $date 菜单：\n\n")
    MealDateDishCrossRef.mealTimeOptions.forEach { period ->
        val dishes = dishesMap[period] ?: emptyList()
        if (dishes.isNotEmpty()) {
            val timeIcon = when (period) {
                "早饭" -> "🌅"
                "中饭" -> "☀️"
                "下午茶" -> "🍵"
                "晚饭" -> "🌙"
                else -> "🌑"
            }
            shareContent.append("$timeIcon 【$period】\n")
            dishes.forEach { shareContent.append(" - ${it.name}\n") }
            shareContent.append("\n")
        }
    }
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareContent.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
