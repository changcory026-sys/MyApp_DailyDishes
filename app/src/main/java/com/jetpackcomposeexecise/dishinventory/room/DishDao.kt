package com.jetpackcomposeexecise.dishinventory.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    //增
    @Insert
    suspend fun insert(dish: DishItem)
    //删
    @Delete
    suspend fun delete(dish: DishItem)
    //查
    @Query("SELECT * FROM dish_table")
    fun getAllDishes(): Flow<List<DishItem>>
    @Query("SELECT * FROM dish_table WHERE id = :id")
    fun getDishById(id: Long): Flow<DishItem?>
    //改（修改全部字段，主键除外）
    @Update
    suspend fun update(dish: DishItem)
    //改：修改单个字段
    @Query("UPDATE dish_table SET name = :newName WHERE id = :id")
    suspend fun updateName(id: Long, newName: String)
    @Query("UPDATE dish_table SET time = :newPrice WHERE id = :id")
    suspend fun updatePrice(id: Long, newPrice: Double)
    @Query("UPDATE dish_table SET medicine = :newMedicine WHERE id = :id")
    suspend fun updateMedicine(id: Long, newMedicine: String)
    @Query("UPDATE dish_table SET dayTime = :newDayTime WHERE id = :id")
    suspend fun updateDayTime(id: Long, newDayTime: String)
    @Query("UPDATE dish_table SET womanPeriod = :newWomamPeriod WHERE id = :id")
    suspend fun updateWomamPeriod(id: Long, newWomamPeriod: String)

}