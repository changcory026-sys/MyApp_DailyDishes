package com.jetpackcomposeexecise.dishinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredientsAndMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateWithDishes
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDateDao {

    @Transaction
    @Query("SELECT * FROM meal_dates WHERE mealDate = :date")
    fun getMealDateWithDishesByDate(date: String): Flow<MealDateWithDishes?>

    @Query("""
        SELECT d.*, ref.mealTime 
        FROM dish_table d
        JOIN meal_date_dish_cross_ref ref ON d.dishId = ref.dishId
        WHERE ref.mealDate = :date
    """)
    fun getDishesWithMealTimeByDate(date: String): Flow<List<DishWithMealTime>>

    @Transaction
    @Query("""
        SELECT d.*, ref.mealTime 
        FROM dish_table d
        JOIN meal_date_dish_cross_ref ref ON d.dishId = ref.dishId
        WHERE ref.mealDate = :date
    """)
    fun getDishesWithIngredientsAndMealTimeByDate(date: String): Flow<List<DishWithIngredientsAndMealTime>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDate(mealDate: MealDateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDateDishCrossRef(crossRef: MealDateDishCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDateDishCrossRefs(crossRefs: List<MealDateDishCrossRef>)

    // 👈 核心改进：真正的原子化保存事务
    @Transaction
    suspend fun insertDishesForDateAtomic(date: String, crossRefs: List<MealDateDishCrossRef>) {
        insertMealDate(MealDateEntity(date))
        insertMealDateDishCrossRefs(crossRefs)
    }

    @Delete
    suspend fun deleteMealDateDishCrossRef(crossRef: MealDateDishCrossRef)

    @Query("DELETE FROM meal_date_dish_cross_ref WHERE mealDate = :date AND dishId = :dishId AND mealTime = :mealTime")
    suspend fun deleteDishFromDate(date: String, dishId: Long, mealTime: String)
}
