package com.jetpackcomposeexecise.dishinventory.data.local.repository

import com.jetpackcomposeexecise.dishinventory.data.local.dao.MealDateDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredientsAndMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateWithDishes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton // 👈 确保 Repository 是单例，以便在内存中保存勾选状态
class MealDateRepository @Inject constructor(
    private val dao: MealDateDao
) {
    // 内存缓存：记录每个日期下已勾选的食材 ID 👈
    private val _checkedIngredientsMap = MutableStateFlow<Map<String, Set<Long>>>(emptyMap())
    val checkedIngredientsMap = _checkedIngredientsMap.asStateFlow()

    fun getDishesByDate(date: String): Flow<MealDateWithDishes?> {
        return dao.getMealDateWithDishesByDate(date)
    }

    fun getDishesWithMealTimeByDate(date: String): Flow<List<DishWithMealTime>> {
        return dao.getDishesWithMealTimeByDate(date)
    }

    fun getDishesWithIngredientsAndMealTimeByDate(date: String): Flow<List<DishWithIngredientsAndMealTime>> {
        return dao.getDishesWithIngredientsAndMealTimeByDate(date)
    }

    // 切换勾选状态逻辑 👈
    fun toggleIngredientChecked(date: String, ingredientId: Long) {
        _checkedIngredientsMap.update { currentMap ->
            val newMap = currentMap.toMutableMap()
            val currentSet = newMap[date]?.toMutableSet() ?: mutableSetOf()
            
            if (currentSet.contains(ingredientId)) {
                currentSet.remove(ingredientId)
            } else {
                currentSet.add(ingredientId)
            }
            
            newMap[date] = currentSet
            newMap
        }
    }

    suspend fun addDishToDate(date: String, dishId: Long, mealTime: String) {
        dao.insertMealDate(MealDateEntity(date))
        dao.insertMealDateDishCrossRef(MealDateDishCrossRef(date, dishId, mealTime))
    }

    suspend fun deleteDishFromDate(date: String, dishId: Long, mealTime: String) {
        dao.deleteDishFromDate(date, dishId, mealTime)
    }
}
