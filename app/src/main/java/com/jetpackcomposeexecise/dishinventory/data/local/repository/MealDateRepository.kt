package com.jetpackcomposeexecise.dishinventory.data.local.repository

import com.jetpackcomposeexecise.dishinventory.data.local.dao.MealDateDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateWithDishes
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MealDateRepository @Inject constructor(
    private val dao: MealDateDao
) {
    // 供界面监听数据流 (原始多对多，主要用于简单查询)
    fun getDishesByDate(date: String): Flow<MealDateWithDishes?> {
        return dao.getMealDateWithDishesByDate(date)
    }

    // 新增：获取带用餐时段的菜品列表
    fun getDishesWithMealTimeByDate(date: String): Flow<List<DishWithMealTime>> {
        return dao.getDishesWithMealTimeByDate(date)
    }

    // 核心业务：向指定日期添加菜品 (增加 mealTime 参数)
    suspend fun addDishToDate(date: String, dishId: Long, mealTime: String) {
        // 1. 确保这一天的记录存在
        dao.insertMealDate(MealDateEntity(date))
        // 2. 建立关联
        dao.insertMealDateDishCrossRef(MealDateDishCrossRef(date, dishId, mealTime))
    }

    // 删除某天的某道菜 (增加 mealTime 参数)
    suspend fun deleteDishFromDate(date: String, dishId: Long, mealTime: String) {
        dao.deleteMealDateDishCrossRef(MealDateDishCrossRef(date, dishId, mealTime))
    }
}