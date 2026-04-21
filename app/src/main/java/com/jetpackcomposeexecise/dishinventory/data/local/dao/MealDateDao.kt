package com.jetpackcomposeexecise.dishinventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    // 插入一个日期记录
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDate(mealDate: MealDateEntity)

    //增：给某天绑定一道菜(插入交叉表)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealDateDishCrossRef(crossRef: MealDateDishCrossRef)

    //删：移除某天的某道菜
    @Delete
    suspend fun deleteMealDateDishCrossRef(crossRef: MealDateDishCrossRef)
    //删：根据日期和菜品 ID 删除关联
    @Query("DELETE FROM meal_date_dish_cross_ref WHERE mealDate = :date AND dishId = :dishId")
    suspend fun deleteDishFromDate(date: String, dishId: Long)

}