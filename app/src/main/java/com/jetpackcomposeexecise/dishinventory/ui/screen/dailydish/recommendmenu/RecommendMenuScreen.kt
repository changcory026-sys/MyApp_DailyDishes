package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.recommendmenu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.ui.utils.getDishTypeEmoji

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecommendMenuScreen(
    modifier: Modifier = Modifier,
    viewModel: RecommendMenuViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val mealDate = viewModel.mealDate
    val mealTime = viewModel.mealTime
    val recommendedDishes by viewModel.recommendedDishes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("推荐菜单") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.saveMenu(onSaveComplete) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                enabled = recommendedDishes.isNotEmpty()
            ) {
                Text("保存到今日菜单")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. 标题、日期与“换一批”按钮
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📅 $mealDate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "用餐时段：$mealTime",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // 👈 3. 实现“一键换一批”按钮
                    ElevatedButton(
                        onClick = { viewModel.generateInitialMenu() },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("不满意？换一批推荐")
                    }
                }
            }

            // 2. 分组展示生成的菜式 (粘性标题 + 动画)
            DishEntity.typeOptions.forEach { type ->
                val dishesInType = recommendedDishes.filter { it.type == type }
                if (dishesInType.isNotEmpty()) {
                    stickyHeader(key = "header_$type") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "---- ${getDishTypeEmoji(type)} $type ----",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    itemsIndexed(
                        items = dishesInType,
                        key = { index, dish -> "rec_${dish.dishId}_${type}_$index" }
                    ) { _, dish ->
                        val absoluteIndex = recommendedDishes.indexOf(dish)
                        Box(modifier = Modifier.animateItem()) {
                            RecommendDishRow(
                                currentDish = dish,
                                availableOptions = viewModel.getAvailableDishesByType(type).collectAsState(emptyList()).value,
                                onDishReplaced = { newDish -> viewModel.replaceDish(absoluteIndex, newDish) },
                                onRemove = { viewModel.removeDish(absoluteIndex) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendDishRow(
    currentDish: DishEntity,
    availableOptions: List<DishEntity>,
    onDishReplaced: (DishEntity) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = currentDish.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (availableOptions.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("无更多同类型菜品可选") },
                            onClick = { expanded = false },
                            enabled = false
                        )
                    } else {
                        availableOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    onDishReplaced(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除菜品",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
