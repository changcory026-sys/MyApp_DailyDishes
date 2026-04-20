package com.jetpackcomposeexecise.dishinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    //增
    @Insert
    suspend fun insert(dish: DishEntity)
    //删
    @Delete
    suspend fun delete(dish: DishEntity)
    //查
    @Query("SELECT * FROM dish_table")
    fun getAllDishes(): Flow<List<DishEntity>>
    @Query("SELECT * FROM dish_table WHERE dishId = :id")
    fun getDishById(id: Long): Flow<DishEntity?>
    //改（修改全部字段，主键除外）
    @Update
    suspend fun update(dish: DishEntity)
    //改：修改单个字段
    @Query("UPDATE dish_table SET name = :newName WHERE dishId = :id")
    suspend fun updateName(id: Long, newName: String)
    @Query("UPDATE dish_table SET time = :newPrice WHERE dishId = :id")
    suspend fun updatePrice(id: Long, newPrice: Double)
    @Query("UPDATE dish_table SET medicine = :newMedicine WHERE dishId = :id")
    suspend fun updateMedicine(id: Long, newMedicine: String)
    @Query("UPDATE dish_table SET dayTime = :newDayTime WHERE dishId = :id")
    suspend fun updateDayTime(id: Long, newDayTime: String)
    @Query("UPDATE dish_table SET womanPeriod = :newWomamPeriod WHERE dishId = :id")
    suspend fun updateWomamPeriod(id: Long, newWomamPeriod: String)

}