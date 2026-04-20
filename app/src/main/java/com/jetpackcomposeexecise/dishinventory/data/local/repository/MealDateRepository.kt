package com.jetpackcomposeexecise.dishinventory.data.local.repository

import com.jetpackcomposeexecise.dishinventory.data.local.dao.MealDateDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateWithDishes
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MealDateRepository @Inject constructor(
    private val dao: MealDateDao
) {
    // 供界面监听数据流
    fun getDishesByDate(date: String): Flow<MealDateWithDishes?> {
        return dao.getMealDateWithDishesByDate(date)
    }

    // 核心业务：向指定日期添加菜品
    suspend fun addDishToDate(date: String, dishId: Long) {
        // 1. 确保这一天的记录存在
        dao.insertMealDate(MealDateEntity(date))
        // 2. 建立关联
        dao.insertMealDateDishCrossRef(MealDateDishCrossRef(date, dishId))
    }

    //删除某天的某道菜
    suspend fun deleteDishFromDate(date: String, dishId: Long) {
        dao.deleteMealDateDishCrossRef(MealDateDishCrossRef(date, dishId))
    }
}