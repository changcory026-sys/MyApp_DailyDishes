package com.jetpackcomposeexecise.dishinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateWithDishes
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDateDao {

    // 查询某天的所有菜。必须加 @Transaction
    @Transaction
    @Query("SELECT * FROM meal_dates WHERE mealDate = :date")
    fun getMealDateWithDishesByDate(date: String): Flow<MealDateWithDishes?>

    // 新增：查询某天的所有菜及其用餐时段 (JOIN 查询)
    @Query("""
        SELECT d.*, ref.mealTime 
        FROM dish_table d
        JOIN meal_date_dish_cross_ref ref ON d.dishId = ref.dishId
        WHERE ref.mealDate = :date
    """)
    fun getDishesWithMealTimeByDate(date: String): Flow<List<DishWithMealTime>>

    // 插入一个日期记录
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDate(mealDate: MealDateEntity)

    //增：给某天绑定一道菜(插入交叉表)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDateDishCrossRef(crossRef: MealDateDishCrossRef)

    //删：移除交叉表中的特定记录 (日期+菜品+时段)
    @Delete
    suspend fun deleteMealDateDishCrossRef(crossRef: MealDateDishCrossRef)

    //删：根据日期、菜品 ID 和时段删除关联
    @Query("DELETE FROM meal_date_dish_cross_ref WHERE mealDate = :date AND dishId = :dishId AND mealTime = :mealTime")
    suspend fun deleteDishFromDate(date: String, dishId: Long, mealTime: String)
}