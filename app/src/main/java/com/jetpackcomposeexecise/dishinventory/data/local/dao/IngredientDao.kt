package com.jetpackcomposeexecise.dishinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    // 插入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ingredient: IngredientEntity)

    // 删除
    @Delete
    suspend fun delete(ingredient: IngredientEntity)

    // 查询全部
    @Query("SELECT * FROM ingredient_table ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    // 查询指定 ID
    @Query("SELECT * FROM ingredient_table WHERE ingredientId = :id")
    fun getIngredientById(id: Long): Flow<IngredientEntity?>

    // 修改整个 Ingredient
    @Update
    suspend fun update(ingredient: IngredientEntity)

    // 修改指定 ID 的单个属性
    @Query("UPDATE ingredient_table SET name = :name WHERE ingredientId = :id")
    suspend fun updateName(id: Long, name: String)

    @Query("UPDATE ingredient_table SET price = :price WHERE ingredientId = :id")
    suspend fun updatePrice(id: Long, price: Double)

    @Query("UPDATE ingredient_table SET type = :type WHERE ingredientId = :id")
    suspend fun updateType(id: Long, type: String)

    @Query("UPDATE ingredient_table SET medicine = :medicine WHERE ingredientId = :id")
    suspend fun updateMedicine(id: Long, medicine: String)

    @Query("UPDATE ingredient_table SET womanPeriod = :womanPeriod WHERE ingredientId = :id")
    suspend fun updateWomanPeriod(id: Long, womanPeriod: String)
}