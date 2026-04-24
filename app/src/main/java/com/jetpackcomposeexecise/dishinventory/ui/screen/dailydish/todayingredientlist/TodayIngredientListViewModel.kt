package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.todayingredientlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredientsAndMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.MealDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TodayIngredientListViewModel @Inject constructor(
    private val repository: MealDateRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mealDate: String = checkNotNull(savedStateHandle["mealDate"])

    // 1. 获取原始数据流
    val dishesWithIngredients: StateFlow<List<DishWithIngredientsAndMealTime>> = 
        repository.getDishesWithIngredientsAndMealTimeByDate(mealDate)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. 全天食材去重汇总
    val aggregatedIngredients: StateFlow<List<IngredientEntity>> = dishesWithIngredients
        .map { list ->
            list.flatMap { it.ingredients }
                .distinctBy { it.ingredientId }
                .sortedBy { it.type }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 3. 从 Repository 监听勾选状态流
    val checkedIds: StateFlow<Set<Long>> = repository.checkedIngredientsMap
        .map { it[mealDate] ?: emptySet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // 4. 👈 新增：统计摘要数据 (用于顶部 Chips 显示)
    val statsSummary: StateFlow<List<Pair<String, Int>>> = aggregatedIngredients
        .map { ingredients ->
            val summary = mutableListOf<Pair<String, Int>>()
            if (ingredients.isNotEmpty()) {
                summary.add("共" to ingredients.size)
                val counts = ingredients.groupingBy { it.type }.eachCount()
                // 按照预设的分类顺序排列
                IngredientEntity.typeOptions.forEach { type ->
                    counts[type]?.let { count ->
                        summary.add(type to count)
                    }
                }
            }
            summary
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. 切换勾选状态的方法
    fun toggleChecked(ingredientId: Long) {
        repository.toggleIngredientChecked(mealDate, ingredientId)
    }
}
