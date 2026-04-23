package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientlist

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBasket
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientListScreen(
    modifier: Modifier = Modifier,
    viewModel: IngredientListViewModel = hiltViewModel(),
    onNaviToIngredientDetailsScreen: (ingredientId: Long) -> Unit,
    onNaviToAddIngredientScreen: () -> Unit
) {
    val context = LocalContext.current
    val allIngredients by viewModel.allIngredients.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.bottom_ingredient)) },
                    actions = {
                        IconButton(onClick = {
                            shareIngredientMenu(context, allIngredients)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "分享食材"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNaviToAddIngredientScreen) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Item"
                    )
                }
            }
        ) { innerpadding ->
            if (allIngredients.isEmpty()) {
                IngredientListEmptyScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerpadding.calculateTopPadding())
                )
            } else {
                IngredientListNotEmptyScreen(
                    allIngredients = allIngredients,
                    onNaviToIngredientDetailsScreen = onNaviToIngredientDetailsScreen,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerpadding.calculateTopPadding())
                )
            }
        }
    }
}

@Composable
fun IngredientListEmptyScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.ShoppingBasket,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(138.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.ingredient_default_text),
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun IngredientListNotEmptyScreen(
    allIngredients: List<IngredientEntity>,
    onNaviToIngredientDetailsScreen: (ingredientId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 按类型分组
    val groupedIngredients = allIngredients.groupBy { it.type }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 计算每个类型的起始索引，用于快速跳转
    val typeToIndex = remember(allIngredients) {
        val map = mutableMapOf<String, Int>()
        var currentIndex = 0
        IngredientEntity.typeOptions.forEach { type ->
            val ingredientsInType = groupedIngredients[type] ?: emptyList()
            if (ingredientsInType.isNotEmpty()) {
                map[type] = currentIndex
                currentIndex += 1 + ingredientsInType.size // 1个header + n个item
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
            IngredientEntity.typeOptions.forEach { type ->
                // 仅显示有菜品的分类按钮
                if (groupedIngredients[type]?.isNotEmpty() == true) {
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
            // 按照 IngredientEntity 定义的 typeOptions 顺序进行遍历
            IngredientEntity.typeOptions.forEach { type ->
                val ingredientsInType = groupedIngredients[type] ?: emptyList()

                // 只有当该类型下有菜品时才显示标签
                if (ingredientsInType.isNotEmpty()) {
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
                        items = ingredientsInType,
                        key = { it.ingredientId }
                    ) { item ->
                        DishCard(
                            modifier = Modifier.fillMaxWidth(),
                            onNaviToIngredientDetailsScreen = onNaviToIngredientDetailsScreen,
                            ingredient = item
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
    onNaviToIngredientDetailsScreen: (ingredientId: Long) -> Unit,
    ingredient: IngredientEntity
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = { onNaviToIngredientDetailsScreen(ingredient.ingredientId) }
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
                    text = ingredient.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ingredient.medicine,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Text(
                text = stringResource(R.string.price, ingredient.price),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

fun shareIngredientMenu(context: Context, ingredients: List<IngredientEntity>) {
    val shareText = buildString {
        append("食材清单：\n\n")
        if (ingredients.isEmpty()) {
            append("还没有食材，快去添加吧！")
        } else {
            val grouped = ingredients.groupBy { it.type }
            IngredientEntity.typeOptions.forEach { type ->
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

    val shareIntent = Intent.createChooser(sendIntent, "分享食材清单到...")
    context.startActivity(shareIntent)
}

//UI测试
