package com.jetpackcomposeexecise.dishinventory.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jetpackcomposeexecise.dishinventory.data.local.dao.DishDao
import com.jetpackcomposeexecise.dishinventory.data.local.dao.MealDateDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateEntity

// 创建&更新 entities 数组，并升级 version
@Database(
    entities = [DishEntity::class, MealDateEntity::class, MealDateDishCrossRef::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DishDao(): DishDao // v1
    abstract fun mealDateDao(): MealDateDao // v2：暴露新的 Dao
}