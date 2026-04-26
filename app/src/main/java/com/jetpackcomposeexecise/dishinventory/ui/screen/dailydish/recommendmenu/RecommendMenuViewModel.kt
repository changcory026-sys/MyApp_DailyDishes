package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.recommendmenu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.data.local.repository.MealDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class RecommendMenuViewModel @Inject constructor(
    private val dishRepository: DishRepository,
    private val mealDateRepository: MealDateRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mealDate: String = checkNotNull(savedStateHandle["mealDate"])
    val mealTime: String = checkNotNull(savedStateHandle["mealTime"])
    private val configJson: String = checkNotNull(savedStateHandle["configJson"])

    private val _recommendedDishes = MutableStateFlow<List<DishEntity>>(emptyList())
    val recommendedDishes = _recommendedDishes.asStateFlow()

    private val allDishesFlow = dishRepository.allDishes

    init {
        generateInitialMenu()
    }

    // 抽离生成逻辑，支持初始生成和手动重刷 👈
    fun generateInitialMenu() {
        viewModelScope.launch {
            val config = Json.decodeFromString<List<Pair<String, Int>>>(configJson)
            val allDishes = allDishesFlow.first()
            
            val result = mutableListOf<DishEntity>()
            config.forEach { (type, count) ->
                val typeDishes = allDishes.filter { it.type == type }.shuffled()
                result.addAll(typeDishes.take(count))
            }
            _recommendedDishes.value = result
        }
    }

    fun replaceDish(index: Int, newDish: DishEntity) {
        _recommendedDishes.update { current ->
            val newList = current.toMutableList()
            if (index in newList.indices) {
                newList[index] = newDish
            }
            newList
        }
    }

    fun removeDish(index: Int) {
        _recommendedDishes.update { current ->
            current.toMutableList().apply {
                if (index in this.indices) removeAt(index)
            }
        }
    }

    // 👈 优化：使用批量插入方法
    fun saveMenu(onComplete: () -> Unit) {
        viewModelScope.launch {
            val crossRefs = _recommendedDishes.value.map { dish ->
                MealDateDishCrossRef(mealDate, dish.dishId, mealTime)
            }
            // 确保 Repository 实现了批量插入方法
            mealDateRepository.addDishesToDate(mealDate, crossRefs)
            onComplete()
        }
    }

    fun getAvailableDishesByType(type: String): Flow<List<DishEntity>> {
        return allDishesFlow.map { all ->
            val selectedIds = _recommendedDishes.value.map { it.dishId }.toSet()
            all.filter { it.type == type && !selectedIds.contains(it.dishId) }
        }
    }
}
