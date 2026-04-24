package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.todayingredientlist

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredientsAndMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.ui.utils.getDishTypeEmoji
import com.jetpackcomposeexecise.dishinventory.ui.utils.getIngredientTypeEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayIngredientListScreen(
    modifier: Modifier = Modifier,
    viewModel: TodayIngredientListViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val mealDate = viewModel.mealDate
    val dishesWithIngredients by viewModel.dishesWithIngredients.collectAsStateWithLifecycle()
    val aggregatedIngredients by viewModel.aggregatedIngredients.collectAsStateWithLifecycle()
    val statsSummary by viewModel.statsSummary.collectAsStateWithLifecycle()
    val checkedIds by viewModel.checkedIds.collectAsStateWithLifecycle()
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("今日食材清单") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = navigateUp,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("确定")
                }
                Button(
                    onClick = { shareDetailedIngredients(context, mealDate, dishesWithIngredients, aggregatedIngredients) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("分享清单")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 头部标题
            item {
                Text(
                    text = "📅 $mealDate 汇总",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // 👈 方案 5：顶部分类汇总 Chips (统计摘要)
            if (statsSummary.isNotEmpty()) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(statsSummary) { pair ->
                            val label = if (pair.first == "共") "共 ${pair.second} 样" else "${getIngredientTypeEmoji(pair.first)} ${pair.first} ${pair.second}"
                            SuggestionChip(
                                onClick = { },
                                label = { Text(label) },
                                colors = if (pair.first == "共") {
                                    SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    SuggestionChipDefaults.suggestionChipColors()
                                }
                            )
                        }
                    }
                }
            }

            if (aggregatedIngredients.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "今日暂无菜式，请先添加菜单。",
                            modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // 1. 全天买菜清单
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(vertical = 16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(text = "🛒", fontSize = 24.sp)
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "全天买菜清单 (实时核对)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val grouped = aggregatedIngredients.groupBy { it.type }
                            IngredientEntity.typeOptions.forEach { type ->
                                val ingredientsOfType = grouped[type] ?: emptyList()
                                if (ingredientsOfType.isNotEmpty()) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                    )
                                    
                                    ingredientsOfType.forEach { ingredient ->
                                        val isChecked = checkedIds.contains(ingredient.ingredientId)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.toggleChecked(ingredient.ingredientId) }
                                                .padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { viewModel.toggleChecked(ingredient.ingredientId) }
                                            )
                                            Text(
                                                text = "${getIngredientTypeEmoji(ingredient.type)} ${ingredient.name}",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                                                ),
                                                color = if (isChecked) Color.Gray else Color.Unspecified,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
                
                // 2. 详细分餐明细
                item {
                    Text(
                        text = "📖 详细分餐明细",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                MealDateDishCrossRef.mealTimeOptions.forEach { period ->
                    val dishesInPeriod = dishesWithIngredients.filter { it.mealTime == period }
                    if (dishesInPeriod.isNotEmpty()) {
                        item(key = period) {
                            val timeIcon = when(period) {
                                "早饭" -> "🌅"
                                "中饭" -> "☀️"
                                "下午茶" -> "🍵"
                                "晚饭" -> "🌙"
                                else -> "🌑"
                            }
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "$timeIcon $period",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    dishesInPeriod.forEach { item ->
                                        Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)) {
                                            Text(
                                                text = "${getDishTypeEmoji(item.dish.type)} ${item.dish.name}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "备菜: " + item.ingredients.joinToString("、") { it.name },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * 分享功能逻辑
 */
private fun shareDetailedIngredients(
    context: Context,
    date: String,
    details: List<DishWithIngredientsAndMealTime>,
    summary: List<IngredientEntity>
) {
    val shareContent = StringBuilder("📅 $date 采购/备菜清单\n\n")
    
    shareContent.append("🛒 全天食材汇总：\n")
    if (summary.isEmpty()) {
        shareContent.append("暂无食材\n")
    } else {
        val groupedSummary = summary.groupBy { it.type }
        IngredientEntity.typeOptions.forEach { type ->
            val list = groupedSummary[type] ?: emptyList()
            if (list.isNotEmpty()) {
                shareContent.append("【$type】\n")
                list.forEach { shareContent.append(" - ${getIngredientTypeEmoji(it.type)} ${it.name}\n") }
            }
        }
    }
    
    shareContent.append("\n📖 分餐菜谱明细：\n")
    MealDateDishCrossRef.mealTimeOptions.forEach { period ->
        val dishes = details.filter { it.mealTime == period }
        if (dishes.isNotEmpty()) {
            val timeIcon = when(period) {
                "早饭" -> "🌅"
                "中饭" -> "☀️"
                "下午茶" -> "🍵"
                "晚饭" -> "🌙"
                else -> "🌑"
            }
            shareContent.append("$timeIcon 【$period】\n")
            dishes.forEach { item ->
                shareContent.append(" - ${item.dish.name}：")
                shareContent.append(item.ingredients.joinToString("、") { it.name } + "\n")
            }
        }
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareContent.toString())
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "分享清单到..."))
}
