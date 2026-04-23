package com.jetpackcomposeexecise.dishinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishIngredientCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    //增
    @Insert
    suspend fun insert(dish: DishEntity): Long
    //删
    @Delete
    suspend fun delete(dish: DishEntity)
    //查
    @Query("SELECT * FROM dish_table")
    fun getAllDishes(): Flow<List<DishEntity>>
    @Query("SELECT * FROM dish_table WHERE dishId = :id")
    fun getDishById(id: Long): Flow<DishEntity?>
    //改
    @Update
    suspend fun update(dish: DishEntity)
    
    //改：修改单个字段
    @Query("UPDATE dish_table SET name = :newName WHERE dishId = :id")
    suspend fun updateName(id: Long, newName: String)
    @Query("UPDATE dish_table SET time = :newPrice WHERE dishId = :id")
    suspend fun updatePrice(id: Long, newPrice: String)
    @Query("UPDATE dish_table SET medicine = :newMedicine WHERE dishId = :id")
    suspend fun updateMedicine(id: Long, newMedicine: String)
    @Query("UPDATE dish_table SET womanPeriod = :newWomamPeriod WHERE dishId = :id")
    suspend fun updateWomamPeriod(id: Long, newWomamPeriod: String)

    // --- 多对多：Dish 和 Ingredient 关联操作 ---

    @Transaction
    @Query("SELECT * FROM dish_table WHERE dishId = :dishId")
    fun getDishWithIngredients(dishId: Long): Flow<DishWithIngredients?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDishIngredientCrossRef(crossRef: DishIngredientCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDishIngredientCrossRefs(crossRefs: List<DishIngredientCrossRef>)

    @Query("DELETE FROM dish_ingredient_cross_ref WHERE dishId = :dishId")
    suspend fun deleteIngredientsByDishId(dishId: Long)

    @Transaction
    suspend fun syncIngredients(dishId: Long, ingredientIds: List<Long>) {
        deleteIngredientsByDishId(dishId)
        val refs = ingredientIds.map { DishIngredientCrossRef(dishId, it) }
        insertDishIngredientCrossRefs(refs)
    }

    @Query("DELETE FROM dish_ingredient_cross_ref WHERE dishId = :dishId AND ingredientId = :ingredientId")
    suspend fun removeIngredientFromDish(dishId: Long, ingredientId: Long)
}
